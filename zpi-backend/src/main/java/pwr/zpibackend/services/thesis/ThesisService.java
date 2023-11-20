package pwr.zpibackend.services.thesis;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Comment;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.repositories.thesis.CommentRepository;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.user.StudentRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ThesisService {

    private final ThesisRepository thesisRepository;
    private final EmployeeRepository employeeRepository;
    private final StatusRepository statusRepository;
    private final CommentRepository commentRepository;

    public List<Thesis> getAllTheses() {
        return thesisRepository.findAll();
    }

    public Thesis getThesis(Long id) {
        return thesisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public Thesis addThesis(Thesis thesis) {
        Employee supervisor = employeeRepository
                .findById(thesis.getSupervisor().getId())
                .orElseThrow(NotFoundException::new);

        Thesis newThesis = new Thesis();
        newThesis.setNamePL(thesis.getNamePL());
        newThesis.setNameEN(thesis.getNameEN());
        newThesis.setDescriptionPL(thesis.getDescriptionPL());
        newThesis.setDescriptionEN(thesis.getDescriptionEN());
        newThesis.setNumPeople(thesis.getNumPeople());
        newThesis.setSupervisor(supervisor);
        newThesis.setPrograms(thesis.getPrograms());
        newThesis.setStudyCycle(thesis.getStudyCycle());
        newThesis.setStatus(thesis.getStatus());
        newThesis.setOccupied(0);

        thesisRepository.saveAndFlush(newThesis);
        return thesis;
    }

    public Thesis updateThesis(Long id, Thesis param) {
        if (thesisRepository.existsById(id)) {
            Thesis updated = thesisRepository.findById(id).get();
            updated.setNamePL(param.getNamePL());
            updated.setNameEN(param.getNameEN());
            updated.setDescriptionPL(param.getDescriptionPL());
            updated.setDescriptionEN(param.getDescriptionEN());
            updated.setNumPeople(param.getNumPeople());

            if (employeeRepository.existsById(param.getSupervisor().getId())) {
                Employee supervisor = employeeRepository.findById(param.getSupervisor().getId()).get();
                updated.setSupervisor(supervisor);
            }
            else{
                throw new NotFoundException();
            }

            updated.setPrograms(param.getPrograms());
            updated.setStudyCycle(param.getStudyCycle());
            thesisRepository.saveAndFlush(updated);
            return updated;
        }
        throw new NotFoundException();
    }

    //  brakowało metody do usuwania tematu
    //  co z rozłączaniem z employee/studentem itp? dobrze to jest?
    @Transactional
    public Thesis deleteThesis(Long id) {
        Optional<Thesis> thesis = thesisRepository.findById(id);
        if (thesis.isPresent()){
            Thesis deletedThesis = thesis.get();

            Status status = deletedThesis.getStatus();
            if (status != null){
                status.getTheses().remove(deletedThesis);
                deletedThesis.setStatus(null);
            }

            deletedThesis.setPrograms(null);
            deletedThesis.setSupervisor(null);
            deletedThesis.setLeader(null);
            deletedThesis.setStudyCycle(null);
            deletedThesis.setReservations(null);

            List<Comment> comments = deletedThesis.getComments();
            if (comments != null) {
                comments.forEach(comment -> commentRepository.deleteById(comment.getId()));
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
