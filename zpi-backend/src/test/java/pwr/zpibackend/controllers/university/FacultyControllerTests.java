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
import pwr.zpibackend.services.university.FacultyService;
import pwr.zpibackend.services.user.EmployeeService;
import pwr.zpibackend.services.user.StudentService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FacultyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FacultyControllerTests {
    private static final String BASE_URL = "/faculty";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private FacultyService facultyService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private FacultyController facultyController;
    private List<Faculty> orderedFaculties;

    @BeforeEach
    public void setUp() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setAbbreviation("W11");
        faculty.setName("Wydział 11");
        faculty.setPrograms(null);
        faculty.setDepartments(null);

        Faculty faculty2 = new Faculty();
        faculty2.setId(2L);
        faculty2.setAbbreviation("W02");
        faculty2.setName("Wydział 2");
        faculty2.setPrograms(null);
        faculty2.setDepartments(null);

        orderedFaculties = List.of(faculty2, faculty);
    }

    @Test
    public void testGetAllFacultiesOrderedByAbbreviation() throws Exception {
        Mockito.when(facultyService.getAllFacultiesOrderedByAbbreviationAsc()).thenReturn(orderedFaculties);

        String returnedJson = objectMapper.writeValueAsString(orderedFaculties);

        mockMvc.perform(get(BASE_URL + "/ordered").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(facultyService).getAllFacultiesOrderedByAbbreviationAsc();
    }
}
