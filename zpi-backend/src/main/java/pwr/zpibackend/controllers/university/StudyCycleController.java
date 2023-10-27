package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<StudyCycle>> getAllStudyCycles(){
        return ResponseEntity.ok(studyCycleService.getAllStudyCycles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyCycle> getStudyCycleById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(studyCycleService.getStudyCycleById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<StudyCycle> createStudyCycle(@RequestBody StudyCycle studyCycle){
        return ResponseEntity.ok(studyCycleService.saveStudyCycle(studyCycle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyCycle> updateStudyCycle(@RequestBody StudyCycle studyCycle, @PathVariable Long id){
        try {
            return ResponseEntity.ok(studyCycleService.updateStudyCycle(id, studyCycle));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StudyCycle> deleteStudyCycle(@PathVariable Long id){
        try {
            return ResponseEntity.ok(studyCycleService.deleteStudyCycle(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
