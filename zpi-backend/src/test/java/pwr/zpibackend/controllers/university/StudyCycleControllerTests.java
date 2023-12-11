package pwr.zpibackend.controllers.university;

import org.springframework.boot.test.mock.mockito.MockBean;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.dto.university.StudyCycleDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.services.impl.university.StudyCycleService;
import pwr.zpibackend.services.impl.user.EmployeeService;
import pwr.zpibackend.services.impl.user.StudentService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import pwr.zpibackend.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudyCycleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StudyCycleControllerTests {

    private static final String BASE_URL = "/api/studycycle";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private StudyCycleService studyCycleService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private StudyCycleController studyCycleController;

    private List<StudyCycle> studyCycles;
    private StudyCycle studyCycle;
    private StudyCycleDTO studyCycleDTO;

    @BeforeEach
    public void setUp() {
        studyCycle = new StudyCycle();
        studyCycle.setId(1L);
        studyCycle.setName("Study Cycle");

        studyCycleDTO = new StudyCycleDTO();
        studyCycleDTO.setName("Study Cycle");

        studyCycles = new ArrayList<>();
        studyCycles.add(studyCycle);
    }

    @Test
    public void testGetAllStudyCycles() throws Exception {
        Mockito.when(studyCycleService.getAllStudyCycles()).thenReturn(studyCycles);

        String returnedJson = objectMapper.writeValueAsString(studyCycles);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(studyCycleService).getAllStudyCycles();
    }

    @Test
    public void testGetStudyCycleById() throws Exception {
        Long studyCycleId = 1L;
        Mockito.when(studyCycleService.getStudyCycleById(studyCycleId)).thenReturn(studyCycle);

        String returnedJson = objectMapper.writeValueAsString(studyCycle);

        mockMvc.perform(get(BASE_URL + "/{id}", studyCycleId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(studyCycleService).getStudyCycleById(studyCycleId);
    }

    @Test
    public void testCreateStudyCycle() throws Exception {
        Mockito.when(studyCycleService.saveStudyCycle(studyCycleDTO)).thenReturn(studyCycle);

        String requestBody = objectMapper.writeValueAsString(studyCycleDTO);
        String responseBody = objectMapper.writeValueAsString(studyCycle);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(studyCycleService).saveStudyCycle(studyCycleDTO);
    }

    @Test
    public void testCreateStudyCycleAlreadyExists() throws Exception {
        Mockito.when(studyCycleService.saveStudyCycle(studyCycleDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(studyCycleDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(studyCycleService).saveStudyCycle(studyCycleDTO);
    }

    @Test
    public void testUpdateStudyCycle() throws Exception {
        Long studyCycleId = 1L;
        studyCycleDTO.setName("Updated Study Cycle");

        Mockito.when(studyCycleService.updateStudyCycle(studyCycleId, studyCycleDTO)).thenReturn(studyCycle);

        String requestBody = objectMapper.writeValueAsString(studyCycleDTO);
        String responseBody = objectMapper.writeValueAsString(studyCycle);

        mockMvc.perform(put(BASE_URL + "/{id}", studyCycleId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(studyCycleService).updateStudyCycle(studyCycleId, studyCycleDTO);
    }

    @Test
    public void testUpdateStudyCycleNotFound() throws Exception {
        Long studyCycleId = 1L;

        Mockito.when(studyCycleService.updateStudyCycle(studyCycleId, studyCycleDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(studyCycleDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", studyCycleId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(studyCycleService).updateStudyCycle(studyCycleId, studyCycleDTO);
    }

    @Test
    public void testUpdateStudyCycleAlreadyExists() throws Exception {
        Long studyCycleId = 1L;

        Mockito.when(studyCycleService.updateStudyCycle(studyCycleId, studyCycleDTO)).thenThrow(
                new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(studyCycleDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", studyCycleId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(studyCycleService).updateStudyCycle(studyCycleId, studyCycleDTO);
    }

    @Test
    public void testDeleteStudyCycle() throws Exception {
        Long studyCycleId = 1L;

        Mockito.when(studyCycleService.deleteStudyCycle(studyCycleId)).thenReturn(studyCycle);

        String returnedJson = objectMapper.writeValueAsString(studyCycle);

        mockMvc.perform(delete(BASE_URL + "/{id}", studyCycleId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(studyCycleService).deleteStudyCycle(studyCycleId);
    }

    @Test
    public void testDeleteStudyCycleNotFound() throws Exception {
        Long studyCycleId = 2L;

        Mockito.when(studyCycleService.deleteStudyCycle(studyCycleId)).thenThrow(new NotFoundException());

        mockMvc.perform(delete(BASE_URL + "/{id}", studyCycleId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(studyCycleService).deleteStudyCycle(studyCycleId);
    }
}
