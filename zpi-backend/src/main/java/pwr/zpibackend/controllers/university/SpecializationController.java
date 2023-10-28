package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Specialization;
import pwr.zpibackend.services.university.SpecialisationService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/specialization")
public class SpecializationController {

    private final SpecialisationService specialisationService;

    @GetMapping("")
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        return ResponseEntity.ok(specialisationService.getAllSpecializations());
    }

    @GetMapping("/{abbreviation}")
    public ResponseEntity<Specialization> getSpecializationByAbbreviation(@PathVariable String abbreviation) {
        try {
            return ResponseEntity.ok(specialisationService.getSpecializationByAbbreviation(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<Specialization> addSpecialization(@RequestBody Specialization specialization) {
        return ResponseEntity.ok(specialisationService.saveSpecialization(specialization));
    }

    @PutMapping("/{abbreviation}")
    public ResponseEntity<Specialization> updateSpecialization(@PathVariable String abbreviation, @RequestBody Specialization specialization) {
        try {
            return ResponseEntity.ok(specialisationService.updateSpecialization(abbreviation, specialization));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{abbreviation}")
    public ResponseEntity<Specialization> deleteSpecialization(@PathVariable String abbreviation) {
        try {
            return ResponseEntity.ok(specialisationService.deleteSpecialization(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
