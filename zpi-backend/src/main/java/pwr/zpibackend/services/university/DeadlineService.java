package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.university.DeadlineDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Deadline;
import pwr.zpibackend.repositories.university.DeadlineRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class DeadlineService {
    private final DeadlineRepository deadlineRepository;

    public List<Deadline> getAllDeadlines() {
        return deadlineRepository.findAll();
    }

    public List<Deadline> getAllDeadlinesOrderedByDateAsc() {
        return deadlineRepository.findAllByOrderByDeadlineDateAsc();
    }

    public Deadline getDeadline(Long deadlineId) {
        return deadlineRepository.findById(deadlineId).orElseThrow(
                () -> new NotFoundException("Deadline with id " + deadlineId + " does not exist")
        );
    }

    public Deadline addDeadline(DeadlineDTO deadline) {
        if(deadlineRepository.existsByNamePL(deadline.getNamePL()) ||
                deadlineRepository.existsByNameEN(deadline.getNameEN())) {
            throw new AlreadyExistsException("Deadline with PL name '" + deadline.getNamePL() +
                    "' or EN name '" + deadline.getNameEN() + "' already exists");
        }
        if(deadline.getDeadlineDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline date cannot be in the past");
        }
        Deadline newDeadline = new Deadline();
        newDeadline.setNamePL(deadline.getNamePL());
        newDeadline.setNameEN(deadline.getNameEN());
        newDeadline.setDeadlineDate(deadline.getDeadlineDate());
        return deadlineRepository.save(newDeadline);
    }

    public Deadline updateDeadline(Long deadlineId, DeadlineDTO updatedDeadline) {
        Deadline deadline = deadlineRepository.findById(deadlineId).orElse(null);
        if (deadline != null) {
            if ((deadlineRepository.existsByNamePL(updatedDeadline.getNamePL()) &&
                    !deadline.getNamePL().equals(updatedDeadline.getNamePL())) ||
                    (deadlineRepository.existsByNameEN(updatedDeadline.getNameEN()) &&
                    !deadline.getNameEN().equals(updatedDeadline.getNameEN()))) {
                throw new AlreadyExistsException("Deadline with PL name '" + updatedDeadline.getNamePL() +
                        "' or EN name '" + updatedDeadline.getNameEN() + "' already exists");
            }
            if(updatedDeadline.getDeadlineDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Deadline date cannot be in the past");
            }
            deadline.setNamePL(updatedDeadline.getNamePL());
            deadline.setNameEN(updatedDeadline.getNameEN());
            deadline.setDeadlineDate(updatedDeadline.getDeadlineDate());
            return deadlineRepository.save(deadline);
        }
        throw new NotFoundException("Deadline with id " + deadlineId + " does not exist");
    }

    public Deadline deleteDeadline(Long deadlineId) {
        Deadline deadline = deadlineRepository.findById(deadlineId).orElse(null);
        if (deadline != null) {
            deadlineRepository.delete(deadline);
            return deadline;
        } else {
            throw new NotFoundException("Deadline with id " + deadlineId + " does not exist");
        }
    }
}
