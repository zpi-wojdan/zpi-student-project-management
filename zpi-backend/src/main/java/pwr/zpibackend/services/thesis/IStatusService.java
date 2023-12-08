package pwr.zpibackend.services.thesis;

import pwr.zpibackend.dto.thesis.StatusDTO;
import pwr.zpibackend.models.thesis.Status;

import java.util.List;

public interface IStatusService {
    List<Status> getAllStatuses();
    List<Status> getAllStatusesWithoutName(String name);
    Status getStatus(Long statusId);
    Status addStatus(StatusDTO status);
    Status updateStatus(Long statusId, StatusDTO updatedStatus);
    Status deleteStatus(Long statusId);
    Status getStatusByName(String name);
}
