package pwr.zpibackend.services.thesis;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.thesis.ThesisDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Comment;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.repositories.thesis.CommentRepository;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ThesisService {

    private final ThesisRepository thesisRepository;
    private final EmployeeRepository employeeRepository;
    private final ProgramRepository programRepository;
    private final StudyCycleRepository studyCycleRepository;
    private final StatusRepository statusRepository;
    private final CommentRepository commentRepository;

    public List<Thesis> getAllTheses() {
        return thesisRepository.findAll();
    }

    public Thesis getThesis(Long id) {
        return thesisRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Thesis with id " + id + " does not exist"));
    }

    public Thesis addThesis(ThesisDTO thesis) {
        Employee supervisor = employeeRepository
                .findById(thesis.getSupervisorId())
                .orElseThrow(
                        () -> new NotFoundException("Employee with id " + thesis.getSupervisorId() + " does not exist")
                );

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

        newThesis.setStudyCycle(thesis.getStudyCycleId().map(index ->
                studyCycleRepository.findById(index).orElseThrow(NotFoundException::new)
        ).orElse(null));

        newThesis.setStatus(statusRepository.findById(thesis.getStatusId()).orElseThrow(NotFoundException::new));
        newThesis.setOccupied(0);

        thesisRepository.saveAndFlush(newThesis);
        return newThesis;
    }

    public Thesis updateThesis(Long id, ThesisDTO thesis) {
        if (thesisRepository.existsById(id)) {
            Thesis updated = thesisRepository.findById(id).get();
            updated.setNamePL(thesis.getNamePL());
            updated.setNameEN(thesis.getNameEN());
            updated.setDescriptionPL(thesis.getDescriptionPL());
            updated.setDescriptionEN(thesis.getDescriptionEN());
            updated.setNumPeople(thesis.getNumPeople());

            updated.setSupervisor(employeeRepository.findById(
                    thesis.getSupervisorId()).orElseThrow(
                    () -> new NotFoundException("Employee with id " + thesis.getSupervisorId() + " does not exist"))
            );
            List<Program> programList = new ArrayList<>();
            thesis.getProgramIds().forEach(programId -> {
                Program program = programRepository.findById(programId).orElseThrow(
                        () -> new NotFoundException("Program with id " + programId + " does not exist"));
                programList.add(program);
            });
            updated.setPrograms(programList);

            updated.setStudyCycle(thesis.getStudyCycleId().map(index ->
                    studyCycleRepository.findById(index).orElseThrow(NotFoundException::new)
            ).orElse(null));

            updated.setStatus(statusRepository.findById(thesis.getStatusId()).orElseThrow(NotFoundException::new));
            thesisRepository.saveAndFlush(updated);
            return updated;
        }
        throw new NotFoundException("Thesis with id " + id + " does not exist");
    }

    //  brakowało metody do usuwania tematu
    //  co z rozłączaniem z employee/studentem itp? dobrze to jest?
    @Transactional
    public Thesis deleteThesis(Long id) {
        Optional<Thesis> thesisOptional = thesisRepository.findById(id);
        if (thesisOptional.isPresent()) {
            Thesis deletedThesis = thesisOptional.get();

            Status status = deletedThesis.getStatus();
            if (status != null) {
                List<Thesis> theses = status.getTheses();
                if (theses != null) {
                    theses.remove(deletedThesis);
                }
                deletedThesis.setStatus(null);
            }

            deletedThesis.setPrograms(null);
            deletedThesis.setSupervisor(null);
            deletedThesis.setLeader(null);
            deletedThesis.setStudyCycle(null);

            List<Comment> comments = deletedThesis.getComments();
            if (comments != null) {
                comments.forEach(comment -> {
                    Long commentId = comment.getId();
                    if (commentId != null && commentRepository.existsById(commentId)) {
                        commentRepository.deleteById(commentId);
                    }
                });
            }
            deletedThesis.setComments(null);

            thesisRepository.deleteById(id);
            return deletedThesis;
        }
        throw new NotFoundException("Thesis with id " + id + " does not exist");
    }


    //  np na zwrócenie: wszystkich zaakceptowanych, wszystkich archiwalnych itp
    public List<Thesis> getAllThesesByStatusName(String name) {
        return thesisRepository.findAllByStatusName(name);
    }

    //  np na zwrócenie wszystkich tematów, które nie są draftami
    public List<Thesis> getAllThesesExcludingStatusName(String name){
        Optional<Status> excludedStatus = statusRepository.findByName(name);
        if (excludedStatus.isEmpty()) {
            throw new NotFoundException("Status with name " + name + " does not exist");
        }
        return thesisRepository.findAll().stream()
                .filter(thesis -> !name.equals(thesis.getStatus().getName()))
                .collect(Collectors.toList());
    }

    //  np na zwrócenie wszystkich draftów danego pracownika
    public List<Thesis> getAllThesesForEmployeeByStatusId(Long empId, String statName) {
        return thesisRepository.findAllByEmployeeIdAndStatusName(empId, statName);
    }

    //  np na zwrócenie wszystkich tematów danego pracownika
    public List<Thesis> getAllThesesForEmployee(Long id) {
        return thesisRepository.findAllByEmployeeId(id);
    }

}
