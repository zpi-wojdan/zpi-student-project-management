package pwr.zpibackend.services.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.user.EmployeeDTO;
import pwr.zpibackend.dto.user.RoleDTO;
import pwr.zpibackend.dto.university.TitleDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.CannotDeleteException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Role;
import pwr.zpibackend.models.university.Title;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.repositories.university.TitleRepository;
import pwr.zpibackend.services.university.DepartmentService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final RoleService roleService;

    private final DepartmentService departmentService;

    private final TitleRepository titleRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Employee with id " + id + " does not exist")
        );
    }

    public Employee getEmployee(String mail) {
        return employeeRepository.findByMail(mail).orElseThrow(
                () -> new NotFoundException("Employee with mail " + mail + " does not exist")
        );
    }

    public List<Employee> getEmployeesByPrefix(String prefix) {
        return employeeRepository.findAllByMailStartingWith(prefix);
    }

    public boolean exists(String mail) {
        return employeeRepository.existsByMail(mail);
    }

    public Employee addEmployee(EmployeeDTO employee) {
        if(employee.getRoles() == null || employee.getRoles().isEmpty())
            throw new IllegalArgumentException("Employee must have at least one role");
        if(!Pattern.matches("^[a-z0-9-]{1,50}(\\.[a-z0-9-]{1,50}){0,4}@(?:student\\.)" +
                "?(pwr\\.edu\\.pl|pwr\\.wroc\\.pl)$", employee.getMail()))
            throw new IllegalArgumentException("Email must be from pwr.edu.pl or pwr.wroc.pl domain");
        if (exists(employee.getMail()))
            throw new AlreadyExistsException("Employee with mail " + employee.getMail() + " already exists");

        Employee newEmployee = new Employee();
        newEmployee.setMail(employee.getMail());
        newEmployee.setName(employee.getName());
        newEmployee.setSurname(employee.getSurname());
        newEmployee.setTitle(validateTitle(employee.getTitle()));
        newEmployee.setRoles(validateRoles(employee.getRoles()));
        newEmployee.setDepartment(departmentService.getDepartmentByCode(employee.getDepartmentCode()));

        return employeeRepository.save(newEmployee);
    }

    public Employee updateEmployee(Long id, EmployeeDTO updatedEmployee) {
        if (employeeRepository.existsByMail(updatedEmployee.getMail()))
            if (!(employeeRepository.findByMail(updatedEmployee.getMail()).get().getId() == id))
                throw new AlreadyExistsException("Employee with mail " + updatedEmployee.getMail() + " already exists");

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with id " + id + " does not exist"));

        existingEmployee.setMail(updatedEmployee.getMail());
        existingEmployee.setName(updatedEmployee.getName());
        existingEmployee.setSurname(updatedEmployee.getSurname());
        existingEmployee.setTitle(validateTitle(updatedEmployee.getTitle()));
        existingEmployee.setDepartment(departmentService.getDepartmentByCode(updatedEmployee.getDepartmentCode()));
        existingEmployee.getRoles().clear();
        existingEmployee.getRoles().addAll(validateRoles(updatedEmployee.getRoles()));

        return employeeRepository.save(existingEmployee);
    }

    private Title validateTitle(TitleDTO titleDTO) {
        if (titleDTO == null)
            throw new IllegalArgumentException("Title cannot be null");
        if (titleDTO.getName() == null)
            throw new IllegalArgumentException("Title name cannot be null");
        Title title = titleRepository.findByName(titleDTO.getName()).orElse(null);
        if (title == null)
            throw new NotFoundException("Title with name " + titleDTO.getName() + " does not exist");
        return title;
    }

    private List<Role> validateRoles(List<RoleDTO> roles) {
        List<Role> newRoles = new ArrayList<>();
        for (RoleDTO role : roles) {
            Role newRole = roleService.getRoleByName(role.getName());
            if (newRole == null)
                throw new NotFoundException("Role with name " + role.getName() + " does not exist");
            if(newRole.getName().equals("student"))
                throw new IllegalArgumentException("Employee cannot have role 'student'");
            newRoles.add(newRole);
        }
        return newRoles;
    }

    public Employee deleteEmployee(Long id) throws NotFoundException, CannotDeleteException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with id " + id + " does not exist"));

        try {
            employeeRepository.delete(employee);
        } catch (Exception e) {
            throw new CannotDeleteException("Employee with email " + employee.getMail() + " cannot be deleted");
        }
        return employee;
    }

}
