package pwr.zpibackend.controllers.university;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.FacultyDTO;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.services.university.IFacultyService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/faculty")
public class FacultyController {

    private final IFacultyService facultyService;

    @GetMapping("")
    @Operation(summary = "Get all faculties", description = "Returns list of all faculties. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @GetMapping("/{abbreviation}")
    @Operation(summary = "Get faculty by abbreviation", description = "Returns faculty with given abbreviation. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable String abbreviation) {
        return ResponseEntity.ok(facultyService.getFacultyByAbbreviation(abbreviation));
    }

    @PostMapping("")
    @Operation(summary = "Add faculty", description = "Adds faculty to database. <br>" +
            "Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> addFaculty(@RequestBody FacultyDTO faculty) {
        return ResponseEntity.ok(facultyService.saveFaculty(faculty));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update faculty", description = "Updates faculty with given id. <br>" +
            "Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id, @RequestBody FacultyDTO faculty) {
        return ResponseEntity.ok(facultyService.updateFaculty(id, faculty));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete faculty", description = "Deletes faculty with given id. <br>" +
            "Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.deleteFaculty(id));
    }

    @GetMapping("/ordered")
    @Operation(summary = "Get all faculties ordered by abbreviation ascending",
            description = "Returns list of all faculties ordered by abbreviation ascending. <br>" +
                    "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Faculty>> getAllFacultiesOrderedByAbbreviation() {
        return ResponseEntity.ok(facultyService.getAllFacultiesOrderedByAbbreviationAsc());
    }
}
