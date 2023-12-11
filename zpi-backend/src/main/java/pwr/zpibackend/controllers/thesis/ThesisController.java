package pwr.zpibackend.controllers.thesis;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.thesis.ThesisDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.services.thesis.IThesisService;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/thesis")
public class ThesisController {

    private final IThesisService thesisService;

    @GetMapping
    @Operation(summary = "Get all theses", description = "Returns list of all theses. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllTheses() {
        return new ResponseEntity<>(thesisService.getAllTheses(), HttpStatus.OK);
    }

    @GetMapping("/public")
    @Operation(summary = "Get all public theses", description = "Returns list of all public theses. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllPublicTheses() {
        return new ResponseEntity<>(thesisService.getAllPublicTheses(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get thesis by id", description = "Returns thesis with given id. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Thesis> getThesisById(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.getThesis(id), HttpStatus.OK);
    }

    @GetMapping("student/{id}")
    @Operation(summary = "Get thesis by student id", description = "Returns thesis with given student id. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    public ResponseEntity<Thesis> getThesisByStudentId(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.getThesisByStudentId(id), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Add thesis", description = "Adds thesis to database. <br>" +
            "Requires ADMIN or SUPERVISOR role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Thesis> addThesis(@RequestBody ThesisDTO thesis) throws NotFoundException {
        return new ResponseEntity<>(thesisService.addThesis(thesis), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update thesis", description = "Updates thesis with given id. <br>" +
            "Requires ADMIN or SUPERVISOR role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<Thesis> updateThesis(@PathVariable Long id, @RequestBody ThesisDTO param) {
        return new ResponseEntity<>(thesisService.updateThesis(id, param), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete thesis", description = "Deletes thesis with given id. <br>" +
            "Requires ADMIN or SUPERVISOR role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Thesis> deleteThesis(@PathVariable Long id) throws NotFoundException {
        return new ResponseEntity<>(thesisService.deleteThesis(id), HttpStatus.OK);
    }

    @GetMapping("/status/{name}")
    @Operation(summary = "Get all theses by status name",
            description = "Returns list of all theses with given status name. <br>Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesByStatusName(@PathVariable String name) {
        String realName = name.replaceAll("_", " ");
        return new ResponseEntity<>(thesisService.getAllThesesByStatusName(realName), HttpStatus.OK);
    }

    @GetMapping("/status/exclude/{name}")
    @Operation(summary = "Get all theses excluding particular status name",
            description = "Returns list of all theses excluding given status name. <br>Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesExcludingStatusName(@PathVariable String name) {
        String realName = name.replaceAll("_", " ");
        return new ResponseEntity<>(thesisService.getAllThesesExcludingStatusName(realName), HttpStatus.OK);
    }

    @GetMapping("/{empId}/{statName}")
    @Operation(summary = "Get all theses for employee by status name",
            description = "Returns list of all theses for employee with given status name. <br>" +
                    "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesForEmployeeByStatusName(@PathVariable Long empId,
                                                                            @PathVariable String statName) {
        String realName = statName.replaceAll("_", " ");
        return new ResponseEntity<>(thesisService.getAllThesesForEmployeeByStatusName(empId, realName), HttpStatus.OK);
    }

    @GetMapping("/employee/{id}")
    @Operation(summary = "Get all theses for employee",
            description = "Returns list of all theses for employee with given id. <br>Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Thesis>> getAllThesesForEmployee(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.getAllThesesForEmployee(id), HttpStatus.OK);
    }

    @GetMapping("/employee/{empId}/statuses")
    @Operation(summary = "Get all theses for employee by status name list",
            description = "Returns list of all theses for employee with the status names from the list. <br>" +
                    "Requires authenticated user.")
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
    @Operation(summary = "Update theses status in bulk",
            description = "Updates theses status in bulk. <br>Requires ADMIN or APPROVER role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_APPROVER')")
    public ResponseEntity<List<Thesis>> updateThesesStatusInBulk(@PathVariable String statName, @RequestBody List<Long> thesesIds) {
        String realName = statName.replaceAll("_", " ");
        return new ResponseEntity<>(thesisService.updateThesesStatusInBulk(realName, thesesIds), HttpStatus.OK);
    }

    @DeleteMapping("/bulk/cycle/{id}")
    @Operation(summary = "Delete theses by study cycle",
            description = "Deletes theses by study cycle. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Thesis>> deleteThesisByStudyCycle(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.deleteThesesByStudyCycle(id), HttpStatus.OK);
    }

    @PutMapping("/bulk")
    @Operation(summary = "Update theses in bulk", description = "Updates theses in bulk. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Thesis>> deleteThesesInBulk(@RequestBody List<Long> thesesIds) {
        return new ResponseEntity<>(thesisService.deleteThesesInBulk(thesesIds), HttpStatus.OK);
    }

}
