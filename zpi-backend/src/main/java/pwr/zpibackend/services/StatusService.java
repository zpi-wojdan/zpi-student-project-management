package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Status;
import pwr.zpibackend.dto.StatusDTO;
import pwr.zpibackend.repositories.StatusRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;

    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    public Status getStatus(Long statusId) {
        return statusRepository.findById(statusId).orElseThrow(
                () -> new NoSuchElementException("Status with id " + statusId + " does not exist")
        );
    }

    public Status addStatus(StatusDTO status) {
        if (statusRepository.existsByName(status.getName())) {
            throw new IllegalArgumentException("Status with name " + status.getName() + " already exists");
        }
        Status newStatus = new Status(status.getName());
        return statusRepository.save(newStatus);
    }

    public Status updateStatus(Long statusId, StatusDTO updatedStatus) {
        Status status = statusRepository.findById(statusId).orElse(null);
        if (status != null) {
            status.setName(updatedStatus.getName());
            return statusRepository.save(status);
        }
        throw new NoSuchElementException("Status with id " + statusId + " does not exist");
    }

    public Status deleteStatus(Long statusId) {
        Status status = statusRepository.findById(statusId).orElse(null);
        if (status != null) {
            statusRepository.delete(status);
            return status;
        } else {
            throw new NoSuchElementException("Status with id " + statusId + " does not exist");
        }
    }

    public Status getStatusByName(String name) {
        return statusRepository.findByName(name).orElseThrow(
                () -> new NoSuchElementException("Status with name " + name + " does not exist")
        );
    }

}
