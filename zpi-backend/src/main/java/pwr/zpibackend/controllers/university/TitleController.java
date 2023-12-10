package pwr.zpibackend.controllers.university;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.TitleDTO;
import pwr.zpibackend.models.university.Title;
import pwr.zpibackend.services.university.ITitleService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/title")
public class TitleController {
    private final ITitleService titleService;

    @GetMapping("")
    @Operation(summary = "Get all titles", description = "Returns list of all titles. <br>Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Title>> getAllTitles() {
        return ResponseEntity.ok(titleService.getAllTitles());
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get title by name",
            description = "Returns title with given name. <br>Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Title> getTitleByName(@PathVariable String name) {
        return ResponseEntity.ok(titleService.getTitleByName(name));
    }

    @PostMapping("")
    @Operation(summary = "Add title", description = "Adds title to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Title> addTitle(@RequestBody TitleDTO title) {
        return ResponseEntity.ok(titleService.addTitle(title));
    }

    @PutMapping("/{titleId}")
    @Operation(summary = "Update title", description = "Updates title with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Title> updateTitle(@PathVariable Long titleId, @RequestBody TitleDTO updatedTitle) {
        return ResponseEntity.ok(titleService.updateTitle(titleId, updatedTitle));
    }

    @DeleteMapping("/{titleId}")
    @Operation(summary = "Delete title", description = "Deletes title with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Title> deleteTitle(@PathVariable Long titleId) {
        return ResponseEntity.ok(titleService.deleteTitle(titleId));
    }
}
