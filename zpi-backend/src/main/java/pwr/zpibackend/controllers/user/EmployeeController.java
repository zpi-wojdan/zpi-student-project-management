package pwr.zpibackend.controllers.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.user.EmployeeDTO;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.services.user.IEmployeeService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {

    private final IEmployeeService employeeService;

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by id",
            description = "Returns employee with given id. <br>Requires ADMIN or SUPERVISOR role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return new ResponseEntity<>(employeeService.getEmployee(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all employees",
            description = "Returns list of all employees. <br>Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Add employee", description = "Adds employee to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Employee> addEmployee(@RequestBody EmployeeDTO employee) {
        return new ResponseEntity<>(employeeService.addEmployee(employee), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee", description = "Updates employee with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO updatedEmployee) {
        return new ResponseEntity<>(employeeService.updateEmployee(id, updatedEmployee), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee", description = "Deletes employee with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Employee> deleteEmployee(@PathVariable Long id) {
        return new ResponseEntity<>(employeeService.deleteEmployee(id), HttpStatus.OK);
    }

}
