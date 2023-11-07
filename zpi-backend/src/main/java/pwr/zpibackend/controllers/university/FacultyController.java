package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.FacultyDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.services.university.FacultyService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @GetMapping("/{abbreviation}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable String abbreviation) {
        try {
            return ResponseEntity.ok(facultyService.getFacultyByAbbreviation(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> addFaculty(@RequestBody FacultyDTO faculty) {
        try{
            return ResponseEntity.ok(facultyService.saveFaculty(faculty));
        } catch(AlreadyExistsException err) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id, @RequestBody FacultyDTO faculty) {
        try {
            return ResponseEntity.ok(facultyService.updateFaculty(id, faculty));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(facultyService.deleteFaculty(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
