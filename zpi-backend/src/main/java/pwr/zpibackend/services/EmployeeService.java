package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.EmployeeDTO;
import pwr.zpibackend.dto.RoleDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Role;
import pwr.zpibackend.repositories.EmployeeRepository;
import pwr.zpibackend.services.university.DepartmentService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final RoleService roleService;

    private final DepartmentService departmentService;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployee(String email) {
        return employeeRepository.findById(email).orElseThrow(
                () -> new NoSuchElementException("Employee with email " + email + " does not exist")
        );
    }

    public boolean exists(String email) {
        return employeeRepository.existsById(email);
    }

    public Employee addEmployee(EmployeeDTO employee) throws NotFoundException {
        if(employee.getRoles() == null || employee.getRoles().isEmpty())
            throw new IllegalArgumentException("Employee must have at least one role");
        if (exists(employee.getMail()))
            throw new IllegalArgumentException("Employee with email " + employee.getMail() + " already exists");

        Employee newEmployee = new Employee();
        newEmployee.setMail(employee.getMail());
        newEmployee.setName(employee.getName());
        newEmployee.setSurname(employee.getSurname());
        newEmployee.setTitle(employee.getTitle());
        newEmployee.setRoles(validateRoles(employee.getRoles()));
        newEmployee.setDepartment(departmentService.getDepartmentByCode(employee.getDepartmentCode()));

        return employeeRepository.save(newEmployee);
    }

    public Employee updateEmployee(String email, EmployeeDTO updatedEmployee) throws NotFoundException {
        Employee existingEmployee = employeeRepository.findById(email)
                .orElseThrow(() -> new NotFoundException("Employee with email " + email + " does not exist"));
        if(!updatedEmployee.getMail().equals(email))
            throw new IllegalArgumentException("Email cannot be changed");

        existingEmployee.setName(updatedEmployee.getName());
        existingEmployee.setSurname(updatedEmployee.getSurname());
        existingEmployee.setTitle(updatedEmployee.getTitle());
        existingEmployee.setDepartment(departmentService.getDepartmentByCode(updatedEmployee.getDepartmentCode()));
        existingEmployee.getRoles().clear();
        existingEmployee.getRoles().addAll(validateRoles(updatedEmployee.getRoles()));

        return employeeRepository.save(existingEmployee);
    }

    private List<Role> validateRoles(List<RoleDTO> roles) {
        List<Role> newRoles = new ArrayList<>();
        for (RoleDTO role : roles) {
            Role newRole = roleService.getRoleByName(role.getName());
            if (newRole == null)
                throw new NoSuchElementException("Role with name " + role.getName() + " does not exist");
            if(newRole.getName().equals("student"))
                throw new IllegalArgumentException("Employee cannot have role 'student'");
            newRoles.add(newRole);
        }
        return newRoles;
    }

    public List<Employee> getEmployeesByPrefix(String prefix) {
        return employeeRepository.findAllByMailStartingWith(prefix);
    }
}
