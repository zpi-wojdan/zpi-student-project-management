package pwr.zpibackend.controllers.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.user.StudentDTO;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.services.user.IStudentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final IStudentService studentService;

    @GetMapping
    @Operation(summary = "Get all students", description = "Returns list of all students. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Student>> getAllStudents() {
        return new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by id", description = "Returns student with given id. <br>" +
            "Requires ADMIN, STUDENT or SUPERVISOR role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getStudent(id), HttpStatus.OK);
    }

    @GetMapping("/index/{index}")
    @Operation(summary = "Get student by index", description = "Returns student with given index. <br>" +
            "Requires ADMIN, STUDENT or SUPERVISOR role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Student> getStudentByIndex(@PathVariable String index) {
        return new ResponseEntity<>(studentService.getStudent(index + "@student.pwr.edu.pl"), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Add student", description = "Adds student to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Student> addStudent(@RequestBody StudentDTO student) {
        return new ResponseEntity<>(studentService.addStudent(student), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student", description = "Updates student with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody StudentDTO updatedStudent) {
        return new ResponseEntity<>(studentService.updateStudent(id, updatedStudent), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student", description = "Deletes student with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.deleteStudent(id), HttpStatus.OK);
    }


    @PutMapping("/bulk/cycle/{cycleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Student>> deleteStudents(@PathVariable Long cycleId, @RequestBody List<Long> studentIds) {
        return new ResponseEntity<>(studentService.deleteStudentsInBulk(cycleId, studentIds), HttpStatus.OK);
    }

}
