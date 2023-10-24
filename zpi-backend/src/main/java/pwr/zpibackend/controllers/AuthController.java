package pwr.zpibackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pwr.zpibackend.exceptions.EmployeeAndStudentWithTheSameEmailException;
import pwr.zpibackend.services.AuthService;

@RestController
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Get logged user details", description = "Returns logged user details based on email")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{email}/details")
    public ResponseEntity<Object> getUserDetails(@PathVariable String email) throws EmployeeAndStudentWithTheSameEmailException {
        return ResponseEntity.ok(authService.getUserDetails(email));
    }

}
