package pwr.zpibackend.controllers.university;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.DeadlineDTO;
import pwr.zpibackend.models.university.Deadline;
import pwr.zpibackend.services.university.IDeadlineService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/deadline")
public class DeadlineController {

    private final IDeadlineService deadlineService;

    @GetMapping
    @Operation(summary = "Get all deadlines", description = "Returns list of all deadlines.")
    public ResponseEntity<List<Deadline>> getAllDeadlines() {
        return ResponseEntity.ok(deadlineService.getAllDeadlines());
    }

    @GetMapping("/ordered")
    @Operation(summary = "Get all deadlines ordered by date ascending",
            description = "Returns list of all deadlines ordered by date ascending.")
    public ResponseEntity<List<Deadline>> getAllDeadlinesOrderedByDateAsc() {
        return ResponseEntity.ok(deadlineService.getAllDeadlinesOrderedByDateAsc());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get deadline by id", description = "Returns deadline with given id.")
    public ResponseEntity<Deadline> getDeadline(@PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.getDeadline(id));
    }

    @PostMapping
    @Operation(summary = "Add deadline", description = "Adds deadline to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Deadline> addDeadline(@RequestBody DeadlineDTO deadline) {
        return new ResponseEntity<>(deadlineService.addDeadline(deadline), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update deadline", description = "Updates deadline with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Deadline> updateDeadline(@PathVariable Long id, @RequestBody DeadlineDTO updatedDeadline) {
        return ResponseEntity.ok(deadlineService.updateDeadline(id, updatedDeadline));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete deadline", description = "Deletes deadline with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Deadline> deleteDeadline(@PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.deleteDeadline(id));
    }
}
