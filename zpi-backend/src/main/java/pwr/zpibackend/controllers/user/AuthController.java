package pwr.zpibackend.controllers.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pwr.zpibackend.services.user.IAuthService;

@RestController
@AllArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @GetMapping("/user/{email}/details")
    @Operation(summary = "Get logged in user details",
            description = "Returns logged user details based on email. <br>Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getUserDetails(@PathVariable String email) {
        return ResponseEntity.ok(authService.getUserDetails(email));
    }

}
