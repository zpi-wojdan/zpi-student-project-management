package pwr.zpibackend.controllers.thesis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;
import pwr.zpibackend.config.GoogleAuthService;

import pwr.zpibackend.dto.thesis.CommentDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Comment;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.services.thesis.CommentService;
import pwr.zpibackend.services.thesis.ThesisService;
import pwr.zpibackend.services.user.EmployeeService;
import pwr.zpibackend.services.user.StudentService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTests {
    private static final String BASE_URL = "/api/thesis/comment";
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private ThesisService thesisService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private CommentController commentController;

    private List<Comment> commentList;
    private Comment comment;
    private CommentDTO commentDTO;

    @BeforeEach
    public void setUp(){
        commentDTO = new CommentDTO("Content - DTO", 1L, 1L);

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

        commentList = new ArrayList<>();
        commentList.add(comment1);
        commentList.add(comment2);
    }

    public String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetAllComments() throws Exception {
        Mockito.when(commentService.getAllComments()).thenReturn(commentList);

        String resultJson = asJsonString(commentList);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(commentService).getAllComments();
    }

    @Test
    public void testGetCommentById() throws Exception {
        Long commentId = 1L;
        Mockito.when(commentService.getComment(commentId)).thenReturn(commentList.get(0));

        String resultJson = asJsonString(commentList.get(0));

        mockMvc.perform(get(BASE_URL + "/{commentId}", commentId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(commentService).getComment(commentId);
    }

    @Test
    public void testGetCommendByIdNotFound() throws Exception {
        Long commentId = 1L;
        Mockito.when(commentService.getComment(commentId)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{commentId}", commentId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(commentService).getComment(commentId);
    }

    @Test
    public void testAddCommentBadRequest() throws Exception {
        String requestBody = asJsonString(commentDTO);

        Mockito.when(commentService.addComment(any(CommentDTO.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(commentService).addComment(any(CommentDTO.class));
    }

    @Test
    public void testAddComment() throws Exception {
        String requestBody = asJsonString(commentDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(commentService).addComment(any(CommentDTO.class));
    }

    @Test
    public void testAddCommentFailure() throws Exception{
        String requestBody = asJsonString(commentDTO);

        doThrow(NotFoundException.class).when(commentService).addComment(any(CommentDTO.class));
        try{
            mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                            .contentType("application/json")
                            .content(requestBody))
                    .andExpect(status().isNotFound());
        }catch (NestedServletException e){
            assertThat(e.getCause()).isInstanceOf(NotFoundException.class);
        }
    }

    @Test
    public void testUpdateComment() throws Exception {
        Long commentId = 1L;
        String requestBody = asJsonString(commentDTO);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{commentId}", commentId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(commentService).updateComment(commentId, commentDTO);
    }

    @Test
    public void testUpdateCommentFailure() throws Exception{
        String requestBody = asJsonString(commentDTO);

        Mockito.doThrow(NotFoundException.class).when(commentService).updateComment(any(Long.class), any(CommentDTO.class));

        try{
            mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{commentId}", 1L)
                            .contentType("application/json")
                            .content(requestBody))
                    .andExpect(status().isNotFound());
        }catch (NestedServletException e){
            assertThat(e.getCause()).isInstanceOf(NotFoundException.class);
        }
        verify(commentService).updateComment(any(Long.class), any(CommentDTO.class));
    }

    @Test
    public void testDeleteComment() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{commentId}", commentId)
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(commentService).deleteComment(commentId);
    }

    @Test
    public void testDeleteFailure() throws Exception{
        Long commentId = 1L;

        Mockito.doThrow(NotFoundException.class).when(commentService).deleteComment(any(Long.class));

        try{
            mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{commentId}", commentId)
                            .contentType("application/json"))
                    .andExpect(status().isNotFound());
        }catch (NestedServletException e){
            assertThat(e.getCause()).isInstanceOf(NotFoundException.class);
        }
        verify(commentService).deleteComment(any(Long.class));
    }

    @Test
    public void testGetAllCommentsByThesisId() throws Exception {
        Long thesisId = 1L;
        Mockito.when(commentService.getAllCommentsByThesisId(thesisId)).thenReturn(commentList);

        String resultJson = asJsonString(commentList);

        mockMvc.perform(get(BASE_URL + "/thesis/{thesisId}", thesisId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(commentService).getAllCommentsByThesisId(thesisId);
    }

    @Test
    public void testGetAllCommentsByThesisIdFailure() throws Exception{
        Long thesisId = 1L;
        Mockito.doThrow(NotFoundException.class).when(commentService).getAllCommentsByThesisId(any(Long.class));

        try{
            mockMvc.perform(get(BASE_URL + "/thesis/{thesisId}", thesisId).contentType("application/json"))
                    .andExpect(status().isNotFound());
        }catch (NestedServletException e){
            assertThat(e.getCause()).isInstanceOf(NotFoundException.class);
        }
        verify(commentService).getAllCommentsByThesisId(any(Long.class));
    }

    @Test
    public void testAllCommentsByAuthorId() throws Exception {
        Long authorId = 1L;
        Mockito.when(commentService.getAllCommentsByAuthorId(authorId)).thenReturn(commentList);

        String resultJson = asJsonString(commentList);

        mockMvc.perform(get(BASE_URL + "/author/{authorId}", authorId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(commentService).getAllCommentsByAuthorId(authorId);
    }

    @Test
    public void testAllCommentsByAuthorIdFailure() throws Exception{
        Long authorId = 1L;
        Mockito.doThrow(NotFoundException.class).when(commentService).getAllCommentsByAuthorId(any(Long.class));

        try{
            mockMvc.perform(get(BASE_URL + "/author/{authorId}", authorId).contentType("application/json"))
                    .andExpect(status().isNotFound());
        }catch (NestedServletException e){
            assertThat(e.getCause()).isInstanceOf(NotFoundException.class);
        }
        verify(commentService).getAllCommentsByAuthorId(any(Long.class));
    }
}
