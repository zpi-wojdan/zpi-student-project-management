package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.StudyFieldDTO;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.services.university.StudyFieldService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/studyfield")
public class StudyFieldController {

    private final StudyFieldService studyFieldService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StudyField>> getAllStudyFields(){
        return ResponseEntity.ok(studyFieldService.getAllStudyFields());
    }

    @GetMapping("/{abbreviation}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StudyField> getStudyFieldByAbbreviation(@PathVariable String abbreviation){
        return ResponseEntity.ok(studyFieldService.getStudyFieldByAbbreviation(abbreviation));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyField> createStudyField(@RequestBody StudyFieldDTO studyField){
        return ResponseEntity.ok(studyFieldService.saveStudyField(studyField));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyField> updateStudyField(@RequestBody StudyFieldDTO studyField, @PathVariable Long id){
        return ResponseEntity.ok(studyFieldService.updateStudyField(id, studyField));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyField> deleteStudyField(@PathVariable Long id){
        return ResponseEntity.ok(studyFieldService.deleteStudyField(id));
    }

    @GetMapping("/ordered")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StudyField>> getAllStudyFieldsOrderedByAbbreviation(){
        return ResponseEntity.ok(studyFieldService.getAllStudyFieldsOrderedByAbbreviationAsc());
    }
}
