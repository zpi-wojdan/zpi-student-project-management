package pwr.zpibackend.services.thesis;

import pwr.zpibackend.dto.thesis.CommentDTO;
import pwr.zpibackend.models.thesis.Comment;

import java.util.List;

public interface ICommentService {
    List<Comment> getAllComments();
    Comment getComment(Long id);
    Comment addComment(CommentDTO comment);
    Comment updateComment(Long id, CommentDTO param);
    Comment deleteComment(Long id);
    List<Comment> getAllCommentsByThesisId(Long id);
    List<Comment> getAllCommentsByAuthorId(Long id);
}
