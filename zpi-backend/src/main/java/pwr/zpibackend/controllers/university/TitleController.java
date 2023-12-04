package pwr.zpibackend.controllers.university;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.university.TitleDTO;
import pwr.zpibackend.models.university.Title;
import pwr.zpibackend.services.university.TitleService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/title")
public class TitleController {
    private final TitleService titleService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Title>> getAllTitles() {
        return ResponseEntity.ok(titleService.getAllTitles());
    }

    @GetMapping("/name")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Title> getTitleByName(String name) {
        return ResponseEntity.ok(titleService.getTitleByName(name));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Title> addTitle(@RequestBody TitleDTO title) {
        return ResponseEntity.ok(titleService.addTitle(title));
    }

    @PutMapping("/{titleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Title> updateTitle(@PathVariable Long titleId, @RequestBody TitleDTO updatedTitle) {
        return ResponseEntity.ok(titleService.updateTitle(titleId, updatedTitle));
    }

    @DeleteMapping("/{titleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Title> deleteTitle(@PathVariable Long titleId) {
        return ResponseEntity.ok(titleService.deleteTitle(titleId));
    }
}
