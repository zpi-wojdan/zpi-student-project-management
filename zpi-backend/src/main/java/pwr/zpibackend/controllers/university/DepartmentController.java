package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.dto.DepartmentDTO;
import pwr.zpibackend.services.university.DepartmentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{code}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable String code) {
        try {
            return ResponseEntity.ok(departmentService.getDepartmentByCode(code));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<Department> addDepartment(@RequestBody DepartmentDTO department) {
        return ResponseEntity.ok(departmentService.addDepartment(department));
    }

    @PutMapping("/{code}")
    public ResponseEntity<Department> updateDepartment(@PathVariable String code, @RequestBody DepartmentDTO department) {
        try {
            return ResponseEntity.ok(departmentService.updateDepartment(code, department));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Department> deleteDepartment(@PathVariable String code) {
        try {
            return ResponseEntity.ok(departmentService.deleteDepartment(code));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
