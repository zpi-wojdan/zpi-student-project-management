package pwr.zpibackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.services.ThesisService;

import java.util.List;

@RestController
@RequestMapping("/thesis")
public class ThesisController {

    private final ThesisService thesisService;

    public ThesisController(ThesisService thesisService) {
        this.thesisService = thesisService;
    }

    @GetMapping
    public ResponseEntity<List<Thesis>> getAllTheses() {
        return new ResponseEntity<>(thesisService.getAllTheses(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Thesis> getThesisById(@PathVariable Long id) {
        return new ResponseEntity<>(thesisService.getThesis(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Thesis> addThesis(@RequestBody Thesis thesis)
    {
        return new ResponseEntity<>(thesisService.addThesis(thesis), HttpStatus.CREATED);
    }

}
