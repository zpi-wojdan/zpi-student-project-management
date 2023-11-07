package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.ProgramDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.services.university.ProgramService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/program")
public class ProgramController {

    private final ProgramService programService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Program>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Program> getProgramById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(programService.getProgramById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Program> addProgram(@RequestBody ProgramDTO program) {
        try {
            return ResponseEntity.ok(programService.saveProgram(program));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Program> updateProgram(@PathVariable Long id, @RequestBody ProgramDTO program) {
        try {
            return ResponseEntity.ok(programService.updateProgram(id, program));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Program> deleteProgram(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(programService.deleteProgram(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
