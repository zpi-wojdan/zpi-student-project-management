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
import pwr.zpibackend.dto.university.StudyFieldDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.services.impl.university.StudyFieldService;
import pwr.zpibackend.services.impl.user.EmployeeService;
import pwr.zpibackend.services.impl.user.StudentService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(StudyFieldController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StudyFieldControllerTests {
    private static final String BASE_URL = "/api/studyfield";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private StudyFieldService studyFieldService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private StudyFieldController studyFieldController;
    private List<StudyField> orderedStudyFields;
    private List<StudyField> studyFields;
    private StudyField studyField;
    private StudyFieldDTO studyFieldDTO;

    @BeforeEach
    public void setUp() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setAbbreviation("W04N");
        faculty.setName("Wydział Informatyki");

        studyField = new StudyField();
        studyField.setId(1L);
        studyField.setAbbreviation("IST");
        studyField.setName("Informatyka Stosowana");
        studyField.setFaculty(faculty);

        StudyField studyField2 = new StudyField();
        studyField2.setId(2L);
        studyField2.setAbbreviation("INA");
        studyField2.setName("Informatyka Algorytmiczna");
        studyField2.setFaculty(faculty);

        studyFieldDTO = new StudyFieldDTO();
        studyFieldDTO.setAbbreviation("IST");
        studyFieldDTO.setName("Informatyka Stosowana");
        studyFieldDTO.setFacultyAbbr("W04N");

        studyFields = List.of(studyField, studyField2);
        orderedStudyFields = List.of(studyField2, studyField);
    }

    @Test
    public void testGetAllStudyFields() throws Exception {
        Mockito.when(studyFieldService.getAllStudyFields()).thenReturn(studyFields);

        String returnedJson = objectMapper.writeValueAsString(studyFields);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(studyFieldService).getAllStudyFields();
    }

    @Test
    public void testGetStudyFieldByAbbreviation() throws Exception {
        String studyFieldAbbreviation = "IST";
        Mockito.when(studyFieldService.getStudyFieldByAbbreviation(studyFieldAbbreviation)).thenReturn(studyField);

        String returnedJson = objectMapper.writeValueAsString(studyField);

        mockMvc.perform(get(BASE_URL + "/{abbreviation}", studyFieldAbbreviation)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(studyFieldService).getStudyFieldByAbbreviation(studyFieldAbbreviation);
    }

    @Test
    public void testGetStudyFieldByAbbreviationNotFound() throws Exception {
        String studyFieldAbbreviation = "not found";
        Mockito.when(studyFieldService.getStudyFieldByAbbreviation(studyFieldAbbreviation))
                .thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{abbreviation}", studyFieldAbbreviation)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(studyFieldService).getStudyFieldByAbbreviation(studyFieldAbbreviation);
    }

    @Test
    public void testGetAllStudyFieldsOrderedByAbbreviation() throws Exception {
        Mockito.when(studyFieldService.getAllStudyFieldsOrderedByAbbreviationAsc()).thenReturn(orderedStudyFields);

        String returnedJson = objectMapper.writeValueAsString(orderedStudyFields);

        mockMvc.perform(get(BASE_URL + "/ordered").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(studyFieldService).getAllStudyFieldsOrderedByAbbreviationAsc();
    }

    @Test
    public void testCreateStudyField() throws Exception {
        Mockito.when(studyFieldService.saveStudyField(studyFieldDTO)).thenReturn(studyField);

        String requestBody = objectMapper.writeValueAsString(studyFieldDTO);
        String responseBody = objectMapper.writeValueAsString(studyField);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(studyFieldService).saveStudyField(studyFieldDTO);
    }

    @Test
    public void testCreateStudyFieldAlreadyExists() throws Exception {
        Mockito.when(studyFieldService.saveStudyField(studyFieldDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(studyFieldDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(studyFieldService).saveStudyField(studyFieldDTO);
    }

    @Test
    public void testCreateStudyFieldFacultyNotFound() throws Exception {
        studyFieldDTO.setFacultyAbbr("not found");
        Mockito.when(studyFieldService.saveStudyField(studyFieldDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(studyFieldDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(studyFieldService).saveStudyField(studyFieldDTO);
    }

    @Test
    public void testUpdateStudyField() throws Exception {
        Long studyFieldId = 1L;
        studyFieldDTO.setName("Updated Study Field");

        Mockito.when(studyFieldService.updateStudyField(studyFieldId, studyFieldDTO)).thenReturn(studyField);

        String requestBody = objectMapper.writeValueAsString(studyFieldDTO);
        String responseBody = objectMapper.writeValueAsString(studyField);

        mockMvc.perform(put(BASE_URL + "/{id}", studyFieldId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(studyFieldService).updateStudyField(studyFieldId, studyFieldDTO);
    }

    @Test
    public void testUpdateStudyFieldNotFound() throws Exception {
        Long studyFieldId = 1L;

        Mockito.when(studyFieldService.updateStudyField(studyFieldId, studyFieldDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(studyFieldDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", studyFieldId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(studyFieldService).updateStudyField(studyFieldId, studyFieldDTO);
    }

    @Test
    public void testUpdateStudyFieldAlreadyExists() throws Exception {
        Long studyFieldId = 1L;
        studyFieldDTO.setAbbreviation("INA");
        Mockito.when(studyFieldService.updateStudyField(studyFieldId, studyFieldDTO)).thenThrow(
                new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(studyFieldDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", studyFieldId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(studyFieldService).updateStudyField(studyFieldId, studyFieldDTO);
    }

    @Test
    public void testDeleteStudyField() throws Exception {
        Long studyFieldId = 1L;

        Mockito.when(studyFieldService.deleteStudyField(studyFieldId)).thenReturn(studyField);

        String returnedJson = objectMapper.writeValueAsString(studyField);

        mockMvc.perform(delete(BASE_URL + "/{id}", studyFieldId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(studyFieldService).deleteStudyField(studyFieldId);
    }

    @Test
    public void testDeleteStudyFieldNotFound() throws Exception {
        Long studyFieldId = 2L;

        Mockito.when(studyFieldService.deleteStudyField(studyFieldId)).thenThrow(new NotFoundException());

        mockMvc.perform(delete(BASE_URL + "/{id}", studyFieldId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(studyFieldService).deleteStudyField(studyFieldId);
    }
}
