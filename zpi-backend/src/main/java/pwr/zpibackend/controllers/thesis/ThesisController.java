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

import java.util.ArrayList;
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

    @GetMapping("student/{id}")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    public ResponseEntity<Thesis> getThesisByStudentId(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.getThesisByStudentId(id), HttpStatus.OK);
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

    @GetMapping("/status/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesByStatusName(@PathVariable String name) {
        String realName = name.replaceAll("_", " ");
        return new ResponseEntity<>(thesisService.getAllThesesByStatusName(realName), HttpStatus.OK);
    }

    @GetMapping("/status/exclude/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesExcludingStatusName(@PathVariable String name) {
        String realName = name.replaceAll("_", " ");
        return new ResponseEntity<>(thesisService.getAllThesesExcludingStatusName(realName), HttpStatus.OK);
    }

    @GetMapping("/{empId}/{statName}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesForEmployeeByStatusName(@PathVariable Long empId,
                                                                            @PathVariable String statName) {
        String realName = statName.replaceAll("_", " ");
        return new ResponseEntity<>(thesisService.getAllThesesForEmployeeByStatusName(empId, realName), HttpStatus.OK);
    }

    @GetMapping("/employee/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesForEmployee(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.getAllThesesForEmployee(id), HttpStatus.OK);
    }

    @GetMapping("/employee/{empId}/statuses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesForEmployeeByStatusNameList(@PathVariable Long empId,
                                                                            @RequestParam List<String> statName) {
        List<String> fixedNames = new ArrayList<>();
        for (String name : statName) {
            fixedNames.add(name.replaceAll("_", " "));
        }
        return new ResponseEntity<>(thesisService.getAllThesesForEmployeeByStatusNameList(empId, fixedNames), HttpStatus.OK);
    }


    @PutMapping("/bulk/{statName}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_APPROVER')")
    public ResponseEntity<List<Thesis>> updateThesesStatusInBulk(@PathVariable String statName, @RequestBody List<Long> thesesIds) {
        String realName = statName.replaceAll("_", " ");
        return new ResponseEntity<>(thesisService.updateThesesStatusInBulk(realName, thesesIds), HttpStatus.OK);
    }

    @DeleteMapping("/bulk/cycle/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Thesis>> deleteThesisByStudyCycle(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.deleteThesesByStudyCycle(id), HttpStatus.OK);
    }

    @PutMapping("/bulk")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Thesis>> deleteThesesInBulk(@RequestBody List<Long> thesesIds) {
        return new ResponseEntity<>(thesisService.deleteThesesInBulk(thesesIds), HttpStatus.OK);
    }

}
