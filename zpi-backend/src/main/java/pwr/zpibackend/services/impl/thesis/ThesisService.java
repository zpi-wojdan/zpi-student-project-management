package pwr.zpibackend.services.impl.thesis;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pwr.zpibackend.dto.thesis.ThesisDTO;
import pwr.zpibackend.exceptions.LimitOfThesesReachedException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.repositories.thesis.CommentRepository;
import pwr.zpibackend.repositories.thesis.ReservationRepository;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.repositories.user.StudentRepository;
import pwr.zpibackend.services.mailing.IMailService;
import pwr.zpibackend.services.thesis.IThesisService;
import pwr.zpibackend.utils.MailTemplates;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ThesisService implements IThesisService {

    private final ThesisRepository thesisRepository;
    private final EmployeeRepository employeeRepository;
    private final ProgramRepository programRepository;
    private final StudyCycleRepository studyCycleRepository;
    private final StatusRepository statusRepository;
    private final CommentRepository commentRepository;
    private final StudentRepository studentRepository;
    private final ReservationRepository reservationRepository;
    private final IMailService mailService;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "studyCycle.name", "id");

    public List<Thesis> getAllTheses() {
        return thesisRepository.findAllByOrderByStudyCycleNameDescIdDesc();
    }

    public List<Thesis> getAllPublicTheses() {
        return thesisRepository.findAllByStatusNameIn(Arrays.asList("Approved", "Assigned", "Closed"), sort);
    }

    public Thesis getThesis(Long id) {
        return thesisRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Thesis with id " + id + " does not exist"));
    }

    public Thesis getThesisByStudentId(Long studentId) {
        Reservation reservation = reservationRepository.findByStudentId(studentId);

        if (reservation == null) {
            throw new NotFoundException("Reservation for student with id " + studentId + " not found");
        }

        return thesisRepository.findByReservations_Id(reservation.getId())
                .orElseThrow(() -> new NotFoundException("Thesis for student with id " + studentId + " not found"));
    }

    @Transactional
    public Thesis addThesis(ThesisDTO thesis) {
        Employee supervisor = employeeRepository
                .findById(thesis.getSupervisorId())
                .orElseThrow(
                        () -> new NotFoundException(
                                "Employee with id " + thesis.getSupervisorId() + " does not exist"));

        Status draftStatus = statusRepository.findByName("Draft").orElseThrow(NotFoundException::new);
        Status rejectedStatus = statusRepository.findByName("Rejected").orElseThrow(NotFoundException::new);
        Status closedStatus = statusRepository.findByName("Closed").orElseThrow(NotFoundException::new);

        if ((!thesis.getStatusId().equals(draftStatus.getId()) &&
                !thesis.getStatusId().equals(rejectedStatus.getId()) &&
                !thesis.getStatusId().equals(closedStatus.getId())) &&
                thesisRepository.findAllBySupervisor_IdAndStatus_NameIn(supervisor.getId(),
                                Arrays.asList("Pending approval", "Approved", "Assigned"), sort)
                        .size() >= supervisor.getNumTheses()) {
            throw new LimitOfThesesReachedException("Employee with id " + thesis.getSupervisorId() +
                    " has reached the limit of theses");
        }

        Thesis newThesis = new Thesis();
        newThesis.setNamePL(thesis.getNamePL());
        newThesis.setNameEN(thesis.getNameEN());
        newThesis.setDescriptionPL(thesis.getDescriptionPL());
        newThesis.setDescriptionEN(thesis.getDescriptionEN());
        newThesis.setNumPeople(thesis.getNumPeople());
        newThesis.setSupervisor(supervisor);
        newThesis.setPrograms(new ArrayList<>());
        thesis.getProgramIds().forEach(programId -> {
            Program program = programRepository.findById(programId).orElseThrow(
                    () -> new NotFoundException("Program with id " + programId + " does not exist"));
            newThesis.getPrograms().add(program);
        });

        newThesis.setStudyCycle(thesis.getStudyCycleId()
                .map(index -> studyCycleRepository.findById(index).orElseThrow(NotFoundException::new)).orElse(null));

        newThesis.setStatus(draftStatus);

        thesisRepository.saveAndFlush(newThesis);

        try {
            if (!thesis.getStudentIndexes().isEmpty() && !thesis.getStatusId().equals(draftStatus.getId())) {
                for (String index : thesis.getStudentIndexes()) {
                    Student student = studentRepository.findByIndex(index)
                            .orElseThrow(
                                    () -> new NotFoundException("Student with index " + index + " does not exist"));
                    if (reservationRepository.findByStudent_Mail(student.getMail()) != null) {
                        throw new IllegalArgumentException(
                                "Student with index " + index + " already has a reservation");
                    } else {
                        Reservation reservation = new Reservation();
                        reservation.setStudent(student);
                        reservation.setThesis(newThesis);
                        reservation.setConfirmedByLeader(false);
                        reservation.setConfirmedBySupervisor(true);
                        reservation.setConfirmedByStudent(false);
                        reservation.setReadyForApproval(true);
                        reservation.setReservationDate(LocalDateTime.now());
                        reservation.setSentForApprovalDate(LocalDateTime.now());
                        reservationRepository.saveAndFlush(reservation);
                    }
                }
                newThesis.setOccupied(thesis.getNumPeople());
            }
        } catch (Exception e) {
            for (String index : thesis.getStudentIndexes()) {
                Student student = studentRepository.findByIndex(index)
                        .orElseThrow(() -> new NotFoundException("Student with index " + index + "does not exist"));
                Reservation reservation = reservationRepository.findByStudent_Mail(student.getMail());
                if (reservation != null) {
                    reservationRepository.delete(reservation);
                }
            }
            thesisRepository.delete(newThesis);
            throw e;
        }

        for (String index : thesis.getStudentIndexes()) {
            Student student = studentRepository.findByIndex(index)
                    .orElseThrow(() -> new NotFoundException("Student with index " + index + "does not exist"));
            mailService.sendHtmlMailMessage(student.getMail(), MailTemplates.RESERVATION_SUPERVISOR,
                    student, supervisor, newThesis);
        }
        newThesis.setStatus(statusRepository.findById(thesis.getStatusId()).orElseThrow(NotFoundException::new));

        thesisRepository.saveAndFlush(newThesis);
        return newThesis;
    }

    @Transactional
    public Thesis updateThesis(Long id, ThesisDTO thesis) {
        if (thesisRepository.existsById(id)) {
            Thesis updated = thesisRepository.findById(id).get();

            Status draftStatus = statusRepository.findByName("Draft").orElseThrow(NotFoundException::new);
            Status rejectedStatus = statusRepository.findByName("Rejected").orElseThrow(NotFoundException::new);
            Status closedStatus = statusRepository.findByName("Closed").orElseThrow(NotFoundException::new);

            if ((updated.getStatus().getName().equals("Draft") || updated.getStatus().getName().equals("Rejected") ||
                    updated.getStatus().getName().equals("Closed")) &&
                    (!thesis.getStatusId().equals(draftStatus.getId()) &&
                            !thesis.getStatusId().equals(rejectedStatus.getId()) &&
                            !thesis.getStatusId().equals(closedStatus.getId()))
                    &&
                    thesisRepository.findAllBySupervisor_IdAndStatus_NameIn(updated.getSupervisor().getId(),
                                    Arrays.asList("Pending approval", "Approved", "Assigned"), sort)
                            .size() >= updated.getSupervisor().getNumTheses()) {
                throw new LimitOfThesesReachedException("Employee with id " + updated.getSupervisor().getId() +
                        " has reached the limit of theses");
            }

            updated.setNamePL(thesis.getNamePL());
            updated.setNameEN(thesis.getNameEN());
            updated.setDescriptionPL(thesis.getDescriptionPL());
            updated.setDescriptionEN(thesis.getDescriptionEN());
            updated.setNumPeople(thesis.getNumPeople());

            updated.setSupervisor(employeeRepository.findById(
                    thesis.getSupervisorId()).orElseThrow(
                    () -> new NotFoundException(
                            "Employee with id " + thesis.getSupervisorId() + " does not exist")));
            List<Program> programList = new ArrayList<>();
            thesis.getProgramIds().forEach(programId -> {
                Program program = programRepository.findById(programId).orElseThrow(
                        () -> new NotFoundException("Program with id " + programId + " does not exist"));
                programList.add(program);
            });
            updated.setPrograms(programList);

            updated.setStudyCycle(thesis.getStudyCycleId()
                    .map(index -> studyCycleRepository.findById(index).orElseThrow(NotFoundException::new))
                    .orElse(null));

            Status status = statusRepository.findById(thesis.getStatusId()).orElseThrow(NotFoundException::new);

            if (status.getName().equals("Rejected")) {
                for (Reservation reservation : updated.getReservations()) {
                    mailService.sendHtmlMailMessage(reservation.getStudent().getMail(),
                            MailTemplates.RESERVATION_CANCELED,
                            reservation.getStudent(), null, updated);

                }
                reservationRepository.deleteAll(updated.getReservations());
                updated.getReservations().clear();
                updated.setOccupied(0);
            } else if (status.getName().equals("Approved") && updated.getOccupied() > 0) {
                boolean allConfirmed = true;
                for (Reservation reservation : updated.getReservations()) {
                    if (!reservation.isConfirmedByStudent() || !reservation.isConfirmedBySupervisor()) {
                        allConfirmed = false;
                        break;
                    }
                }
                if (allConfirmed) {
                    status = statusRepository.findByName("Assigned").orElseThrow(NotFoundException::new);
                }
            }

            updated.setStatus(status);

            thesisRepository.saveAndFlush(updated);
            return updated;
        }
        throw new NotFoundException("Thesis with id " + id + " does not exist");
    }

    // brakowało metody do usuwania tematu
    // co z rozłączaniem z employee/studentem itp? dobrze to jest?
    @Transactional
    public Thesis deleteThesis(Long id) {
        Optional<Thesis> thesisOptional = thesisRepository.findById(id);
        if (thesisOptional.isPresent()) {
            Thesis deletedThesis = thesisOptional.get();

            deletedThesis.setStatus(null);
            deletedThesis.setPrograms(null);
            deletedThesis.getSupervisor().getSupervisedTheses().remove(deletedThesis);
            deletedThesis.setSupervisor(null);
            deletedThesis.setLeader(null);
            deletedThesis.setStudyCycle(null);

            thesisRepository.delete(deletedThesis);
            return deletedThesis;
        }
        throw new NotFoundException("Thesis with id " + id + " does not exist");
    }

    // np na zwrócenie: wszystkich zaakceptowanych, wszystkich archiwalnych itp
    public List<Thesis> getAllThesesByStatusName(String name) {
        return thesisRepository.findAllByStatusName(name, sort);
    }

    // np na zwrócenie wszystkich tematów, które nie są draftami
    public List<Thesis> getAllThesesExcludingStatusName(String name) {
        Optional<Status> excludedStatus = statusRepository.findByName(name);
        if (excludedStatus.isEmpty()) {
            throw new NotFoundException("Status with name " + name + " does not exist");
        }
        return thesisRepository.findAllByOrderByStudyCycleNameDescIdDesc().stream()
                .filter(thesis -> !name.equals(thesis.getStatus().getName()))
                .collect(Collectors.toList());
    }

    // np na zwrócenie wszystkich draftów danego pracownika
    public List<Thesis> getAllThesesForEmployeeByStatusName(Long empId, String statName) {
        return thesisRepository.findAllBySupervisorIdAndStatusName(empId, statName, sort);
    }

    // np na zwrócenie wszystkich tematów danego pracownika
    public List<Thesis> getAllThesesForEmployee(Long id) {
        return thesisRepository.findAllBySupervisorId(id, sort);
    }

    public List<Thesis> getAllThesesForEmployeeByStatusNameList(Long empId, List<String> statNames) {
        return thesisRepository.findAllBySupervisor_IdAndStatus_NameIn(empId, statNames, sort);
    }

    @Transactional
    public List<Thesis> updateThesesStatusInBulk(String statName, List<Long> thesesIds) {
        Status status = statusRepository.findByName(statName).orElseThrow(NotFoundException::new);
        List<Thesis> thesesForUpdate = new ArrayList<>();

        for (Long id : thesesIds) {
            Optional<Thesis> thesisOptional = thesisRepository.findById(id);
            if (thesisOptional.isPresent()) {
                Thesis thesis = thesisOptional.get();

                if (status.getName().equals("Rejected")) {
                    reservationRepository.deleteAll(new ArrayList<>(thesis.getReservations()));
                    thesis.getReservations().clear();
                    thesis.setOccupied(0);
                } else if (status.getName().equals("Approved") && thesis.getOccupied() > 0) {
                    boolean allConfirmed = true;
                    for (Reservation reservation : thesis.getReservations()) {
                        if (!reservation.isConfirmedByStudent() || !reservation.isConfirmedBySupervisor()) {
                            allConfirmed = false;
                            break;
                        }
                    }
                    if (allConfirmed) {
                        status = statusRepository.findByName("Assigned").orElseThrow(NotFoundException::new);
                    }
                }

                thesis.setStatus(status);
                thesesForUpdate.add(thesis);
            } else {
                throw new NotFoundException("Thesis with id " + id + " does not exist");
            }
        }

        return thesisRepository.saveAllAndFlush(thesesForUpdate);
    }

    @Transactional
    public List<Thesis> deleteThesesByStudyCycle(Long cycId) {
        List<Thesis> thesesInCycle = thesisRepository.findAllByStudyCycle_Id(cycId);
        List<Thesis> closedTheses = thesesInCycle.stream()
                .filter(thesis -> "Closed".equals(thesis.getStatus().getName()))
                .toList();

        closedTheses.forEach(thesis -> {
            thesis.getPrograms().size();
            thesis.setSupervisor(null);
            thesis.setLeader(null);
        });

        reservationRepository.deleteAll(closedTheses.stream()
                .flatMap(thesis -> thesis.getReservations().stream())
                .collect(Collectors.toList()));
        commentRepository.deleteAll(closedTheses.stream()
                .flatMap(thesis -> thesis.getComments().stream())
                .collect(Collectors.toList()));
        thesisRepository.deleteAll(closedTheses);
        return closedTheses;
    }

    @Transactional
    public List<Thesis> deleteThesesInBulk(List<Long> thesesIds) {
        List<Thesis> theses = thesisRepository.findAllById(thesesIds);

        //  programy nie fetchują się leniwie, więc musiałem to zrobić ręcznie
        //  a nie chciałem robić FetchType.EAGER w temacie jeśli nie ma konieczności
        //  czyszczę pracowników i studentów, żeby nie usuwali się kaskadowo
        theses.forEach(thesis -> {
            thesis.getPrograms().size();
            thesis.setSupervisor(null);
            thesis.setLeader(null);
        });

        reservationRepository.deleteAll(theses.stream()
                .flatMap(thesis -> thesis.getReservations().stream())
                .collect(Collectors.toList()));
        commentRepository.deleteAll(theses.stream()
                .flatMap(thesis -> thesis.getComments().stream())
                .collect(Collectors.toList()));
        thesisRepository.deleteAll(theses);
        return theses;
    }
}