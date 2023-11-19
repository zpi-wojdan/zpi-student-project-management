package pwr.zpibackend.controllers.thesis;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.thesis.CommentDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Comment;
import pwr.zpibackend.services.thesis.CommentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/thesis/comment")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<List<Comment>> getAllComments() {
        return new ResponseEntity<>(commentService.getAllComments(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        return new ResponseEntity<>(commentService.getComment(id), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<Comment> addComment(@RequestBody CommentDTO comment) throws NotFoundException {
        return new ResponseEntity<>(commentService.addComment(comment), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody CommentDTO param) {
        return new ResponseEntity<>(commentService.updateComment(id, param), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<Comment> deleteComment(@PathVariable Long id) {
        return new ResponseEntity<>(commentService.deleteComment(id), HttpStatus.OK);
    }

    @GetMapping("/thesis/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<List<Comment>> getAllCommentsByThesisId(@PathVariable Long id) {
        return new ResponseEntity<>(commentService.getAllCommentsByThesisId(id), HttpStatus.OK);
    }

    @GetMapping("/author/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_APPROVER')")
    public ResponseEntity<List<Comment>> getAllCommentsByAuthorId(@PathVariable Long id) {
        return new ResponseEntity<>(commentService.getAllCommentsByAuthorId(id), HttpStatus.OK);
    }
}
