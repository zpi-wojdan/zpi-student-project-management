package pwr.zpibackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.repositories.EmployeeRepository;

@RestController
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @PostMapping("/add")
    public void addEmployee(@RequestBody Employee employee) {
        employeeRepository.save(employee);
    }

}
