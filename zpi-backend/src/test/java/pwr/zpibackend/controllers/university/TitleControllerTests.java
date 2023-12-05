package pwr.zpibackend.controllers.university;

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
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.dto.university.TitleDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Title;
import pwr.zpibackend.services.university.TitleService;
import pwr.zpibackend.services.user.EmployeeService;
import pwr.zpibackend.services.user.StudentService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TitleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TitleControllerTests {

    private static final String BASE_URL = "/api/title";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private TitleService titleService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private TitleController titleController;

    private List<Title> titles;
    private Title title;
    private TitleDTO titleDTO;

    @BeforeEach
    public void setUp() {
        title = new Title(1L, "TitleName", 2);
        titleDTO = new TitleDTO();
        titleDTO.setName("TitleName");
        titleDTO.setNumTheses(2);

        titles = new ArrayList<>();
        titles.add(title);
    }

    @Test
    public void testGetAllTitles() throws Exception {
        Mockito.when(titleService.getAllTitles()).thenReturn(titles);

        String returnedJson = objectMapper.writeValueAsString(titles);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(titleService).getAllTitles();
    }

    @Test
    public void testGetTitleByName() throws Exception {
        String titleName = "dr";
        Mockito.when(titleService.getTitleByName(titleName)).thenReturn(title);

        String returnedJson = objectMapper.writeValueAsString(title);

        mockMvc.perform(get(BASE_URL + "/{name}", titleName).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(titleService).getTitleByName(titleName);
    }

    @Test
    public void testGetTitleByNameNotFound() throws Exception {
        String titleName = "dr";
        Mockito.when(titleService.getTitleByName(titleName)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{name}", titleName).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(titleService).getTitleByName(titleName);
    }

    @Test
    public void testAddTitle() throws Exception {
        Mockito.when(titleService.addTitle(titleDTO)).thenReturn(title);

        String requestBody = objectMapper.writeValueAsString(titleDTO);
        String responseBody = objectMapper.writeValueAsString(title);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(titleService).addTitle(titleDTO);
    }

    @Test
    public void testAddTitleAlreadyExists() throws Exception {
        Mockito.when(titleService.addTitle(titleDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(titleDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(titleService).addTitle(titleDTO);
    }

    @Test
    public void testUpdateTitle() throws Exception {
        Long titleId = 1L;
        titleDTO.setName("dr hab");

        Mockito.when(titleService.updateTitle(titleId, titleDTO)).thenReturn(title);

        String requestBody = objectMapper.writeValueAsString(titleDTO);
        String responseBody = objectMapper.writeValueAsString(title);

        mockMvc.perform(put(BASE_URL + "/{titleId}", titleId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(titleService).updateTitle(titleId, titleDTO);
    }

    @Test
    public void testUpdateTitleNotFound() throws Exception {
        Long titleId = 1L;
        titleDTO.setName("dr hab xxx");

        Mockito.when(titleService.updateTitle(titleId, titleDTO)).thenThrow(new NotFoundException());

        String requestBody = objectMapper.writeValueAsString(titleDTO);

        mockMvc.perform(put(BASE_URL + "/{titleId}", titleId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(titleService).updateTitle(titleId, titleDTO);
    }

    @Test
    public void testUpdateTitleAlreadyExists() throws Exception {
        Long titleId = 1L;
        titleDTO.setName("dr");

        Mockito.when(titleService.updateTitle(titleId, titleDTO)).thenThrow(new AlreadyExistsException());

        String requestBody = objectMapper.writeValueAsString(titleDTO);

        mockMvc.perform(put(BASE_URL + "/{titleId}", titleId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(titleService).updateTitle(titleId, titleDTO);
    }

    @Test
    public void testDeleteTitle() throws Exception {
        Long titleId = 1L;

        Mockito.when(titleService.deleteTitle(titleId)).thenReturn(title);

        String returnedJson = objectMapper.writeValueAsString(title);

        mockMvc.perform(delete(BASE_URL + "/{titleId}", titleId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(titleService).deleteTitle(titleId);
    }

    @Test
    public void testDeleteTitleNotFound() throws Exception {
        Long titleId = 1L;

        Mockito.when(titleService.deleteTitle(titleId)).thenThrow(new NotFoundException());

        mockMvc.perform(delete(BASE_URL + "/{titleId}", titleId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(titleService).deleteTitle(titleId);
    }
}
