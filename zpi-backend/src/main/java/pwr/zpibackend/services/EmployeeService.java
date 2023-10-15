package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.repositories.EmployeeRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id).get();
    }

    public Employee addEmployee(Employee employee)
    {
        employeeRepository.saveAndFlush(employee);
        return employee;
    }
}
