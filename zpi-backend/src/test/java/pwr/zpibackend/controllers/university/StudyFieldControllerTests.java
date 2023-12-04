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
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.services.university.StudyFieldService;
import pwr.zpibackend.services.user.EmployeeService;
import pwr.zpibackend.services.user.StudentService;

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

    @BeforeEach
    public void setUp() {
        StudyField studyField = new StudyField();
        studyField.setId(1L);
        studyField.setAbbreviation("IST");
        studyField.setName("Informatyka Stosowana");
        studyField.setFaculty(null);

        StudyField studyField2 = new StudyField();
        studyField2.setId(2L);
        studyField2.setAbbreviation("INA");
        studyField2.setName("Informatyka Algorytmiczna");
        studyField2.setFaculty(null);

        orderedStudyFields = List.of(studyField2, studyField);
    }

    @Test
    public void testGetAllFacultiesOrderedByAbbreviation() throws Exception {
        Mockito.when(studyFieldService.getAllStudyFieldsOrderedByAbbreviationAsc()).thenReturn(orderedStudyFields);

        String returnedJson = objectMapper.writeValueAsString(orderedStudyFields);

        mockMvc.perform(get(BASE_URL + "/ordered").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(studyFieldService).getAllStudyFieldsOrderedByAbbreviationAsc();
    }
}
