package pwr.zpibackend.controllers.thesis;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.thesis.ThesisDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.services.thesis.ThesisService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/thesis")
public class ThesisController {

    private final ThesisService thesisService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllTheses() {
        return new ResponseEntity<>(thesisService.getAllTheses(), HttpStatus.OK);
    }

    @GetMapping("/public")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllPublicTheses() {
        return new ResponseEntity<>(thesisService.getAllPublicTheses(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Thesis> getThesisById(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.getThesis(id), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Thesis> addThesis(@RequestBody ThesisDTO thesis) throws NotFoundException {
        return new ResponseEntity<>(thesisService.addThesis(thesis), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<Thesis> updateThesis(@PathVariable Long id, @RequestBody ThesisDTO param) {
        return new ResponseEntity<>(thesisService.updateThesis(id, param), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Thesis> deleteThesis(@PathVariable Long id) throws NotFoundException {
        return new ResponseEntity<>(thesisService.deleteThesis(id), HttpStatus.OK);
    }

    @GetMapping("/status/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesByStatusId(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.getAllThesesByStatusId(id), HttpStatus.OK);
    }

    @GetMapping("/{empId}/{statId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesForEmployeeByStatusId(@PathVariable Long empId,
                                                                        @PathVariable Long statId) {
        return new ResponseEntity<>(thesisService.getAllThesesForEmployeeByStatusId(empId, statId), HttpStatus.OK);
    }

    @GetMapping("/employee/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesForEmployee(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.getAllThesesForEmployee(id), HttpStatus.OK);
    }
}
