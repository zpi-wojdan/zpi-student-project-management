package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.services.university.StudyCycleService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/studycycle")
public class StudyCycleController {

    private final StudyCycleService studyCycleService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StudyCycle>> getAllStudyCycles(){
        return ResponseEntity.ok(studyCycleService.getAllStudyCycles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StudyCycle> getStudyCycleById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(studyCycleService.getStudyCycleById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyCycle> createStudyCycle(@RequestBody StudyCycle studyCycle){
        return ResponseEntity.ok(studyCycleService.saveStudyCycle(studyCycle));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyCycle> updateStudyCycle(@RequestBody StudyCycle studyCycle, @PathVariable Long id){
        try {
            return ResponseEntity.ok(studyCycleService.updateStudyCycle(id, studyCycle));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyCycle> deleteStudyCycle(@PathVariable Long id){
        try {
            return ResponseEntity.ok(studyCycleService.deleteStudyCycle(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
