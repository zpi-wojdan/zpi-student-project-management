package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.exceptions.AlreadyExistsException;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        return ResponseEntity.ok(specialisationService.getAllSpecializations());
    }

    @GetMapping("/{abbreviation}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Specialization> getSpecializationByAbbreviation(@PathVariable String abbreviation) {
        try {
            return ResponseEntity.ok(specialisationService.getSpecializationByAbbreviation(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Specialization> addSpecialization(@RequestBody Specialization specialization) {
        try{
            return ResponseEntity.ok(specialisationService.saveSpecialization(specialization));
        } catch(AlreadyExistsException err) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{abbreviation}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Specialization> updateSpecialization(@PathVariable String abbreviation, @RequestBody Specialization specialization) {
        try {
            return ResponseEntity.ok(specialisationService.updateSpecialization(abbreviation, specialization));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{abbreviation}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Specialization> deleteSpecialization(@PathVariable String abbreviation) {
        try {
            return ResponseEntity.ok(specialisationService.deleteSpecialization(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
