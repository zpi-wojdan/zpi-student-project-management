package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @GetMapping("/{abbreviation}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable String abbreviation) {
        try {
            return ResponseEntity.ok(facultyService.getFacultyByAbbreviation(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> addFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.saveFaculty(faculty));
    }

    @PutMapping("/{abbreviation}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable String abbreviation, @RequestBody Faculty faculty) {
        try {
            return ResponseEntity.ok(facultyService.updateFaculty(abbreviation, faculty));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{abbreviation}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable String abbreviation) {
        try {
            return ResponseEntity.ok(facultyService.deleteFaculty(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
