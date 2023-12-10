package pwr.zpibackend.controllers.university;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.StudyCycleDTO;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.services.university.IStudyCycleService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/studycycle")
public class StudyCycleController {

    private final IStudyCycleService studyCycleService;

    @GetMapping("")
    @Operation(summary = "Get all study cycles", description = "Returns list of all study cycles. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StudyCycle>> getAllStudyCycles(){
        return ResponseEntity.ok(studyCycleService.getAllStudyCycles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get study cycle by id", description = "Returns study cycle with given id. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StudyCycle> getStudyCycleById(@PathVariable Long id){
        return ResponseEntity.ok(studyCycleService.getStudyCycleById(id));
    }

    @PostMapping("")
    @Operation(summary = "Add study cycle", description = "Adds study cycle to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyCycle> createStudyCycle(@RequestBody StudyCycleDTO studyCycle){
        return ResponseEntity.ok(studyCycleService.saveStudyCycle(studyCycle));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update study cycle",
            description = "Updates study cycle with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyCycle> updateStudyCycle(@RequestBody StudyCycleDTO studyCycle, @PathVariable Long id){
        return ResponseEntity.ok(studyCycleService.updateStudyCycle(id, studyCycle));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete study cycle",
            description = "Deletes study cycle with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyCycle> deleteStudyCycle(@PathVariable Long id){
        return ResponseEntity.ok(studyCycleService.deleteStudyCycle(id));
    }
}
