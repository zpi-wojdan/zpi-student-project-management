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
import pwr.zpibackend.dto.university.SpecializationDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.university.Specialization;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.services.impl.university.SpecializationService;
import pwr.zpibackend.services.impl.user.EmployeeService;
import pwr.zpibackend.services.impl.user.StudentService;
import pwr.zpibackend.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecializationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SpecializationControllerTests {

    private static final String BASE_URL = "/api/specialization";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private SpecializationService specializationService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private SpecializationController specializationController;

    private List<Specialization> specializations;
    private Specialization specialization;
    private SpecializationDTO specializationDTO;

    @BeforeEach
    public void setUp() {
        StudyField studyField = new StudyField();
        studyField.setId(1L);
        studyField.setAbbreviation("SF");
        studyField.setName("Study Field");

        specialization = new Specialization();
        specialization.setId(1L);
        specialization.setAbbreviation("SP");
        specialization.setName("Specialization");
        specialization.setStudyField(studyField);

        specializationDTO = new SpecializationDTO();
        specializationDTO.setAbbreviation("SP");
        specializationDTO.setName("Specialization");
        specializationDTO.setStudyFieldAbbr("SF");

        specializations = new ArrayList<>();
        specializations.add(specialization);
    }

    @Test
    public void testGetAllSpecializations() throws Exception {
        Mockito.when(specializationService.getAllSpecializations()).thenReturn(specializations);

        String returnedJson = objectMapper.writeValueAsString(specializations);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(specializationService).getAllSpecializations();
    }

    @Test
    public void testGetSpecializationByAbbreviation() throws Exception {
        String specializationAbbreviation = "SP";
        Mockito.when(specializationService.getSpecializationByAbbreviation(specializationAbbreviation))
                .thenReturn(specialization);

        String returnedJson = objectMapper.writeValueAsString(specialization);

        mockMvc.perform(get(BASE_URL + "/{abbreviation}", specializationAbbreviation)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(specializationService).getSpecializationByAbbreviation(specializationAbbreviation);
    }

    @Test
    public void testGetSpecializationByAbbreviationNotFound() throws Exception {
        String specializationAbbreviation = "SP";
        Mockito.when(specializationService.getSpecializationByAbbreviation(specializationAbbreviation))
                .thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{abbreviation}", specializationAbbreviation)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(specializationService).getSpecializationByAbbreviation(specializationAbbreviation);
    }

    @Test
    public void testAddSpecialization() throws Exception {
        Mockito.when(specializationService.saveSpecialization(specializationDTO)).thenReturn(specialization);

        String requestBody = objectMapper.writeValueAsString(specializationDTO);
        String responseBody = objectMapper.writeValueAsString(specialization);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(specializationService).saveSpecialization(specializationDTO);
    }

    @Test
    public void testAddSpecializationAlreadyExists() throws Exception {
        Mockito.when(specializationService.saveSpecialization(specializationDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(specializationDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(specializationService).saveSpecialization(specializationDTO);
    }

    @Test
    public void testAddSpecializationStudyFieldNotFound() throws Exception {
        specializationDTO.setStudyFieldAbbr("not found");
        Mockito.when(specializationService.saveSpecialization(specializationDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(specializationDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(specializationService).saveSpecialization(specializationDTO);
    }

    @Test
    public void testUpdateSpecialization() throws Exception {
        Long specializationId = 1L;
        specializationDTO.setName("Updated Specialization");

        Mockito.when(specializationService.updateSpecialization(specializationId, specializationDTO))
                .thenReturn(specialization);

        String requestBody = objectMapper.writeValueAsString(specializationDTO);
        String responseBody = objectMapper.writeValueAsString(specialization);

        mockMvc.perform(put(BASE_URL + "/{id}", specializationId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(specializationService).updateSpecialization(specializationId, specializationDTO);
    }

    @Test
    public void testUpdateSpecializationNotFound() throws Exception {
        Long specializationId = 1L;

        Mockito.when(specializationService.updateSpecialization(specializationId, specializationDTO))
                .thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(specializationDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", specializationId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(specializationService).updateSpecialization(specializationId, specializationDTO);
    }

    @Test
    public void testUpdateSpecializationAlreadyExists() throws Exception {
        Long specializationId = 1L;
        specializationDTO.setAbbreviation("SP"); // Setting an abbreviation that exists

        Mockito.when(specializationService.updateSpecialization(specializationId, specializationDTO))
                .thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(specializationDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", specializationId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(specializationService).updateSpecialization(specializationId, specializationDTO);
    }

    @Test
    public void testDeleteSpecialization() throws Exception {
        Long specializationId = 1L;

        Mockito.when(specializationService.deleteSpecialization(specializationId)).thenReturn(specialization);

        String returnedJson = objectMapper.writeValueAsString(specialization);

        mockMvc.perform(delete(BASE_URL + "/{id}", specializationId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(specializationService).deleteSpecialization(specializationId);
    }

    @Test
    public void testDeleteSpecializationNotFound() throws Exception {
        Long specializationId = 2L;

        Mockito.when(specializationService.deleteSpecialization(specializationId)).thenThrow(new NotFoundException());

        mockMvc.perform(delete(BASE_URL + "/{id}", specializationId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(specializationService).deleteSpecialization(specializationId);
    }
}
