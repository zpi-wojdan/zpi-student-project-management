package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.dto.university.DepartmentDTO;
import pwr.zpibackend.services.university.DepartmentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Department> getDepartmentById(@PathVariable String code) {
        try {
            return ResponseEntity.ok(departmentService.getDepartmentByCode(code));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Department> addDepartment(@RequestBody DepartmentDTO department) {
        try{
            return ResponseEntity.ok(departmentService.addDepartment(department));
        } catch(AlreadyExistsException err) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO department) {
        try {
            return ResponseEntity.ok(departmentService.updateDepartment(id, department));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Department> deleteDepartment(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(departmentService.deleteDepartment(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
