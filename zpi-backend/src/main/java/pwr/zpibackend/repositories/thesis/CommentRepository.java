package pwr.zpibackend.repositories.thesis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.thesis.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@RepositoryRestResource
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.thesis.Id = :thesisId")
    List<Comment> findAllByThesisId(@Param("thesisId") Long thesisId);

    @Query("SELECT c FROM Comment c WHERE c.author.id = :authorId")
    List<Comment> findAllByAuthorId(@Param("authorId") Long authorId);
}
