package pwr.zpibackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.controllers.university.DeadlineController;
import pwr.zpibackend.dto.university.DeadlineDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Deadline;
import pwr.zpibackend.services.impl.university.DeadlineService;
import pwr.zpibackend.services.impl.user.EmployeeService;
import pwr.zpibackend.services.impl.user.StudentService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeadlineController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DeadlineControllerTests {

    private static final String BASE_URL = "/api/deadline";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private DeadlineService deadlineService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private DeadlineController deadlineController;

    private List<Deadline> deadlines;
    private List<Deadline> orderedDeadlines;
    private Deadline deadline;
    private DeadlineDTO deadlineDTO;

    @BeforeEach
    public void setUp() {
        deadline = new Deadline();
        deadline.setId(1L);
        deadline.setNamePL("Czynnosc 1");
        deadline.setNameEN("Activity 1");
        deadline.setDeadlineDate(LocalDate.now());

        deadlineDTO = new DeadlineDTO();
        deadlineDTO.setNamePL("Czynnosc 1");
        deadlineDTO.setNameEN("Activity 1");
        deadlineDTO.setDeadlineDate(LocalDate.now());

        Deadline deadline2 = new Deadline();
        deadline2.setId(2L);
        deadline2.setNamePL("Czynnosc 2");
        deadline2.setNameEN("Activity 2");
        deadline2.setDeadlineDate(LocalDate.now());

        deadlines = List.of(deadline, deadline2);
        orderedDeadlines = List.of(deadline2, deadline);
    }

    @Test
    public void testGetAllDeadlines() throws Exception {
        when(deadlineService.getAllDeadlines()).thenReturn(deadlines);

        String returnedJson = objectMapper.writeValueAsString(deadlines);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(deadlineService).getAllDeadlines();
    }

    @Test
    public void testGetAllDeadlinesOrderedByDate() throws Exception {
        when(deadlineService.getAllDeadlinesOrderedByDateAsc()).thenReturn(orderedDeadlines);

        String returnedJson = objectMapper.writeValueAsString(orderedDeadlines);

        mockMvc.perform(get(BASE_URL + "/ordered").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(deadlineService).getAllDeadlinesOrderedByDateAsc();
    }

    @Test
    public void testGetDeadlineById() throws Exception {
        Long deadlineId = 1L;
        when(deadlineService.getDeadline(deadlineId)).thenReturn(deadline);

        String returnedJson = objectMapper.writeValueAsString(deadline);

        mockMvc.perform(get(BASE_URL + "/{id}", deadlineId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(deadlineService).getDeadline(deadlineId);
    }

    @Test
    public void testGetDeadlineByIdNotFound() throws Exception {
        Long deadlineId = 1L;
        when(deadlineService.getDeadline(deadlineId)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{id}", deadlineId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(deadlineService).getDeadline(deadlineId);
    }

    @Test
    public void testAddDeadline() throws Exception {
        String requestBody = objectMapper.writeValueAsString(deadlineDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(deadlineService).addDeadline(deadlineDTO);
    }

    @Test
    public void testAddDeadlineAlreadyExists() throws Exception {
        when(deadlineService.addDeadline(deadlineDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(deadlineDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(deadlineService).addDeadline(deadlineDTO);
    }

    @Test
    public void testAddDeadlineDateInPast() throws Exception {
        deadlineDTO.setDeadlineDate(LocalDate.of(2000, 11, 20));
        when(deadlineService.addDeadline(deadlineDTO)).thenThrow(new IllegalArgumentException());

        String requestBody = objectMapper.writeValueAsString(deadlineDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(deadlineService).addDeadline(deadlineDTO);
    }

    @Test
    public void testUpdateDeadline() throws Exception {
        Long deadlineId = 1L;
        deadlineDTO.setNamePL("Czynnosc 1 updated");
        deadline.setNamePL("Czynnosc 1 updated");
        when(deadlineService.updateDeadline(deadlineId, deadlineDTO)).thenReturn(deadline);

        String requestBody = objectMapper.writeValueAsString(deadlineDTO);
        String responseBody = objectMapper.writeValueAsString(deadline);

        mockMvc.perform(put(BASE_URL + "/{id}", deadlineId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(deadlineService).updateDeadline(deadlineId, deadlineDTO);
    }

    @Test
    public void testUpdateDeadlineNotFound() throws Exception {
        Long deadlineId = 1L;
        when(deadlineService.updateDeadline(deadlineId, deadlineDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(deadlineDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", deadlineId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(deadlineService).updateDeadline(deadlineId, deadlineDTO);
    }

    @Test
    public void testUpdateDeadlineAlreadyExists() throws Exception {
        Long deadlineId = 1L;
        deadlineDTO.setNamePL("Czynnosc 2");
        when(deadlineService.updateDeadline(deadlineId, deadlineDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(deadlineDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", deadlineId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(deadlineService).updateDeadline(deadlineId, deadlineDTO);
    }

    @Test
    public void testUpdateDeadlineDateInPast() throws Exception {
        Long deadlineId = 1L;
        deadlineDTO.setDeadlineDate(LocalDate.of(2000, 11, 20));
        when(deadlineService.updateDeadline(deadlineId, deadlineDTO)).thenThrow(new IllegalArgumentException());

        String requestBody = objectMapper.writeValueAsString(deadlineDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", deadlineId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(deadlineService).updateDeadline(deadlineId, deadlineDTO);
    }

    @Test
    public void testDeleteDeadline() throws Exception {
        Long deadlineId = 1L;
        when(deadlineService.deleteDeadline(deadlineId)).thenReturn(deadline);

        String returnedJson = objectMapper.writeValueAsString(deadline);

        mockMvc.perform(delete(BASE_URL + "/{id}", deadlineId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(deadlineService).deleteDeadline(deadlineId);
    }

    @Test
    public void testDeleteDeadlineNotFound() throws Exception {
        Long deadlineId = 1000L;
        when(deadlineService.deleteDeadline(deadlineId)).thenThrow(new NotFoundException());

        mockMvc.perform(delete(BASE_URL + "/{id}", deadlineId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(deadlineService).deleteDeadline(deadlineId);
    }
}
