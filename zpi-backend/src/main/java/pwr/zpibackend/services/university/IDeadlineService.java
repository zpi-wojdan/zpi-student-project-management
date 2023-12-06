package pwr.zpibackend.services.university;

import pwr.zpibackend.dto.university.DeadlineDTO;
import pwr.zpibackend.models.university.Deadline;

import java.util.List;

public interface IDeadlineService {
    List<Deadline> getAllDeadlines();
    List<Deadline> getAllDeadlinesOrderedByDateAsc();
    Deadline getDeadline(Long deadlineId);
    Deadline addDeadline(DeadlineDTO deadline);
    Deadline updateDeadline(Long deadlineId, DeadlineDTO updatedDeadline);
    Deadline deleteDeadline(Long deadlineId);
}
