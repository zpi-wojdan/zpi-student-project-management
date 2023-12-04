package pwr.zpibackend.controllers.thesis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.dto.thesis.StatusDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.services.thesis.StatusService;
import pwr.zpibackend.services.user.EmployeeService;
import pwr.zpibackend.services.user.StudentService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatusController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StatusControllerTests {

    private static final String BASE_URL = "/api/status";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private StatusService statusService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private StatusController statusController;

    private List<Status> statuses;
    private Status status;
    private StatusDTO statusDTO;

    @BeforeEach
    public void setUp() {
        status = new Status();
        status.setId(1L);
        status.setName("Draft");

        statusDTO = new StatusDTO();
        statusDTO.setName("Draft");

        statuses = new ArrayList<>();
        statuses.add(status);
    }

    @Test
    public void testGetAllStatuses() throws Exception {
        Mockito.when(statusService.getAllStatuses()).thenReturn(statuses);

        String returnedJson = objectMapper.writeValueAsString(statuses);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(statusService).getAllStatuses();
    }

    @Test
    public void testGetStatusByName() throws Exception {
        String statusName = "Draft";
        Mockito.when(statusService.getStatusByName(statusName)).thenReturn(status);

        String returnedJson = objectMapper.writeValueAsString(status);

        mockMvc.perform(get(BASE_URL + "/{name}", statusName).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(statusService).getStatusByName(statusName);
    }

    @Test
    public void testGetStatusByNameNotFound() throws Exception {
        String statusName = "not found status";
        Mockito.when(statusService.getStatusByName(statusName)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{name}", statusName).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(statusService).getStatusByName(statusName);
    }

    @Test
    public void testGetAllStatusesWithoutName() throws Exception {
        String excludedName = "Draft";
        Mockito.when(statusService.getAllStatusesWithoutName(excludedName)).thenReturn(statuses);

        String returnedJson = objectMapper.writeValueAsString(statuses);

        mockMvc.perform(get(BASE_URL + "/exclude/{name}", excludedName).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(statusService).getAllStatusesWithoutName(excludedName);
    }

    @Test
    public void testAddStatus() throws Exception {
        Mockito.when(statusService.addStatus(statusDTO)).thenReturn(status);

        String requestBody = objectMapper.writeValueAsString(statusDTO);
        String responseBody = objectMapper.writeValueAsString(status);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(statusService).addStatus(statusDTO);
    }

    @Test
    public void testAddStatusAlreadyExists() throws Exception {
        Mockito.when(statusService.addStatus(statusDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(statusDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(statusService).addStatus(statusDTO);
    }

    @Test
    public void testUpdateStatus() throws Exception {
        Long statusId = 1L;
        statusDTO.setName("updated");

        Mockito.when(statusService.updateStatus(statusId, statusDTO)).thenReturn(status);

        String requestBody = objectMapper.writeValueAsString(statusDTO);
        String responseBody = objectMapper.writeValueAsString(status);

        mockMvc.perform(put(BASE_URL + "/{statusId}", statusId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(statusService).updateStatus(statusId, statusDTO);
    }

    @Test
    public void testUpdateStatusNotFound() throws Exception {
        Long statusId = 1L;

        Mockito.when(statusService.updateStatus(statusId, statusDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(statusDTO);

        mockMvc.perform(put(BASE_URL + "/{statusId}", statusId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(statusService).updateStatus(statusId, statusDTO);
    }

    @Test
    public void testUpdateStatusAlreadyExists() throws Exception {
        Long statusId = 1L;
        statusDTO.setName("active");

        Mockito.when(statusService.updateStatus(statusId, statusDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(statusDTO);

        mockMvc.perform(put(BASE_URL + "/{statusId}", statusId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(statusService).updateStatus(statusId, statusDTO);
    }

    @Test
    public void testDeleteStatus() throws Exception {
        Long statusId = 1L;

        Mockito.when(statusService.deleteStatus(statusId)).thenReturn(status);

        String returnedJson = objectMapper.writeValueAsString(status);

        mockMvc.perform(delete(BASE_URL + "/{statusId}", statusId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(statusService).deleteStatus(statusId);
    }

    @Test
    public void testDeleteStatusNotFound() throws Exception {
        Long statusId = 2L;

        Mockito.when(statusService.deleteStatus(statusId)).thenThrow(new NotFoundException());

        mockMvc.perform(delete(BASE_URL + "/{statusId}", statusId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(statusService).deleteStatus(statusId);
    }
}
