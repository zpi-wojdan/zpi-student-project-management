package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.SpecializationDTO;
import pwr.zpibackend.models.university.Specialization;
import pwr.zpibackend.services.university.ISpecializationService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/specialization")
public class SpecializationController {

    private final ISpecializationService specializationService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        return ResponseEntity.ok(specializationService.getAllSpecializations());
    }

    @GetMapping("/{abbreviation}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Specialization> getSpecializationByAbbreviation(@PathVariable String abbreviation) {
        return ResponseEntity.ok(specializationService.getSpecializationByAbbreviation(abbreviation));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Specialization> addSpecialization(@RequestBody SpecializationDTO specialization) {
        return ResponseEntity.ok(specializationService.saveSpecialization(specialization));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Specialization> updateSpecialization(@PathVariable Long id, @RequestBody SpecializationDTO specialization) {
        return ResponseEntity.ok(specializationService.updateSpecialization(id, specialization));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Specialization> deleteSpecialization(@PathVariable Long id) {
        return ResponseEntity.ok(specializationService.deleteSpecialization(id));
    }
}
