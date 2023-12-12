package pwr.zpibackend.controllers.thesis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.dto.thesis.ThesisDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.services.impl.user.EmployeeService;
import pwr.zpibackend.services.impl.user.StudentService;
import pwr.zpibackend.services.impl.thesis.ThesisService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ThesisController.class)
@AutoConfigureMockMvc(addFilters = false)
class ThesisControllerTests {

    private static final String BASE_URL = "/api/thesis";

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
    private ThesisDTO thesisDTO;

    @BeforeEach
    public void setUp() {
        theses = new ArrayList<>();

        thesis = new Thesis();
        thesis.setId(1L);
        thesis.setNamePL("Thesis 1 PL");
        thesis.setNameEN("Thesis 1 EN");
        thesis.setDescriptionPL("Opis 1");
        thesis.setDescriptionEN("Description 1");
        thesis.setNumPeople(4);
        Employee emp = new Employee();
        emp.setId(1L);
        thesis.setSupervisor(emp);
        thesis.setPrograms(List.of(new Program()));
        thesis.setStatus(new Status(1, "Approved"));

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
    public void testGetAllPublicTheses() throws Exception {
        Mockito.when(thesisService.getAllPublicTheses()).thenReturn(theses);

        String resultJson = objectMapper.writeValueAsString(theses);

        mockMvc.perform(get(BASE_URL + "/public").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(thesisService).getAllPublicTheses();
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

    public String asJsonString(final Object obj) {
        try {
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Thesis createTestThesis(Employee supervisor, Student leader){
        Thesis thesisToAdd = new Thesis();
        thesisToAdd.setNamePL("Test Thesis PL");
        thesisToAdd.setNameEN("Test Thesis EN");
        thesisToAdd.setDescriptionPL("Test Description PL");
        thesisToAdd.setDescriptionEN("Test Description EN");
        thesisToAdd.setNumPeople(5);
        thesisToAdd.setSupervisor(supervisor);
        thesisToAdd.setLeader(leader);
        thesisToAdd.setPrograms(List.of(new Program()));
        thesisToAdd.setStudyCycle(new StudyCycle());
        thesisToAdd.setStatus(new Status("TEST STATUS"));
        thesisToAdd.setOccupied(3);
        return thesisToAdd;
    }

    public static ThesisDTO createTestThesisDTO(Long supervisorId){
        ThesisDTO thesisToAdd = new ThesisDTO();
        thesisToAdd.setNamePL("Test Thesis PL");
        thesisToAdd.setNameEN("Test Thesis EN");
        thesisToAdd.setDescriptionPL("Test Description PL");
        thesisToAdd.setDescriptionEN("Test Description EN");
        thesisToAdd.setNumPeople(5);
        thesisToAdd.setSupervisorId(supervisorId);
        thesisToAdd.setProgramIds(List.of(0L));
        thesisToAdd.setStudyCycleId(Optional.of(0L));
        thesisToAdd.setStatusId(0L);
        return thesisToAdd;
    }

    public static void assertTestData(Thesis addedThesis, Employee supervisor, Student leader){
        assertThat(addedThesis.getNamePL()).isEqualTo("Test Thesis PL");
        assertThat(addedThesis.getNameEN()).isEqualTo("Test Thesis EN");
        assertThat(addedThesis.getDescriptionPL()).isEqualTo("Test Description PL");
        assertThat(addedThesis.getDescriptionEN()).isEqualTo("Test Description EN");
        assertThat(addedThesis.getNumPeople()).isEqualTo(5);
        assertThat(addedThesis.getSupervisor()).isEqualTo(supervisor);
        assertThat(addedThesis.getLeader()).isEqualTo(leader);
        assertThat(addedThesis.getPrograms()).isEqualTo(List.of(new Program()));
        assertThat(addedThesis.getStudyCycle()).isEqualTo(new StudyCycle());
        assertThat(addedThesis.getStatus().getName()).isEqualTo("TEST STATUS");
        assertThat(addedThesis.getOccupied()).isEqualTo(3);
    }
    @Test
    public void testAddThesisSuccess() throws Exception {
        Employee supervisor = new Employee();
        Student leader = new Student();
        Thesis thesisToAdd = createTestThesis(supervisor, leader);

        doReturn(thesisToAdd).when(thesisService).addThesis(any(ThesisDTO.class));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL)
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
        ThesisDTO thesisDTO = createTestThesisDTO(0L);

        doThrow(NotFoundException.class).when(thesisService).addThesis(any(ThesisDTO.class));
        try {
            mockMvc.perform(MockMvcRequestBuilders
                    .post("/thesis")
                    .content(asJsonString(thesisDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        } catch (NestedServletException e) {
            assertThat(e.getRootCause()).isInstanceOf(NotFoundException.class);
        }
    }

    @Test
    public void testAddThesisBadRequest() throws Exception{
        String requestBody = asJsonString(thesis);

        Mockito.when(thesisService.addThesis(any(ThesisDTO.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(thesisService).addThesis(any(ThesisDTO.class));
    }

    @Test
    public void testUpdateThesisSuccess() throws Exception {
        Employee supervisor = new Employee();
        Student leader = new Student();
        Thesis existingThesis = createTestThesis(supervisor, leader);
        ThesisDTO thesisDTO = createTestThesisDTO(0L);
        thesisDTO.setDescriptionEN("UPDATED DESCRIPTION");
        thesisDTO.setNamePL("UPDATED NAME");

        existingThesis.setId(1L);
        existingThesis.setNamePL("UPDATED NAME");
        existingThesis.setDescriptionEN("UPDATED DESCRIPTION");

        Thesis updatedThesis = createTestThesis(supervisor, leader);
        updatedThesis.setId(1L);

        when(thesisService.updateThesis(1L, thesisDTO)).thenReturn(updatedThesis);

        String requestBody = objectMapper.writeValueAsString(thesisDTO);
        String responseBody = objectMapper.writeValueAsString(updatedThesis);

        mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        assertTestData(updatedThesis, supervisor, leader);
    }


    @Test
    public void testUpdateThesisFailure() throws Exception {
        Employee supervisor = new Employee();
        Student leader = new Student();
        Thesis updatedThesis = createTestThesis(supervisor, leader);
        ThesisDTO thesisDTO = createTestThesisDTO(0L);
        updatedThesis.setId(1L);

        doThrow(NotFoundException.class).when(thesisService).updateThesis(1L, thesisDTO);
        try {
            mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                    .content(asJsonString(thesisDTO))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        } catch (NestedServletException e) {
            assertThat(e.getRootCause()).isInstanceOf(NotFoundException.class);
        }
    }

    @Test
    public void testDeleteThesisSuccess() throws Exception {
        Mockito.when(thesisService.deleteThesis(1L)).thenReturn(new Thesis());
        mockMvc.perform(delete(BASE_URL + "/{id}", 1L))
                .andExpect(status().isOk());
        verify(thesisService).deleteThesis(1L);
    }

    @Test
    public void testDeleteThesisFailure() throws Exception {
        Mockito.when(thesisService.deleteThesis(3L)).thenThrow(new NotFoundException());
        mockMvc.perform(delete(BASE_URL + "/{id}", 3L))
                .andExpect(status().isNotFound());
        verify(thesisService).deleteThesis(3L);
    }

    @Test
    public void testGetAllThesesByStatusId() throws Exception {
        String statusName = "Draft";
        when(thesisService.getAllThesesByStatusName(statusName)).thenReturn(theses);

        String resultJson = objectMapper.writeValueAsString(theses);

        mockMvc.perform(get(BASE_URL + "/status/{id}", statusName).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(thesisService).getAllThesesByStatusName(statusName);
    }

    @Test
    public void testGetAllThesesExcludingStatusId() throws Exception {
        String name = "Draft";
        Mockito.when(thesisService.getAllThesesExcludingStatusName(name)).thenReturn(theses);

        String resultJson = objectMapper.writeValueAsString(theses);

        mockMvc.perform(get(BASE_URL + "/status/exclude/{id}", name).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(thesisService).getAllThesesExcludingStatusName(name);
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusId() throws Exception {
        Long empId = 1L;
        String statName = "Draft";
        Mockito.when(thesisService.getAllThesesForEmployeeByStatusName(empId, statName)).thenReturn(theses);

        String resultJson = objectMapper.writeValueAsString(theses);

        mockMvc.perform(get(BASE_URL + "/{empId}/{statId}", empId, statName).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(thesisService).getAllThesesForEmployeeByStatusName(empId, statName);
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusIdFailure() throws Exception {
        Long empId = 3L;
        String statName = "Rejected";
        Mockito.when(thesisService.getAllThesesForEmployeeByStatusName(empId, statName)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{empId}/{statId}", empId, statName).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(thesisService).getAllThesesForEmployeeByStatusName(empId, statName);
    }

    @Test
    public void testGetAllThesesForEmployee() throws Exception {
        Long empId = 2L;
        Mockito.when(thesisService.getAllThesesForEmployee(empId)).thenReturn(theses);

        String resultJson = objectMapper.writeValueAsString(theses);

        mockMvc.perform(get(BASE_URL + "/employee/{empId}", empId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(thesisService).getAllThesesForEmployee(empId);
    }

    @Test
    public void testGetAllThesesForEmployeeFailure() throws Exception {
        Long empId = 3L;
        Mockito.when(thesisService.getAllThesesForEmployee(empId)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/employee/{empId}", empId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(thesisService).getAllThesesForEmployee(empId);
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusNameList() throws Exception {
        Long empId = 2L;
        List<String> statName = Arrays.asList("Draft", "Rejected");
        Mockito.when(thesisService.getAllThesesForEmployeeByStatusNameList(empId, statName)).thenReturn(theses);

        String resultJson = objectMapper.writeValueAsString(theses);

        mockMvc.perform(get(BASE_URL + "/employee/{empId}/statuses", empId)
                .param("statName", statName.toArray(new String[0]))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(thesisService).getAllThesesForEmployeeByStatusNameList(empId, statName);
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusNameListFailure() throws Exception{
        Long empId = 3L;
        List<String> statName = Arrays.asList("Draft", "Rejected");
        Mockito.when(thesisService.getAllThesesForEmployeeByStatusNameList(empId,statName)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/employee/{empId}/statuses", empId)
                .param("statName", statName.toArray(new String[0]))
                .contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(thesisService).getAllThesesForEmployeeByStatusNameList(empId, statName);
    }

    @Test
    public void testUpdateThesesStatusInBulk() throws Exception {
        List<Long> thesesIds = Arrays.asList(1L, 2L);
        Mockito.when(thesisService.updateThesesStatusInBulk("Closed", thesesIds)).thenReturn(theses);

        mockMvc.perform(put(BASE_URL + "/bulk/Closed")
                .content(asJsonString(thesesIds))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(thesisService).updateThesesStatusInBulk("Closed", thesesIds);
    }

    @Test
    public void testUpdateThesesStatusInBulkNotFound() throws Exception{
        List<Long> thesesIds = Arrays.asList(1L, 2L);
        Mockito.when(thesisService.updateThesesStatusInBulk("xd", thesesIds)).thenThrow(new NotFoundException());

        mockMvc.perform(put(BASE_URL  + "/bulk/xd")
                .content(asJsonString(thesesIds))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(thesisService).updateThesesStatusInBulk("xd", thesesIds);
    }

    @Test
    public void testDeleteThesesByStudyCycle() throws Exception {
        Long studyCycleId = 1L;
        List<Thesis> thesesToDelete = Collections.singletonList(thesis);

        Mockito.when(thesisService.deleteThesesByStudyCycle(studyCycleId)).thenReturn(thesesToDelete);

        mockMvc.perform(delete(BASE_URL + "/bulk/cycle/{id}", studyCycleId))
                .andExpect(status().isOk());

        verify(thesisService).deleteThesesByStudyCycle(studyCycleId);
    }

    @Test
    public void testDeleteThesesByStudyCycleFailure() throws Exception {
        Long studyCycleId = 1L;
        Mockito.when(thesisService.deleteThesesByStudyCycle(studyCycleId)).thenThrow(new NotFoundException());
        mockMvc.perform(delete(BASE_URL + "/bulk/cycle/{id}", studyCycleId))
                .andExpect(status().isNotFound());
        verify(thesisService).deleteThesesByStudyCycle(studyCycleId);
    }

    @Test
    public void testDeleteThesesInBulk() throws Exception {
        List<Long> thesesIds = Arrays.asList(1L, 2L);
        List<Thesis> thesesToDelete = Collections.singletonList(thesis);

        Mockito.when(thesisService.deleteThesesInBulk(thesesIds)).thenReturn(thesesToDelete);

        mockMvc.perform(put(BASE_URL + "/bulk")
                .content(asJsonString(thesesIds))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(thesisService).deleteThesesInBulk(thesesIds);
    }

    @Test
    public void testDeleteThesesInBulkFailure() throws Exception{
        List<Long> thesesIds = Arrays.asList(1L, 2L);
        Mockito.when(thesisService.deleteThesesInBulk(thesesIds)).thenThrow(new NotFoundException());
        mockMvc.perform(put(BASE_URL + "/bulk")
                .content(asJsonString(thesesIds))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(thesisService).deleteThesesInBulk(thesesIds);
    }

}