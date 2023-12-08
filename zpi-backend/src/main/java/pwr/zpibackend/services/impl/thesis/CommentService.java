package pwr.zpibackend.services.impl.thesis;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.thesis.CommentDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Comment;
import pwr.zpibackend.repositories.thesis.CommentRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.services.thesis.ICommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final ThesisRepository thesisRepository;
    private final EmployeeRepository employeeRepository;

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(
                () -> new NotFoundException("comment with id " + id + " does not exist")
        );
    }

    public Comment addComment(CommentDTO comment) {
        Comment newComment = new Comment();
        newComment.setContent(comment.getContent());
        newComment.setCreationTime(LocalDateTime.now());

        newComment.setThesis(
                thesisRepository.findById(comment.getThesisId())
                        .orElseThrow(NotFoundException::new)
        );
        newComment.setAuthor(
                employeeRepository.findById(comment.getAuthorId())
                        .orElseThrow(NotFoundException::new)
        );

        commentRepository.save(newComment);
        return newComment;
    }

    public Comment updateComment(Long id, CommentDTO param){
        Comment existing = commentRepository.findById(id).orElse(null);
        if (existing != null){
            existing.setThesis(
                    thesisRepository.findById(param.getThesisId())
                            .orElseThrow(NotFoundException::new)
            );
            existing.setAuthor(
                    employeeRepository.findById(param.getAuthorId())
                            .orElseThrow(NotFoundException::new)
            );

            existing.setCreationTime(LocalDateTime.now());
            existing.setContent(param.getContent());
            return commentRepository.save(existing);
        }
        throw new NotFoundException("comment with id " + id + " does not exist");
    }

    public Comment deleteComment(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()){
            Comment deleted = comment.get();
            commentRepository.deleteById(id);
            return deleted;
        }
        throw new NotFoundException();
    }

    public List<Comment> getAllCommentsByThesisId(Long id) {
        return commentRepository.findAllByThesisId(id);
    }

    public List<Comment> getAllCommentsByAuthorId(Long id) {
        return commentRepository.findAllByAuthorId(id);
    }
}
