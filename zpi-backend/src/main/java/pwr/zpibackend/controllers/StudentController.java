package pwr.zpibackend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Student>> getAllStudents() {
        return new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK);
    }

    @GetMapping("/{mail}")
    public ResponseEntity<Student> getStudentById(@PathVariable String mail) {
        try{
            return new ResponseEntity<>(studentService.getStudent(mail), HttpStatus.OK);
        }
        catch(NotFoundException err){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping
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

    @PutMapping("/{mail}")
    public ResponseEntity<Student> updateStudent(@PathVariable String mail, @RequestBody StudentDTO updatedStudent) {
        try{
            return new ResponseEntity<>(studentService.updateStudent(mail, updatedStudent), HttpStatus.OK);
        }
        catch(NotFoundException err){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{mail}")
    public ResponseEntity<Student> deleteStudent(@PathVariable String mail) {
        try{
            return new ResponseEntity<>(studentService.deleteStudent(mail), HttpStatus.OK);
        }
        catch(NotFoundException err){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
