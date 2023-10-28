package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @GetMapping("/{abbreviation}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable String abbreviation) {
        try {
            return ResponseEntity.ok(facultyService.getFacultyByAbbreviation(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<Faculty> addFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.saveFaculty(faculty));
    }

    @PutMapping("/{abbreviation}")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable String abbreviation, @RequestBody Faculty faculty) {
        try {
            return ResponseEntity.ok(facultyService.updateFaculty(abbreviation, faculty));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{abbreviation}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable String abbreviation) {
        try {
            return ResponseEntity.ok(facultyService.deleteFaculty(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
