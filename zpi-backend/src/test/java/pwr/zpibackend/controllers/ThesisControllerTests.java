package pwr.zpibackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.controllers.ThesisController;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.services.EmployeeService;
import pwr.zpibackend.services.StudentService;
import pwr.zpibackend.services.ThesisService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ThesisController.class)
@AutoConfigureMockMvc(addFilters = false)
class ThesisControllerTests {

    private static final String BASE_URL = "/thesis";

    @Autowired private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private ThesisService thesisService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private ThesisController thesisController;

    private List<Thesis> theses;
    private Thesis thesis;

    @BeforeEach
    public void setUp() {
        thesis = new Thesis();
        thesis.setId(1L);
        thesis.setNamePL("Thesis 1 PL");
        thesis.setNameEN("Thesis 1 EN");
        thesis.setDescription("Description 1");
        thesis.setNum_people(4);
        thesis.setSupervisor(new Employee());
        thesis.setPrograms(List.of(new Program()));

        theses = new ArrayList<>();
        theses.add(thesis);

    }

    @Test
    void testGetAllTheses() throws Exception {
        Mockito.when(thesisService.getAllTheses()).thenReturn(theses);

        String resultJson = objectMapper.writeValueAsString(theses);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(thesisService).getAllTheses();
    }

    @Test
    public void testGetThesisById() throws Exception {
        Long thesisId = 1L;

        Mockito.when(thesisService.getThesis(thesisId)).thenReturn(thesis);

        String resultJson = objectMapper.writeValueAsString(thesis);

        mockMvc.perform(get(BASE_URL + "/{id}", thesisId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(thesisService).getThesis(thesisId);
    }

    @Test
    public void testGetThesisByIdNotFound() throws Exception {
        Long thesisId = 1L;

        Mockito.when(thesisService.getThesis(thesisId)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{id}", thesisId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(thesisService).getThesis(thesisId);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Thesis createTestThesis(Employee supervisor, Student leader){
        Thesis thesisToAdd = new Thesis();
        thesisToAdd.setNamePL("Test Thesis PL");
        thesisToAdd.setNameEN("Test Thesis EN");
        thesisToAdd.setDescription("Test Description");
        thesisToAdd.setNum_people(5);
        thesisToAdd.setSupervisor(supervisor);
        thesisToAdd.setLeader(leader);
        thesisToAdd.setPrograms(List.of(new Program()));
        thesisToAdd.setStudyCycle(new StudyCycle());
        thesisToAdd.setStatus("Test Status");
        thesisToAdd.setOccupied(3);
        return thesisToAdd;
    }

    public static void assertTestData(Thesis addedThesis, Employee supervisor, Student leader){
        assertThat(addedThesis.getNamePL()).isEqualTo("Test Thesis PL");
        assertThat(addedThesis.getNameEN()).isEqualTo("Test Thesis EN");
        assertThat(addedThesis.getDescription()).isEqualTo("Test Description");
        assertThat(addedThesis.getNum_people()).isEqualTo(5);
        assertThat(addedThesis.getSupervisor()).isEqualTo(supervisor);
        assertThat(addedThesis.getLeader()).isEqualTo(leader);
        assertThat(addedThesis.getPrograms()).isEqualTo(List.of(new Program()));
        assertThat(addedThesis.getStudyCycle()).isEqualTo(new StudyCycle());
        assertThat(addedThesis.getStatus()).isEqualTo("Test Status");
        assertThat(addedThesis.getOccupied()).isEqualTo(3);
    }
    @Test
    public void testAddThesisSuccess() throws Exception {
        Employee supervisor = new Employee();
        Student leader = new Student();
        Thesis thesisToAdd = createTestThesis(supervisor, leader);

        doReturn(thesisToAdd).when(thesisService).addThesis(any(Thesis.class));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/thesis")
                        .content(asJsonString(thesisToAdd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String jsonResponse = response.getContentAsString();
        Thesis addedThesis = new ObjectMapper().readValue(jsonResponse, Thesis.class);

        assertTestData(addedThesis, supervisor, leader);
    }

    @Test
    public void testAddThesisFailure() throws Exception {
        Employee supervisor = new Employee();
        Student leader = new Student();
        Thesis thesisToAdd = createTestThesis(supervisor, leader);


        doThrow(NotFoundException.class).when(thesisService).addThesis(any(Thesis.class));
        try {
            mockMvc.perform(MockMvcRequestBuilders
                    .post("/thesis")
                    .content(asJsonString(thesisToAdd))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        } catch (NestedServletException e) {
            assertThat(e.getRootCause()).isInstanceOf(NotFoundException.class);
        }
    }

    @Test
    public void testUpdateThesisSuccess() throws Exception {
        Employee supervisor = new Employee();
        Student leader = new Student();
        Thesis existingThesis = createTestThesis(supervisor, leader);
        existingThesis.setId(1L);
        existingThesis.setLeader(null);
        existingThesis.setNamePL("PREVIOUS DATA");

        Thesis updatedThesis = createTestThesis(supervisor, leader);
        updatedThesis.setId(1L);

        doReturn(updatedThesis).when(thesisService).updateThesis(1L, updatedThesis);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put("/thesis/1")
                        .content(asJsonString(updatedThesis))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String jsonResponse = response.getContentAsString();
        Thesis updatedResponse = new ObjectMapper().readValue(jsonResponse, Thesis.class);

        assertTestData(updatedResponse, supervisor, leader);
    }


    @Test
    public void testUpdateThesisFailure() throws Exception {
        Employee supervisor = new Employee();
        Student leader = new Student();
        Thesis updatedThesis = createTestThesis(supervisor, leader);
        updatedThesis.setId(1L);

        doThrow(NotFoundException.class).when(thesisService).updateThesis(1L, updatedThesis);
        try {
            mockMvc.perform(MockMvcRequestBuilders
                    .put("/thesis/1")
                    .content(asJsonString(updatedThesis))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        } catch (NestedServletException e) {
            assertThat(e.getRootCause()).isInstanceOf(NotFoundException.class);
        }
    }

}