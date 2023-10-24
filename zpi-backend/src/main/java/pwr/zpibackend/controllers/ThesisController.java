package pwr.zpibackend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.services.ThesisService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/thesis")
public class ThesisController {

    private final ThesisService thesisService;

    @GetMapping
    public ResponseEntity<List<Thesis>> getAllTheses() {
        return new ResponseEntity<>(thesisService.getAllTheses(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Thesis> getThesisById(@PathVariable Long id) {
        try{
            return new ResponseEntity<>(thesisService.getThesis(id), HttpStatus.OK);
        }
        catch(NotFoundException err){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Thesis> addThesis(@RequestBody Thesis thesis) throws NotFoundException {
        return new ResponseEntity<>(thesisService.addThesis(thesis), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Thesis> updateThesis(@PathVariable Long id, @RequestBody Thesis param) {
        try{
            return new ResponseEntity<>(thesisService.updateThesis(id, param), HttpStatus.OK);
        }
        catch(NotFoundException err){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

}
