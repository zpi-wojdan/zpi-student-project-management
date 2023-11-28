package pwr.zpibackend.controllers.user;

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
import pwr.zpibackend.controllers.user.EmployeeController;
import pwr.zpibackend.dto.user.EmployeeDTO;
import pwr.zpibackend.dto.user.RoleDTO;
import pwr.zpibackend.dto.university.TitleDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.CannotDeleteException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Role;
import pwr.zpibackend.models.university.Title;
import pwr.zpibackend.services.user.EmployeeService;
import pwr.zpibackend.services.user.StudentService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeControllerTests {

    private static final String BASE_URL = "/employee";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private EmployeeController employeeController;

    private List<Employee> employees;
    private Employee employee;

    private EmployeeDTO employeeDTO;

    @BeforeEach
    public void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setMail("123456@pwr.edu.pl");
        employee.setName("John");
        employee.setSurname("Doe");
        employee.setDepartment(null);
        employee.setTitle(new Title("mgr inż."));

        Role role = new Role("admin");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        employee.setRoles(roles);

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setMail("121212@pwr.edu.pl");
        employee2.setName("Jane");
        employee2.setSurname("Doe");
        employee2.setDepartment(null);
        employee2.setTitle(new Title("mgr inż."));
        employee2.setRoles(roles);

        employeeDTO = new EmployeeDTO();
        employeeDTO.setMail("111111@pwr.edu.pl");
        employeeDTO.setName("John");
        employeeDTO.setSurname("Doe");
        employeeDTO.setDepartmentCode(null);
        employeeDTO.setTitle(new TitleDTO("mgr inż."));
        List<RoleDTO> roleDTOS = new ArrayList<>();
        roleDTOS.add(new RoleDTO("admin"));
        employeeDTO.setRoles(roleDTOS);

        employees = new ArrayList<>();
        employees.add(employee);
        employees.add(employee2);
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        Mockito.when(employeeService.getAllEmployees()).thenReturn(employees);

        String returnedJson = objectMapper.writeValueAsString(employees);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(employeeService).getAllEmployees();
    }

    @Test
    public void testGetEmployeeById() throws Exception {
        Long id = 1L;
        Mockito.when(employeeService.getEmployee(id)).thenReturn(employee);

        String returnedJson = objectMapper.writeValueAsString(employee);

        mockMvc.perform(get(BASE_URL + "/{id}", id).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(employeeService).getEmployee(id);
    }

    @Test
    public void testGetEmployeeByIdNotFound() throws Exception {
        Long id = 1L;
        Mockito.when(employeeService.getEmployee(id)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{id}", id).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(employeeService).getEmployee(id);
    }

    @Test
    public void testAddEmployee() throws Exception {
        String requestBody = objectMapper.writeValueAsString(employeeDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(employeeService).addEmployee(employeeDTO);
    }

    @Test
    public void testAddEmployeeAlreadyExists() throws Exception {
        employeeDTO.setMail("123456@pwr.edu.pl");
        Mockito.when(employeeService.addEmployee(employeeDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(employeeDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(employeeService).addEmployee(employeeDTO);
    }

    @Test
    public void testAddEmployeeWithStudentRole() throws Exception {
        employeeDTO.getRoles().clear();
        employeeDTO.getRoles().add(new RoleDTO("student"));
        Mockito.when(employeeService.addEmployee(employeeDTO)).thenThrow(new IllegalArgumentException());

        String requestBody = objectMapper.writeValueAsString(employeeDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(employeeService).addEmployee(employeeDTO);
    }

    @Test
    public void testAddEmployeeWithoutExistingRole() throws Exception {
        employeeDTO.getRoles().clear();
        employeeDTO.getRoles().add(new RoleDTO("tester"));

        Mockito.when(employeeService.addEmployee(employeeDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(employeeDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(employeeService).addEmployee(employeeDTO);
    }

    @Test
    public void testUpdateEmployee() throws Exception {
        Long id = 1L;
        employeeDTO.setName("Updated");
        employee.setName("Updated");

        Mockito.when(employeeService.updateEmployee(id, employeeDTO)).thenReturn(employee);

        String requestBody = objectMapper.writeValueAsString(employeeDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(employeeService).updateEmployee(id, employeeDTO);
    }

    @Test
    public void testUpdateEmployeeNotFound() throws Exception {
        Long id = 1L;

        Mockito.when(employeeService.updateEmployee(id, employeeDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(employeeDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(employeeService).updateEmployee(id, employeeDTO);
    }

    @Test
    public void testUpdateEmployeeMail() throws Exception {
        Long id = 1L;

        Mockito.when(employeeService.updateEmployee(id, employeeDTO)).thenThrow(new IllegalArgumentException());

        String requestBody = objectMapper.writeValueAsString(employeeDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(employeeService).updateEmployee(id, employeeDTO);
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        Long id = 1L;

        Mockito.when(employeeService.deleteEmployee(id)).thenReturn(employee);

        String returnedJson = objectMapper.writeValueAsString(employee);

        mockMvc.perform(delete(BASE_URL + "/{id}", id).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(employeeService).deleteEmployee(id);
    }

    @Test
    public void testDeleteEmployeeNotFound() throws Exception {
        Long id = 1L;

        Mockito.when(employeeService.deleteEmployee(id)).thenThrow(new NotFoundException());

        mockMvc.perform(delete(BASE_URL + "/{id}", id).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(employeeService).deleteEmployee(id);
    }

    @Test
    public void testDeleteEmployeeWhoCannotBeDeleted() throws Exception {
        Long id = 1L;

        Mockito.when(employeeService.deleteEmployee(id)).thenThrow(new CannotDeleteException());

        mockMvc.perform(delete(BASE_URL + "/{id}", id).contentType("application/json"))
                .andExpect(status().isMethodNotAllowed());

        verify(employeeService).deleteEmployee(id);
    }
}
