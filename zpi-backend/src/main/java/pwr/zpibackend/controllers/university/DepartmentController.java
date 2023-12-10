package pwr.zpibackend.controllers.university;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.dto.university.DepartmentDTO;
import pwr.zpibackend.services.university.IDepartmentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {

    private final IDepartmentService departmentService;

    @GetMapping("")
    @Operation(summary = "Get all departments", description = "Returns list of all departments. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get department by code", description = "Returns department with given code. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Department> getDepartmentById(@PathVariable String code) {
        return ResponseEntity.ok(departmentService.getDepartmentByCode(code));
    }

    @PostMapping("")
    @Operation(summary = "Add department", description = "Adds department to database. <br>" +
            "Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Department> addDepartment(@RequestBody DepartmentDTO department) {
        return ResponseEntity.ok(departmentService.addDepartment(department));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department", description = "Updates department with given id. <br>" +
            "Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO department) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, department));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department", description = "Deletes department with given id. <br>" +
            "Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Department> deleteDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.deleteDepartment(id));
    }
}
