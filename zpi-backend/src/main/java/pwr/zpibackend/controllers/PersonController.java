package pwr.zpibackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import pwr.zpibackend.models.Person;
import pwr.zpibackend.repositories.PersonRepository;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @PostMapping("/add")
    public void addEmployee(@RequestBody Person person) {
        personRepository.save(person);
    }

    @GetMapping("/get")
    public ResponseEntity<List<Person>> getAllPeople() {
        return ResponseEntity.ok(personRepository.findAll());
    }

}
