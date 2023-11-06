package pwr.zpibackend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.EmployeeDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.CannotDeleteException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.services.EmployeeService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        return new ResponseEntity<>(employeeService.getEmployee(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
    }

    @GetMapping("/match/{prefix}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Employee>> getEmployeesByPrefix(@PathVariable String prefix) {
        List<Employee> matchingEmployees = employeeService.getEmployeesByPrefix(prefix.toLowerCase());
        return new ResponseEntity<>(matchingEmployees, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Employee> addEmployee(@RequestBody EmployeeDTO employee) throws NotFoundException {
        try {
            return new ResponseEntity<>(employeeService.addEmployee(employee), HttpStatus.CREATED);
        } catch (AlreadyExistsException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String id, @RequestBody EmployeeDTO updatedEmployee)
            throws NotFoundException {
        return new ResponseEntity<>(employeeService.updateEmployee(id, updatedEmployee), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Employee> deleteEmployee(@PathVariable String id) throws NotFoundException,
            CannotDeleteException {
        return new ResponseEntity<>(employeeService.deleteEmployee(id), HttpStatus.OK);
    }

}
