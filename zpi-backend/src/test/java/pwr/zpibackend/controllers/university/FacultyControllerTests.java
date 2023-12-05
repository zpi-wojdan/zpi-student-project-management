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
import pwr.zpibackend.dto.university.FacultyDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.services.university.FacultyService;
import pwr.zpibackend.services.user.EmployeeService;
import pwr.zpibackend.services.user.StudentService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FacultyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FacultyControllerTests {
    private static final String BASE_URL = "/api/faculty";

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
    private List<Faculty> faculties;
    private Faculty faculty;
    private FacultyDTO facultyDTO;

    @BeforeEach
    public void setUp() {
        faculty = new Faculty();
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

        facultyDTO = new FacultyDTO();
        facultyDTO.setAbbreviation("W11");
        facultyDTO.setName("Wydział 11");

        faculties = new ArrayList<>();
        faculties.add(faculty);
        faculties.add(faculty2);

        orderedFaculties = List.of(faculty2, faculty);
    }

    @Test
    public void testGetAllFaculties() throws Exception {
        Mockito.when(facultyService.getAllFaculties()).thenReturn(faculties);

        String returnedJson = objectMapper.writeValueAsString(faculties);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(facultyService).getAllFaculties();
    }

    @Test
    public void testGetFacultyById() throws Exception {
        String facultyAbbreviation = "W11";
        Mockito.when(facultyService.getFacultyByAbbreviation(facultyAbbreviation)).thenReturn(faculty);

        String returnedJson = objectMapper.writeValueAsString(faculty);

        mockMvc.perform(get(BASE_URL + "/{abbreviation}", facultyAbbreviation).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(facultyService).getFacultyByAbbreviation(facultyAbbreviation);
    }

    @Test
    public void testGetFacultyByIdNotFound() throws Exception {
        String facultyAbbreviation = "not found abbr";
        Mockito.when(facultyService.getFacultyByAbbreviation(facultyAbbreviation)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{abbreviation}", facultyAbbreviation).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(facultyService).getFacultyByAbbreviation(facultyAbbreviation);
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

    @Test
    public void testAddFaculty() throws Exception {
        Mockito.when(facultyService.saveFaculty(facultyDTO)).thenReturn(faculty);

        String requestBody = objectMapper.writeValueAsString(facultyDTO);
        String responseBody = objectMapper.writeValueAsString(faculty);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(facultyService).saveFaculty(facultyDTO);
    }

    @Test
    public void testAddFacultyAlreadyExists() throws Exception {
        Mockito.when(facultyService.saveFaculty(facultyDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(facultyDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(facultyService).saveFaculty(facultyDTO);
    }

    @Test
    public void testUpdateFaculty() throws Exception {
        Long facultyId = 1L;
        facultyDTO.setName("Updated Faculty");

        Mockito.when(facultyService.updateFaculty(facultyId, facultyDTO)).thenReturn(faculty);

        String requestBody = objectMapper.writeValueAsString(facultyDTO);
        String responseBody = objectMapper.writeValueAsString(faculty);

        mockMvc.perform(put(BASE_URL + "/{id}", facultyId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(facultyService).updateFaculty(facultyId, facultyDTO);
    }

    @Test
    public void testUpdateFacultyNotFound() throws Exception {
        Long facultyId = 1L;

        Mockito.when(facultyService.updateFaculty(facultyId, facultyDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(facultyDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", facultyId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(facultyService).updateFaculty(facultyId, facultyDTO);
    }

    @Test
    public void testUpdateFacultyAlreadyExists() throws Exception {
        Long facultyId = 1L;
        facultyDTO.setAbbreviation("W11");

        Mockito.when(facultyService.updateFaculty(facultyId, facultyDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(facultyDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", facultyId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(facultyService).updateFaculty(facultyId, facultyDTO);
    }

    @Test
    public void testDeleteFaculty() throws Exception {
        Long facultyId = 1L;

        Mockito.when(facultyService.deleteFaculty(facultyId)).thenReturn(faculty);

        String returnedJson = objectMapper.writeValueAsString(faculty);

        mockMvc.perform(delete(BASE_URL + "/{id}", facultyId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(facultyService).deleteFaculty(facultyId);
    }

    @Test
    public void testDeleteFacultyNotFound() throws Exception {
        Long facultyId = 2L;

        Mockito.when(facultyService.deleteFaculty(facultyId)).thenThrow(new NotFoundException());

        mockMvc.perform(delete(BASE_URL + "/{id}", facultyId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(facultyService).deleteFaculty(facultyId);
    }
}
