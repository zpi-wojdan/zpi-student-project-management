package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.DeadlineDTO;
import pwr.zpibackend.models.university.Deadline;
import pwr.zpibackend.services.university.DeadlineService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/deadline")
public class DeadlineController {

    private final DeadlineService deadlineService;

    @GetMapping
    public ResponseEntity<List<Deadline>> getAllDeadlines() {
        return ResponseEntity.ok(deadlineService.getAllDeadlines());
    }

    @GetMapping("/ordered")
    public ResponseEntity<List<Deadline>> getAllDeadlinesOrderedByDateAsc() {
        return ResponseEntity.ok(deadlineService.getAllDeadlinesOrderedByDateAsc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deadline> getDeadline(@PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.getDeadline(id));
    }

    @PostMapping
    public ResponseEntity<Deadline> addDeadline(@RequestBody DeadlineDTO deadline) {
        return new ResponseEntity<>(deadlineService.addDeadline(deadline), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deadline> updateDeadline(@PathVariable Long id, @RequestBody DeadlineDTO updatedDeadline) {
        return ResponseEntity.ok(deadlineService.updateDeadline(id, updatedDeadline));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Deadline> deleteDeadline(@PathVariable Long id) {
        return ResponseEntity.ok(deadlineService.deleteDeadline(id));
    }
}
