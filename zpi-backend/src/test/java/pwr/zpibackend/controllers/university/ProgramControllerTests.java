package pwr.zpibackend.controllers.university;

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
import pwr.zpibackend.dto.university.ProgramDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.university.*;
import pwr.zpibackend.services.impl.university.ProgramService;
import pwr.zpibackend.services.impl.user.EmployeeService;
import pwr.zpibackend.services.impl.user.StudentService;
import pwr.zpibackend.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProgramController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProgramControllerTests {

    private static final String BASE_URL = "/api/program";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private ProgramService programService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private ProgramController programController;

    private List<Program> programs;
    private Program program;
    private ProgramDTO programDTO;

    @BeforeEach
    public void setUp() {
        StudyField studyField = new StudyField();
        studyField.setId(1L);
        studyField.setAbbreviation("SF");
        studyField.setName("Study Field");

        Specialization specialization = new Specialization();
        specialization.setId(1L);
        specialization.setAbbreviation("SP");
        specialization.setName("Specialization");

        StudyCycle studyCycle = new StudyCycle();
        studyCycle.setId(1L);
        studyCycle.setName("Study Cycle");

        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setAbbreviation("FAC");
        faculty.setName("Faculty");

        program = new Program();
        program.setId(1L);
        program.setName("Program Name");
        program.setStudyField(studyField);
        program.setSpecialization(specialization);
        program.setStudyCycles(Collections.singletonList(studyCycle));
        program.setFaculty(faculty);

        programDTO = new ProgramDTO();
        programDTO.setName("Program Name");
        programDTO.setStudyFieldAbbr("SF");
        programDTO.setSpecializationAbbr("SP");
        programDTO.setStudyCycleIds(Collections.singletonList(1L));
        programDTO.setFacultyId(1L);

        programs = new ArrayList<>();
        programs.add(program);
    }

    @Test
    public void testGetAllPrograms() throws Exception {
        Mockito.when(programService.getAllPrograms()).thenReturn(programs);

        String returnedJson = objectMapper.writeValueAsString(programs);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(programService).getAllPrograms();
    }

    @Test
    public void testGetProgramById() throws Exception {
        Long programId = 1L;
        Mockito.when(programService.getProgramById(programId)).thenReturn(program);

        String returnedJson = objectMapper.writeValueAsString(program);

        mockMvc.perform(get(BASE_URL + "/{id}", programId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(programService).getProgramById(programId);
    }

    @Test
    public void testGetProgramByIdNotFound() throws Exception {
        Long programId = 1L;
        Mockito.when(programService.getProgramById(programId)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{id}", programId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(programService).getProgramById(programId);
    }

    @Test
    public void testAddProgram() throws Exception {
        Mockito.when(programService.saveProgram(programDTO)).thenReturn(program);

        String requestBody = objectMapper.writeValueAsString(programDTO);
        String responseBody = objectMapper.writeValueAsString(program);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(programService).saveProgram(programDTO);
    }

    @Test
    public void testAddProgramAlreadyExists() throws Exception {
        Mockito.when(programService.saveProgram(programDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(programDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(programService).saveProgram(programDTO);
    }

    @Test
    public void testAddProgramWithoutStudyFieldAndSpecialization() throws Exception {
        programDTO.setSpecializationAbbr(null);
        programDTO.setStudyFieldAbbr(null);
        Mockito.when(programService.saveProgram(programDTO)).thenThrow(new IllegalArgumentException());

        String requestBody = objectMapper.writeValueAsString(programDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(programService).saveProgram(programDTO);
    }

    @Test
    public void testUpdateProgram() throws Exception {
        Long programId = 1L;
        programDTO.setName("Updated Program");

        Mockito.when(programService.updateProgram(programId, programDTO)).thenReturn(program);

        String requestBody = objectMapper.writeValueAsString(programDTO);
        String responseBody = objectMapper.writeValueAsString(program);

        mockMvc.perform(put(BASE_URL + "/{id}", programId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(programService).updateProgram(programId, programDTO);
    }

    @Test
    public void testUpdateProgramNotFound() throws Exception {
        Long programId = 1L;

        Mockito.when(programService.updateProgram(programId, programDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(programDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", programId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(programService).updateProgram(programId, programDTO);
    }

    @Test
    public void testUpdateProgramAlreadyExists() throws Exception {
        Long programId = 1L;
        programDTO.setName("Program Name");

        Mockito.when(programService.updateProgram(programId, programDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(programDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", programId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(programService).updateProgram(programId, programDTO);
    }

    @Test
    public void testUpdateProgramWithoutStudyFieldAndSpecialization() throws Exception {
        Long programId = 1L;
        programDTO.setName("Program Name");
        programDTO.setSpecializationAbbr(null);
        programDTO.setStudyFieldAbbr(null);

        Mockito.when(programService.updateProgram(programId, programDTO)).thenThrow(new IllegalArgumentException());

        String requestBody = objectMapper.writeValueAsString(programDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", programId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(programService).updateProgram(programId, programDTO);
    }

    @Test
    public void testDeleteProgram() throws Exception {
        Long programId = 1L;

        Mockito.when(programService.deleteProgram(programId)).thenReturn(program);

        String returnedJson = objectMapper.writeValueAsString(program);

        mockMvc.perform(delete(BASE_URL + "/{id}", programId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(programService).deleteProgram(programId);
    }

    @Test
    public void testDeleteProgramNotFound() throws Exception {
        Long programId = 2L;

        Mockito.when(programService.deleteProgram(programId)).thenThrow(new NotFoundException());

        mockMvc.perform(delete(BASE_URL + "/{id}", programId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(programService).deleteProgram(programId);
    }
}
