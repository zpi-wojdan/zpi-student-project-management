package pwr.zpibackend.services.impl.thesis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pwr.zpibackend.dto.thesis.CommentDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Comment;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.repositories.thesis.CommentRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.services.impl.thesis.CommentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CommentServiceTests {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ThesisRepository thesisRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private CommentService commentService;

    private List<Comment> commentList;
    private Comment comment;
    private CommentDTO commentDTO;

    @BeforeEach
    public void setUp(){
        commentDTO = new CommentDTO("Content - DTO", 3L, 3L);

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setContent("Content 1");
        comment1.setCreationTime(LocalDateTime.now());
        comment1.setThesis(new Thesis());
        comment1.setAuthor(new Employee());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setContent("Content 2");
        comment2.setCreationTime(LocalDateTime.now());
        comment2.setThesis(new Thesis());
        comment2.setAuthor(new Employee());

        Comment comment3 = new Comment();
        comment3.setId(3L);
        comment3.setContent("Content - DTO");
        comment3.setCreationTime(LocalDateTime.now());

        Thesis thesis = new Thesis();
        thesis.setId(3L);
        Employee employee = new Employee();
        employee.setId(3L);

        comment3.setThesis(thesis);
        comment3.setAuthor(employee);

        commentList = new ArrayList<>();
        commentList.add(comment1);
        commentList.add(comment2);
        commentList.add(comment3);
    }

    @Test
    public void testGetAllComments(){
        when(commentRepository.findAll()).thenReturn(commentList);

        List<Comment> result = commentService.getAllComments();

        assertEquals(3, result.size());
        assertEquals(commentList, result);
    }

    @Test
    public void testGetCommentById(){
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.ofNullable(commentList.get(0)));

        Comment result = commentService.getComment(commentId);

        assertEquals(commentList.get(0), result);
    }

    @Test
    public void testGetCommentByIdNotFound(){
        Long commentId = 5L;
        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.empty());
        assertThrows(NotFoundException.class, () -> commentService.getComment(commentId));
    }

    @Test
    public void testAddComment(){
        Employee emp = new Employee();
        Thesis thes = new Thesis();
        emp.setId(3L);
        thes.setId(3L);

        when(commentRepository.save(comment)).thenReturn(comment);
        when(employeeRepository.findById(commentDTO.getAuthorId())).thenReturn(java.util.Optional.of(emp));
        when(thesisRepository.findById(commentDTO.getThesisId())).thenReturn(java.util.Optional.of(thes));

        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = commentService.addComment(commentDTO);

        System.out.println(commentList.get(2));
        assertEquals(commentList.get(2).getId(), 3L);
        assertEquals(commentList.get(2).getContent(), "Content - DTO");
        assertEquals(commentList.get(2).getThesis(), result.getThesis());
        assertEquals(commentList.get(2).getAuthor(), result.getAuthor());
    }

    @Test
    public void testAddCommentEmployeeNotFound(){
        Employee emp = new Employee();
        Thesis thes = new Thesis();
        emp.setId(3L);
        thes.setId(3L);

        when(commentRepository.save(comment)).thenReturn(comment);
        when(employeeRepository.findById(commentDTO.getAuthorId())).thenReturn(java.util.Optional.empty());
        when(thesisRepository.findById(commentDTO.getThesisId())).thenReturn(java.util.Optional.of(thes));

        when(commentRepository.save(comment)).thenReturn(comment);

        assertThrows(NotFoundException.class, () -> commentService.addComment(commentDTO));
    }

    @Test
    public void testAddCommentThesisNotFound(){
        Employee emp = new Employee();
        Thesis thes = new Thesis();
        emp.setId(3L);
        thes.setId(3L);

        when(commentRepository.save(comment)).thenReturn(comment);
        when(employeeRepository.findById(commentDTO.getAuthorId())).thenReturn(java.util.Optional.of(emp));
        when(thesisRepository.findById(commentDTO.getThesisId())).thenReturn(java.util.Optional.empty());

        when(commentRepository.save(comment)).thenReturn(comment);

        assertThrows(NotFoundException.class, () -> commentService.addComment(commentDTO));
    }

    @Test
    public void testUpdateComment(){
        Long commentId = 1L;
        Long authorId = 1L;
        Long thesisId = 1L;

        CommentDTO commentDTO = new CommentDTO("Content - DTO", authorId, thesisId);
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Content - DTO");
        comment.setCreationTime(LocalDateTime.now());
        comment.setThesis(new Thesis());
        comment.setAuthor(new Employee());

        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(thesisRepository.findById(thesisId)).thenReturn(java.util.Optional.ofNullable(comment.getThesis()));
        when(employeeRepository.findById(authorId)).thenReturn(java.util.Optional.ofNullable(comment.getAuthor()));

        Comment result = commentService.updateComment(commentId, commentDTO);
        assertEquals(comment, result);
    }

    @Test
    public void testUpdateCommentEmployeeNotFound(){
        Long commentId = 1L;
        Long authorId = 1L;
        Long thesisId = 1L;

        CommentDTO commentDTO = new CommentDTO("Content - DTO", authorId, thesisId);
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Content - DTO");
        comment.setCreationTime(LocalDateTime.now());
        comment.setThesis(new Thesis());
        comment.setAuthor(new Employee());

        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(thesisRepository.findById(thesisId)).thenReturn(java.util.Optional.ofNullable(comment.getThesis()));
        when(employeeRepository.findById(authorId)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.updateComment(commentId, commentDTO));
    }

    @Test
    public void testUpdateCommentThesisNotFound(){
        Long commentId = 1L;
        Long authorId = 1L;
        Long thesisId = 1L;

        CommentDTO commentDTO = new CommentDTO("Content - DTO", authorId, thesisId);
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Content - DTO");
        comment.setCreationTime(LocalDateTime.now());
        comment.setThesis(new Thesis());
        comment.setAuthor(new Employee());

        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(thesisRepository.findById(thesisId)).thenReturn(java.util.Optional.empty());
        when(employeeRepository.findById(authorId)).thenReturn(java.util.Optional.ofNullable(comment.getAuthor()));

        assertThrows(NotFoundException.class, () -> commentService.updateComment(commentId, commentDTO));
    }

    @Test
    public void testDeleteComment(){
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.of(comment));
        doNothing().when(commentRepository).deleteById(commentId);

        Comment result = commentService.deleteComment(commentId);
        assertEquals(comment, result);
    }

    @Test
    public void testDeleteCommentNotFound(){
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.deleteComment(commentId));
    }

    @Test
    public void testGetAllCommentsByThesisId(){
        Long thesisId = 3L;
        when(commentRepository.findAllByThesisId(thesisId)).thenReturn(commentList);

        List<Comment> result = commentService.getAllCommentsByThesisId(thesisId);

        assertEquals(3, result.size());
        assertEquals(commentList, result);
    }

    @Test
    public void testGetAllCommentsByThesisIdNotFound(){
        Long thesisId = 5L;
        when(commentRepository.findAllByThesisId(thesisId)).thenReturn(new ArrayList<>());

        List<Comment> result = commentService.getAllCommentsByThesisId(thesisId);

        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllCommentsByAuthorId(){
        Long authorId = 3L;
        when(commentRepository.findAllByAuthorId(authorId)).thenReturn(commentList);

        List<Comment> result = commentService.getAllCommentsByAuthorId(authorId);

        assertEquals(3, result.size());
        assertEquals(commentList, result);
    }

    @Test
    public void testGetAllCommentsByAuthorIdNotFound(){
        Long authorId = 5L;
        when(commentRepository.findAllByAuthorId(authorId)).thenReturn(new ArrayList<>());

        List<Comment> result = commentService.getAllCommentsByAuthorId(authorId);

        assertEquals(0, result.size());
    }
}
