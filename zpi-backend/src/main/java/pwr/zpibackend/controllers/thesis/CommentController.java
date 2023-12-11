package pwr.zpibackend.controllers.thesis;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.thesis.CommentDTO;
import pwr.zpibackend.models.thesis.Comment;
import pwr.zpibackend.services.thesis.ICommentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/thesis/comment")
public class CommentController {

    private final ICommentService commentService;

    @GetMapping
    @Operation(summary = "Get all comments", description = "Returns list of all comments. <br>" +
            "Requires ADMIN, SUPERVISOR or APPROVER role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<List<Comment>> getAllComments() {
        return new ResponseEntity<>(commentService.getAllComments(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comment by id", description = "Returns comment with given id. <br>" +
            "Requires ADMIN, SUPERVISOR or APPROVER role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        return new ResponseEntity<>(commentService.getComment(id), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Add comment", description = "Adds comment to database. <br>" +
            "Requires ADMIN, SUPERVISOR or APPROVER role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<Comment> addComment(@RequestBody CommentDTO comment) {
        return new ResponseEntity<>(commentService.addComment(comment), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update comment", description = "Updates comment with given id. <br>" +
            "Requires ADMIN, SUPERVISOR or APPROVER role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody CommentDTO param) {
        return new ResponseEntity<>(commentService.updateComment(id, param), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Deletes comment with given id. <br>" +
            "Requires ADMIN, SUPERVISOR or APPROVER role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<Comment> deleteComment(@PathVariable Long id) {
        return new ResponseEntity<>(commentService.deleteComment(id), HttpStatus.OK);
    }

    @GetMapping("/thesis/{id}")
    @Operation(summary = "Get all comments by thesis id",
            description = "Returns list of all comments with given thesis id. <br>" +
                    "Requires ADMIN, SUPERVISOR or APPROVER role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<List<Comment>> getAllCommentsByThesisId(@PathVariable Long id) {
        return new ResponseEntity<>(commentService.getAllCommentsByThesisId(id), HttpStatus.OK);
    }

    @GetMapping("/author/{id}")
    @Operation(summary = "Get all comments by author id",
            description = "Returns list of all comments with given author id. <br>" +
                    "Requires ADMIN, SUPERVISOR or APPROVER role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<List<Comment>> getAllCommentsByAuthorId(@PathVariable Long id) {
        return new ResponseEntity<>(commentService.getAllCommentsByAuthorId(id), HttpStatus.OK);
    }
}
