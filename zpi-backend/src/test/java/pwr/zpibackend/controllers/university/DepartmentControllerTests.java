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
import pwr.zpibackend.dto.university.DepartmentDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.services.impl.university.DepartmentService;
import pwr.zpibackend.services.impl.user.EmployeeService;
import pwr.zpibackend.services.impl.user.StudentService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DepartmentControllerTests {

    private static final String BASE_URL = "/api/departments";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private DepartmentService departmentService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private DepartmentController departmentController;

    private List<Department> departments;
    private Department department;
    private DepartmentDTO departmentDTO;

    @BeforeEach
    public void setUp() {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setAbbreviation("ENG");
        faculty.setName("Engineering");

        department = new Department();
        department.setId(1L);
        department.setCode("CS");
        department.setName("Computer Science");
        department.setFaculty(faculty);

        departmentDTO = new DepartmentDTO();
        departmentDTO.setCode("K01");
        departmentDTO.setName("IT");
        departmentDTO.setFacultyAbbreviation("ENG");

        departments = new ArrayList<>();
        departments.add(department);
    }

    @Test
    public void testGetAllDepartments() throws Exception {
        Mockito.when(departmentService.getAllDepartments()).thenReturn(departments);

        String returnedJson = objectMapper.writeValueAsString(departments);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(departmentService).getAllDepartments();
    }

    @Test
    public void testGetDepartmentByCode() throws Exception {
        String departmentCode = "K01";
        Mockito.when(departmentService.getDepartmentByCode(departmentCode)).thenReturn(department);

        String returnedJson = objectMapper.writeValueAsString(department);

        mockMvc.perform(get(BASE_URL + "/{code}", departmentCode).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(departmentService).getDepartmentByCode(departmentCode);
    }

    @Test
    public void testGetDepartmentByCodeNotFound() throws Exception {
        String departmentCode = "not found code";
        Mockito.when(departmentService.getDepartmentByCode(departmentCode)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{code}", departmentCode).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(departmentService).getDepartmentByCode(departmentCode);
    }

    @Test
    public void testAddDepartment() throws Exception {
        Mockito.when(departmentService.addDepartment(departmentDTO)).thenReturn(department);

        String requestBody = objectMapper.writeValueAsString(departmentDTO);
        String responseBody = objectMapper.writeValueAsString(department);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(departmentService).addDepartment(departmentDTO);
    }

    @Test
    public void testAddDepartmentAlreadyExists() throws Exception {
        Mockito.when(departmentService.addDepartment(departmentDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(departmentDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(departmentService).addDepartment(departmentDTO);
    }

    @Test
    public void testUpdateDepartment() throws Exception {
        Long departmentId = 1L;
        departmentDTO.setName("Updated Department");

        Mockito.when(departmentService.updateDepartment(departmentId, departmentDTO)).thenReturn(department);

        String requestBody = objectMapper.writeValueAsString(departmentDTO);
        String responseBody = objectMapper.writeValueAsString(department);

        mockMvc.perform(put(BASE_URL + "/{id}", departmentId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(departmentService).updateDepartment(departmentId, departmentDTO);
    }

    @Test
    public void testUpdateDepartmentNotFound() throws Exception {
        Long departmentId = 1L;

        Mockito.when(departmentService.updateDepartment(departmentId, departmentDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(departmentDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", departmentId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(departmentService).updateDepartment(departmentId, departmentDTO);
    }

    @Test
    public void testUpdateDepartmentAlreadyExists() throws Exception {
        Long departmentId = 1L;
        departmentDTO.setCode("K01");

        Mockito.when(departmentService.updateDepartment(departmentId, departmentDTO)).thenThrow(
                new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(departmentDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", departmentId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(departmentService).updateDepartment(departmentId, departmentDTO);
    }

    @Test
    public void testDeleteDepartment() throws Exception {
        Long departmentId = 1L;

        Mockito.when(departmentService.deleteDepartment(departmentId)).thenReturn(department);

        String returnedJson = objectMapper.writeValueAsString(department);

        mockMvc.perform(delete(BASE_URL + "/{id}", departmentId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(departmentService).deleteDepartment(departmentId);
    }

    @Test
    public void testDeleteDepartmentNotFound() throws Exception {
        Long departmentId = 2L;

        Mockito.when(departmentService.deleteDepartment(departmentId)).thenThrow(new NotFoundException());

        mockMvc.perform(delete(BASE_URL + "/{id}", departmentId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(departmentService).deleteDepartment(departmentId);
    }
}
