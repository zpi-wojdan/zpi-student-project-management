package pwr.zpibackend.controllers.thesis;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.thesis.StatusDTO;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.services.thesis.StatusService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/status")
public class StatusController {
    private final StatusService statusService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Status>> getAllStatuses() {
        return ResponseEntity.ok(statusService.getAllStatuses());
    }

    @GetMapping("/name")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Status> getStatusByName(String name) {
        return ResponseEntity.ok(statusService.getStatusByName(name));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Status> addStatus(StatusDTO status) {
        return ResponseEntity.ok(statusService.addStatus(status));
    }

    @PutMapping("/{statusId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Status> updateStatus(@PathVariable Long statusId, StatusDTO updatedStatus) {
        return ResponseEntity.ok(statusService.updateStatus(statusId, updatedStatus));
    }

    @DeleteMapping("/{statusId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Status> deleteStatus(@PathVariable Long statusId) {
        return ResponseEntity.ok(statusService.deleteStatus(statusId));
    }
}
