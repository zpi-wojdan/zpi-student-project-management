package pwr.zpibackend.controllers.thesis;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.thesis.StatusDTO;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.services.thesis.IStatusService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/status")
public class StatusController {
    private final IStatusService statusService;

    @GetMapping("")
    @Operation(summary = "Get all statuses", description = "Returns list of all statuses. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Status>> getAllStatuses() {
        return ResponseEntity.ok(statusService.getAllStatuses());
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get status by name", description = "Returns status with given name. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Status> getStatusByName(@PathVariable String name) {
        return ResponseEntity.ok(statusService.getStatusByName(name));
    }

    @GetMapping("/exclude/{name}")
    @Operation(summary = "Get all statuses without given name",
            description = "Returns list of all statuses without given name. <br>Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Status>> getAllStatusesWithoutName(@PathVariable String name) {
        return ResponseEntity.ok(statusService.getAllStatusesWithoutName(name));
    }

    @PostMapping("")
    @Operation(summary = "Add status", description = "Adds status to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Status> addStatus(@RequestBody StatusDTO status) {
        return ResponseEntity.ok(statusService.addStatus(status));
    }

    @PutMapping("/{statusId}")
    @Operation(summary = "Update status", description = "Updates status with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Status> updateStatus(@PathVariable Long statusId, @RequestBody StatusDTO updatedStatus) {
        return ResponseEntity.ok(statusService.updateStatus(statusId, updatedStatus));
    }

    @DeleteMapping("/{statusId}")
    @Operation(summary = "Delete status", description = "Deletes status with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Status> deleteStatus(@PathVariable Long statusId) {
        return ResponseEntity.ok(statusService.deleteStatus(statusId));
    }
}
