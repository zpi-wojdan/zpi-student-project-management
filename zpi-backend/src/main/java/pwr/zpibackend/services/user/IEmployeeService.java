package pwr.zpibackend.services.user;

import pwr.zpibackend.dto.user.EmployeeDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.user.Employee;

import java.util.List;

public interface IEmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployee(String mail);
    Employee getEmployee(Long id);
    Employee addEmployee(EmployeeDTO employee);
    Employee updateEmployee(Long id, EmployeeDTO updatedEmployee);
    Employee deleteEmployee(Long id) throws NotFoundException;
}
