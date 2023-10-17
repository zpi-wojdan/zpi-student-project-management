package pwr.zpibackend.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.repositories.EmployeeRepository;
import pwr.zpibackend.services.EmployeeService;
import pwr.zpibackend.services.ThesisService;

import java.util.List;

@RestController
@RequestMapping("/thesis")
public class ThesisController {

    private final ThesisService thesisService;
    private final EmployeeService employeeService;

    public ThesisController(ThesisService thesisService, EmployeeService employeeService) {
        this.thesisService = thesisService;
        this.employeeService = employeeService;
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

    @PutMapping("/{id}")
    public ResponseEntity<Thesis> updateThesis(@PathVariable Long id, @RequestBody ObjectNode json) {
        if (!json.has("namePL") || !json.has("nameEN") || !json.has("description") || !json.has("num_people") ||
                !json.has("supervisorId") || !json.has("faculty") || !json.has("field") || !json.has("edu_cycle")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try{
            Long supervisorId = json.get("supervisorId").asLong();
            Employee supervisor = employeeService.getEmployee(supervisorId);
            if (supervisor == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Thesis thesis = thesisService.updateThesis(id, json.get("namePL").asText(), json.get("nameEN").asText(),
                    json.get("description").asText(), json.get("num_people").asInt(), supervisor,
                    json.get("faculty").asText(), json.get("field").asText(), json.get("edu_cycle").asText());
            return new ResponseEntity<>(thesis, HttpStatus.OK);
        }
        catch(NotFoundException err){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

}
