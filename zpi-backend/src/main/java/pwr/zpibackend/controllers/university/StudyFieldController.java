package pwr.zpibackend.controllers.university;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.StudyFieldDTO;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.services.university.IStudyFieldService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/studyfield")
public class StudyFieldController {

    private final IStudyFieldService studyFieldService;

    @GetMapping("")
    @Operation(summary = "Get all study fields", description = "Returns list of all study fields. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StudyField>> getAllStudyFields(){
        return ResponseEntity.ok(studyFieldService.getAllStudyFields());
    }

    @GetMapping("/{abbreviation}")
    @Operation(summary = "Get study field by abbreviation",
            description = "Returns study field with given abbreviation. <br>Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StudyField> getStudyFieldByAbbreviation(@PathVariable String abbreviation){
        return ResponseEntity.ok(studyFieldService.getStudyFieldByAbbreviation(abbreviation));
    }

    @PostMapping("")
    @Operation(summary = "Add study field",
            description = "Adds study field to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyField> createStudyField(@RequestBody StudyFieldDTO studyField){
        return ResponseEntity.ok(studyFieldService.saveStudyField(studyField));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update study field",
            description = "Updates study field with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyField> updateStudyField(@RequestBody StudyFieldDTO studyField, @PathVariable Long id){
        return ResponseEntity.ok(studyFieldService.updateStudyField(id, studyField));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete study field",
            description = "Deletes study field with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyField> deleteStudyField(@PathVariable Long id){
        return ResponseEntity.ok(studyFieldService.deleteStudyField(id));
    }

    @GetMapping("/ordered")
    @Operation(summary = "Get all study fields ordered by abbreviation",
            description = "Returns list of all study fields ordered by abbreviation ascending. <br>" +
                    "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StudyField>> getAllStudyFieldsOrderedByAbbreviation(){
        return ResponseEntity.ok(studyFieldService.getAllStudyFieldsOrderedByAbbreviationAsc());
    }
}
