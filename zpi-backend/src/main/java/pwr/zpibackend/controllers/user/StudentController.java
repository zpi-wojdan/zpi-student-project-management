package pwr.zpibackend.controllers.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.user.StudentDTO;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.services.user.StudentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Student>> getAllStudents() {
        return new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getStudent(id), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Student> addStudent(@RequestBody StudentDTO student)
    {
        return new ResponseEntity<>(studentService.addStudent(student), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody StudentDTO updatedStudent) {
        return new ResponseEntity<>(studentService.updateStudent(id, updatedStudent), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.deleteStudent(id), HttpStatus.OK);
    }
}
