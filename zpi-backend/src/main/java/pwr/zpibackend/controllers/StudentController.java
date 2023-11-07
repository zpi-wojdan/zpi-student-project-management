package pwr.zpibackend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pwr.zpibackend.dto.StudentDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.services.StudentService;

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
        try{
            return new ResponseEntity<>(studentService.getStudent(id), HttpStatus.OK);
        }
        catch(NotFoundException err){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Student> addStudent(@RequestBody StudentDTO student)
    {
        try{
            return new ResponseEntity<>(studentService.addStudent(student), HttpStatus.CREATED);
        }
        catch(AlreadyExistsException err){
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody StudentDTO updatedStudent) {
        try{
            return new ResponseEntity<>(studentService.updateStudent(id, updatedStudent), HttpStatus.OK);
        }
        catch(NotFoundException err){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        try{
            return new ResponseEntity<>(studentService.deleteStudent(id), HttpStatus.OK);
        }
        catch(NotFoundException err){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
