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
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.repositories.thesis.CommentRepository;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.user.StudentRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
                .orElseThrow(NotFoundException::new);
    }

    public Thesis addThesis(ThesisDTO thesis) {
        Employee supervisor = employeeRepository
                .findById(thesis.getSupervisorId())
                .orElseThrow(NotFoundException::new);

        Thesis newThesis = new Thesis();
        newThesis.setNamePL(thesis.getNamePL());
        newThesis.setNameEN(thesis.getNameEN());
        newThesis.setDescriptionPL(thesis.getDescriptionPL());
        newThesis.setDescriptionEN(thesis.getDescriptionEN());
        newThesis.setNumPeople(thesis.getNumPeople());
        newThesis.setSupervisor(supervisor);
        newThesis.setPrograms(new ArrayList<>());
        thesis.getProgramIds().forEach(programId -> {
            Program program = programRepository.findById(programId).orElseThrow(NotFoundException::new);
            newThesis.getPrograms().add(program);
        });
        newThesis.setStudyCycle(studyCycleRepository.findById(
                thesis.getStudyCycleId()).orElseThrow(NotFoundException::new)
        );
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

            updated.getPrograms().clear();

            updated.setSupervisor(employeeRepository.findById(
                    thesis.getSupervisorId()).orElseThrow(NotFoundException::new)
            );
            List<Program> programList = new ArrayList<>();
            thesis.getProgramIds().forEach(programId -> {
                Program program = programRepository.findById(programId).orElseThrow(NotFoundException::new);
                programList.add(program);
            });
            updated.setPrograms(programList);
            updated.setStudyCycle(studyCycleRepository.findById(
                    thesis.getStudyCycleId()).orElseThrow(NotFoundException::new)
            );
            updated.setStatus(statusRepository.findById(thesis.getStatusId()).orElseThrow(NotFoundException::new));
            thesisRepository.saveAndFlush(updated);
            return updated;
        }
        throw new NotFoundException();
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
        throw new NotFoundException();
    }


    //  np na zwrócenie: wszystkich zaakceptowanych, wszystkich archiwalnych itp
    public List<Thesis> getAllThesesByStatusId(Long id) {
        return thesisRepository.findAllByStatusId(id);
    }

    //  np na zwrócenie wszystkich tematów, które nie są draftami
    public List<Thesis> getAllThesesExcludingStatusId(Long id){
        Optional<Status> excludedStatus = statusRepository.findById(id);
        if (excludedStatus.isEmpty()) {
            throw new NotFoundException();
        }
        return thesisRepository.findAll().stream()
                .filter(thesis -> !id.equals(thesis.getStatus().getId()))
                .collect(Collectors.toList());
    }

    //  np na zwrócenie wszystkich draftów danego pracownika
    public List<Thesis> getAllThesesForEmployeeByStatusId(Long empId, Long statId) {
        return thesisRepository.findAllByEmployeeIdAndStatusName(empId, statId);
    }

    //  np na zwrócenie wszystkich tematów danego pracownika
    public List<Thesis> getAllThesesForEmployee(Long id) {
        return thesisRepository.findAllByEmployeeId(id);
    }

}
