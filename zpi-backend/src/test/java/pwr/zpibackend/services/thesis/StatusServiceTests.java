package pwr.zpibackend.services.thesis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pwr.zpibackend.dto.thesis.StatusDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.repositories.thesis.StatusRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatusServiceTests {

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusService statusService;

    @Test
    public void testGetAllStatuses() {
        when(statusRepository.findAll()).thenReturn(Arrays.asList(new Status("Test Status 1"), new Status("Test Status 2")));

        List<Status> result = statusService.getAllStatuses();

        verify(statusRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllStatusesWithoutName() {
        String name = "Test Status 1";
        when(statusRepository.findAllByNameNot(name)).thenReturn(Arrays.asList(new Status("Test Status 2")));

        List<Status> result = statusService.getAllStatusesWithoutName(name);

        verify(statusRepository, times(1)).findAllByNameNot(name);
    }

    @Test
    public void testGetStatus() {
        Long statusId = 1L;
        Status status = new Status("Test Status");
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(status));

        Status result = statusService.getStatus(statusId);

        verify(statusRepository, times(1)).findById(statusId);
    }

    @Test
    public void testAddStatus() {
        StatusDTO statusDTO = new StatusDTO("Test Status");
        Status status = new Status("Test Status");
        when(statusRepository.existsByName(statusDTO.getName())).thenReturn(false);
        when(statusRepository.save(any(Status.class))).thenReturn(status);

        Status result = statusService.addStatus(statusDTO);

        verify(statusRepository, times(1)).existsByName(statusDTO.getName());
        verify(statusRepository, times(1)).save(any(Status.class));
    }

    @Test
    public void testUpdateStatus() {
        Long statusId = 1L;
        StatusDTO updatedStatusDTO = new StatusDTO("Updated Test Status");
        Status status = new Status("Test Status");
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(status));
        when(statusRepository.existsByName(updatedStatusDTO.getName())).thenReturn(false);
        when(statusRepository.save(any(Status.class))).thenReturn(status);

        Status result = statusService.updateStatus(statusId, updatedStatusDTO);

        verify(statusRepository, times(1)).findById(statusId);
        verify(statusRepository, times(1)).existsByName(updatedStatusDTO.getName());
        verify(statusRepository, times(1)).save(any(Status.class));
    }

    @Test
    public void testDeleteStatus() {
        Long statusId = 1L;
        Status status = new Status("Test Status");
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(status));
        doNothing().when(statusRepository).delete(status);

        Status result = statusService.deleteStatus(statusId);

        verify(statusRepository, times(1)).findById(statusId);
        verify(statusRepository, times(1)).delete(status);
    }

    @Test
    public void testGetStatusByName() {
        String name = "Test Status";
        Status status = new Status(name);
        when(statusRepository.findByName(name)).thenReturn(Optional.of(status));

        Status result = statusService.getStatusByName(name);

        verify(statusRepository, times(1)).findByName(name);
    }

    @Test
    public void testAddStatus_AlreadyExistsException() {
        StatusDTO statusDTO = new StatusDTO("Test Status");
        when(statusRepository.existsByName(statusDTO.getName())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> statusService.addStatus(statusDTO));
    }
    @Test
    public void testUpdateStatus_NotFoundException() {
        Long statusId = 1L;
        StatusDTO updatedStatusDTO = new StatusDTO("Updated Test Status");
        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> statusService.updateStatus(statusId, updatedStatusDTO));
    }

    @Test
    public void testDeleteStatus_NotFoundException() {
        Long statusId = 1L;
        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> statusService.deleteStatus(statusId));
    }

    @Test
    public void testGetStatusByName_NotFoundException() {
        String name = "Test Status";
        when(statusRepository.findByName(name)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> statusService.getStatusByName(name));
    }
}
