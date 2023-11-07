package pwr.zpibackend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import pwr.zpibackend.dto.EmployeeDTO;
import pwr.zpibackend.dto.RoleDTO;
import pwr.zpibackend.dto.university.TitleDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.CannotDeleteException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Role;
import pwr.zpibackend.models.university.Title;
import pwr.zpibackend.repositories.EmployeeRepository;
import pwr.zpibackend.repositories.university.TitleRepository;
import pwr.zpibackend.services.university.DepartmentService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private DepartmentService departmentService;
    @Mock
    private TitleRepository titleRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private List<Employee> employees;
    private Employee employee;

    private EmployeeDTO employeeDTO;

    @BeforeEach
    public void setUp() {
        employee = new Employee();
        employee.setMail("123456@pwr.edu.pl");
        employee.setName("John");
        employee.setSurname("Doe");
        employee.setDepartment(null);
        employee.setTitle(new Title("mgr"));

        Role role = new Role("admin");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        employee.setRoles(roles);

        Employee employee2 = new Employee();
        employee2.setMail("121212@pwr.edu.pl");
        employee2.setName("Jane");
        employee2.setSurname("Doe");
        employee2.setDepartment(null);
        employee2.setTitle(new Title("dr"));
        employee2.setRoles(roles);

        employeeDTO = new EmployeeDTO();
        employeeDTO.setMail("123456@pwr.edu.pl");
        employeeDTO.setName("Jo");
        employeeDTO.setSurname("Smith");
        employeeDTO.setDepartmentCode(null);
        employeeDTO.setTitle(new TitleDTO("dr"));
        List<RoleDTO> roleDTOS = new ArrayList<>();
        roleDTOS.add(new RoleDTO("supervisor"));
        employeeDTO.setRoles(roleDTOS);

        employees = new ArrayList<>();
        employees.add(employee);
        employees.add(employee2);
    }

    @Test
    public void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertSame(2, result.size());
        assertSame(employees, result);
    }

    @Test
    public void testGetEmployeeByMail() {
        String email = "123456@pwr.edu.pl";
        when(employeeRepository.findByMail(email)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployee(email);

        assertSame(employee, result);
    }

    @Test
    public void testGetEmployeeByMailNotFound() {
        String email = "123456@pwr.edu.pl";
        when(employeeRepository.findByMail(email)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> employeeService.getEmployee(email));
    }

    @Test
    public void testGetEmployeesByPrefix() {
        String prefix = "1234";
        when(employeeRepository.findAllByMailStartingWith(prefix)).thenReturn(List.of(employee));

        List<Employee> result = employeeService.getEmployeesByPrefix(prefix);

        assertSame(1, result.size());
        assertSame(employee, result.get(0));
    }

    @Test
    public void testEmployeeExists() {
        String email = "123456@pwr.edu.pl";
        when(employeeRepository.existsByMail(email)).thenReturn(true);

        boolean result = employeeService.exists(email);

        assertSame(true, result);
    }

    @Test
    public void testEmployeeDoesNotExist() {
        String email = "123456@pwr.edu.pl";
        when(employeeRepository.existsByMail(email)).thenReturn(false);

        boolean result = employeeService.exists(email);

        assertSame(false, result);
    }

    @Test
    public void testAddEmployee() throws AlreadyExistsException, NotFoundException {
        Employee newEmployee = new Employee();
        newEmployee.setMail(employeeDTO.getMail());
        newEmployee.setName(employeeDTO.getName());
        newEmployee.setSurname(employeeDTO.getSurname());
        newEmployee.setTitle(new Title(employeeDTO.getTitle().getName()));
        newEmployee.setRoles(List.of(new Role("supervisor")));
        newEmployee.setDepartment(null);

        when(employeeRepository.existsByMail(employeeDTO.getMail())).thenReturn(false);
        when(roleService.getRoleByName("supervisor")).thenReturn(new Role("supervisor"));
        when(departmentService.getDepartmentByCode(employeeDTO.getDepartmentCode())).thenReturn(null);
        when(employeeRepository.save(newEmployee)).thenReturn(newEmployee);
        when(titleRepository.findByName(employeeDTO.getTitle().getName())).thenReturn(Optional.of(new Title(employeeDTO.getTitle().getName())));

        Employee result = employeeService.addEmployee(employeeDTO);

        assertSame(newEmployee, result);
    }

    @Test
    public void testAddEmployeeWithNoRoles() {
        employeeDTO.setRoles(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(employeeDTO));
    }

    @Test
    public void testAddEmployeeWithInvalidEmail() {
        employeeDTO.setMail("123456@mail.com");

        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(employeeDTO));
    }

    @Test
    public void testAddEmployeeAlreadyExists() {
        when(employeeRepository.existsByMail(employeeDTO.getMail())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> employeeService.addEmployee(employeeDTO));
    }

    @Test
    public void testAddEmployeeWithNotExistingRole() {
        employeeDTO.getRoles().clear();
        employeeDTO.getRoles().add(new RoleDTO("tester"));

        when(employeeRepository.existsByMail(employeeDTO.getMail())).thenReturn(false);
        when(roleService.getRoleByName("tester")).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> employeeService.addEmployee(employeeDTO));
    }

    @Test
    public void testAddEmployeeWithInvalidRoleStudent() {
        employeeDTO.getRoles().clear();
        employeeDTO.getRoles().add(new RoleDTO("student"));

        when(employeeRepository.existsByMail(employeeDTO.getMail())).thenReturn(false);
        when(roleService.getRoleByName("student")).thenReturn(new Role("student"));
        when(titleRepository.findByName(employeeDTO.getTitle().getName())).thenReturn(Optional.of(new Title(employeeDTO.getTitle().getName())));

        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(employeeDTO));
    }

    @Test
    public void testUpdateEmployee() throws NotFoundException {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setMail(employeeDTO.getMail());
        updatedEmployee.setName(employeeDTO.getName());
        updatedEmployee.setSurname(employeeDTO.getSurname());
        updatedEmployee.setTitle(new Title(employeeDTO.getTitle().getName()));
        updatedEmployee.setRoles(List.of(new Role("supervisor")));
        updatedEmployee.setDepartment(null);

        when(employeeRepository.findById(updatedEmployee.getId())).thenReturn(Optional.of(employee));
        when(roleService.getRoleByName("supervisor")).thenReturn(new Role("supervisor"));
        when(departmentService.getDepartmentByCode(employeeDTO.getDepartmentCode())).thenReturn(null);
        when(employeeRepository.save(any())).thenReturn(updatedEmployee);
        when(titleRepository.findByName(employeeDTO.getTitle().getName())).thenReturn(Optional.of(new Title(employeeDTO.getTitle().getName())));

        Employee result = employeeService.updateEmployee(1L, employeeDTO);

        assertSame(updatedEmployee, result);
    }

    @Test
    public void testUpdateEmployeeNotFound() {
        when(employeeRepository.findByMail(employeeDTO.getMail())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.updateEmployee(1L, employeeDTO));
    }

    @Test
    public void testUpdateEmployeeWithNotExistingRole() {
        employeeDTO.getRoles().clear();
        employeeDTO.getRoles().add(new RoleDTO("tester"));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(roleService.getRoleByName("tester")).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> employeeService.updateEmployee(1L,
                employeeDTO));
    }

    @Test
    public void testUpdateWithInvalidRoleStudent() {
        employeeDTO.getRoles().clear();
        employeeDTO.getRoles().add(new RoleDTO("student"));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(roleService.getRoleByName("student")).thenReturn(new Role("student"));
        when(titleRepository.findByName(employeeDTO.getTitle().getName())).thenReturn(Optional.of(new Title(employeeDTO.getTitle().getName())));

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployee(1L,
                employeeDTO));
    }

    @Test
    public void testDeleteEmployee() throws CannotDeleteException, NotFoundException {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.deleteEmployee(1L);

        assertSame(employee, result);
    }

    @Test
    public void testDeleteEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.deleteEmployee(1L));
    }

    @Test
    public void testDeleteEmployeeInUse() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doThrow(new DataIntegrityViolationException("")).when(employeeRepository).delete(employee);

        assertThrows(CannotDeleteException.class, () -> employeeService.deleteEmployee(1L));
    }
}
