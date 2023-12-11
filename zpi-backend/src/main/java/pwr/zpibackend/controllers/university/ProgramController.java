package pwr.zpibackend.controllers.university;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.ProgramDTO;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.services.university.IProgramService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/program")
public class ProgramController {

    private final IProgramService programService;

    @GetMapping("")
    @Operation(summary = "Get all programs", description = "Returns list of all programs. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Program>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get program by id", description = "Returns program with given id. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Program> getProgramById(@PathVariable Long id) {
        return ResponseEntity.ok(programService.getProgramById(id));
    }

    @PostMapping("")
    @Operation(summary = "Add program", description = "Adds program to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Program> addProgram(@RequestBody ProgramDTO program) {
        return ResponseEntity.ok(programService.saveProgram(program));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update program", description = "Updates program with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Program> updateProgram(@PathVariable Long id, @RequestBody ProgramDTO program) {
        return ResponseEntity.ok(programService.updateProgram(id, program));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete program", description = "Deletes program with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Program> deleteProgram(@PathVariable Long id) {
        return ResponseEntity.ok(programService.deleteProgram(id));
    }
}
