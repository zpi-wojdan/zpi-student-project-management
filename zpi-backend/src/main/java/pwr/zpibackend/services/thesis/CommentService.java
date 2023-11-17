package pwr.zpibackend.services.thesis;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Comment;
import pwr.zpibackend.repositories.thesis.CommentRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ThesisRepository thesisRepository;
    private final EmployeeRepository employeeRepository;

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getAllCommentsByThesisId(Long id) {
        return commentRepository.findAllByThesis_Id(id);
    }

    public List<Comment> getAllCommentsByAuthorId(Long id) {
        return commentRepository.findAllByAuthor_Id(id);
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow();
    }

    public Comment addComment(Comment comment) {
        Comment newComment = new Comment();
        newComment.setAuthor(comment.getAuthor());
        newComment.setThesis(comment.getThesis());
        newComment.setContent(comment.getContent());
        newComment.setCreationTime(comment.getCreationTime());
        commentRepository.saveAndFlush(newComment);
        return newComment;
    }

    public Comment updateComment(Long id, Comment param){
        if(commentRepository.existsById(id)){
            Comment updated = commentRepository.findById(id).get();

            if (employeeRepository.existsById(param.getAuthor().getId())) {
                updated.setAuthor(param.getAuthor());
            }
            else {
                throw new NotFoundException();
            }
            if (thesisRepository.existsById(param.getThesis().getId())) {
                updated.setThesis(param.getThesis());
            }
            else {
                throw new NotFoundException();
            }

            updated.setContent(param.getContent());
            updated.setCreationTime(param.getCreationTime());
            commentRepository.saveAndFlush(updated);
            return updated;
        }
        throw new NotFoundException();
    }

    public void deleteComment(Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        }
        else {
            throw new NotFoundException();
        }
    }

}
