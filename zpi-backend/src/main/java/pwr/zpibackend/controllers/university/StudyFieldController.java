package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.services.university.StudyFieldService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/studyfield")
public class StudyFieldController {

    private final StudyFieldService studyFieldService;

    @GetMapping("")
    public ResponseEntity<List<StudyField>> getAllStudyFields(){
        return ResponseEntity.ok(studyFieldService.getAllStudyFields());
    }

    @GetMapping("/{abbreviation}")
    public ResponseEntity<StudyField> getStudyFieldByAbbreviation(@PathVariable String abbreviation){
        try {
            return ResponseEntity.ok(studyFieldService.getStudyFieldByAbbreviation(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<StudyField> createStudyField(@RequestBody StudyField studyField){
        return ResponseEntity.ok(studyFieldService.saveStudyField(studyField));
    }

    @PutMapping("/{abbreviation}")
    public ResponseEntity<StudyField> updateStudyField(@RequestBody StudyField studyField, @PathVariable String abbreviation){
        try {
            return ResponseEntity.ok(studyFieldService.updateStudyField(abbreviation, studyField));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{abbreviation}")
    public ResponseEntity<StudyField> deleteStudyField(@PathVariable String abbreviation){
        try {
            return ResponseEntity.ok(studyFieldService.deleteStudyField(abbreviation));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
