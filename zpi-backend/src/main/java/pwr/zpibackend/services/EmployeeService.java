package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.repositories.EmployeeRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final RoleService roleService;

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

    public Employee addEmployee(Employee employee)
    {
        if(employee.getRoles() == null || employee.getRoles().isEmpty())
        {
            throw new IllegalArgumentException("Employee must have at least one role");
        }
        if(employee.getRoles().contains(roleService.getRoleByName("student")))
        {
            throw new IllegalArgumentException("Employee cannot have student role");
        }
        employeeRepository.saveAndFlush(employee);
        return employee;
    }

    public List<Employee> getEmployeesByPrefix(String prefix) {
        return employeeRepository.findAllByMailStartingWith(prefix);
    }
}
