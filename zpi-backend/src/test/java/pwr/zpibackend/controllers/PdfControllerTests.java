package pwr.zpibackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.dto.reports.StudentInReportsDTO;
import pwr.zpibackend.dto.reports.SupervisorDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.services.reports.PdfService;
import pwr.zpibackend.services.user.EmployeeService;
import pwr.zpibackend.services.user.StudentService;

import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PdfController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PdfControllerTests {
    private static final String BASE_URL = "/report";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private PdfService pdfService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private PdfController pdfController;
    private String facultyAbbr;
    private String studyFieldAbbr;
    private Map<String, Map<String, List<StudentInReportsDTO>>> studentsWithoutThesis;
    private Map<String, Map<String, List<ThesisGroupDTO>>> thesisGroups;
    private ThesisGroupDTO thesisGroup;

    @BeforeEach
    public void setUp() {
        facultyAbbr = "W04N";
        studyFieldAbbr = "IST";

        studentsWithoutThesis = Map.of(
                "W04N", Map.of(
                        "IST", List.of(
                                new StudentInReportsDTO("123456", "John", "Doe",
                                        "123456@student.pwr.edu.pl", "W04N", "IST"),
                                new StudentInReportsDTO("121212", "Adam", "Smith",
                                        "121212@student.pwr.edu.pl", "W04N", "IST")
                        )
                )
        );

        thesisGroup = new ThesisGroupDTO("Temat 1", "Thesis 1", "W04N", "IST",
                new SupervisorDTO("j.d@pwr.edu.pl", "Joe", "Damon", "dr",
                        "K035", "Katedra 1"),
                List.of(new StudentInReportsDTO("123456", "John", "Doe",
                                "123456@student.pwr.edu.pl", "W04N", "IST"),
                        new StudentInReportsDTO("121212", "Adam", "Smith",
                                "121212@student.pwr.edu.pl", "W04N", "IST")
                )
        );

        thesisGroups = Map.of(
                "W04N", Map.of(
                        "IST", List.of(thesisGroup)
                )
        );
    }

    @Test
    public void testGenerateStudentsWithoutThesisReport() throws Exception {
        when(pdfService.generateStudentsWithoutThesisReport(any(HttpServletResponse.class), eq(facultyAbbr),
                eq(studyFieldAbbr))).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/pdf/students-without-thesis")
                        .param("facultyAbbr", facultyAbbr)
                        .param("studyFieldAbbr", studyFieldAbbr))
                .andExpect(status().isOk())
                .andExpect(content().string("Report generated successfully"));

        verify(pdfService, times(1)).generateStudentsWithoutThesisReport(
                any(HttpServletResponse.class), eq(facultyAbbr), eq(studyFieldAbbr)
        );
    }

    @Test
    public void testGenerateStudentsWithoutThesisReportFailed() throws Exception {
        when(pdfService.generateStudentsWithoutThesisReport(any(HttpServletResponse.class), eq(facultyAbbr),
                eq(studyFieldAbbr))).thenReturn(false);

        mockMvc.perform(get(BASE_URL + "/pdf/students-without-thesis")
                        .param("facultyAbbr", facultyAbbr)
                        .param("studyFieldAbbr", studyFieldAbbr))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Students without thesis not found"));

        verify(pdfService, times(1)).generateStudentsWithoutThesisReport(
                any(HttpServletResponse.class), eq(facultyAbbr), eq(studyFieldAbbr)
        );
    }

    @Test
    public void testGenerateThesisGroupsReport() throws Exception {
        when(pdfService.generateThesisGroupsReport(any(HttpServletResponse.class), eq(facultyAbbr),
                eq(studyFieldAbbr))).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/pdf/thesis-groups")
                        .param("facultyAbbr", facultyAbbr)
                        .param("studyFieldAbbr", studyFieldAbbr))
                .andExpect(status().isOk())
                .andExpect(content().string("Report generated successfully"));

        verify(pdfService, times(1)).generateThesisGroupsReport(
                any(HttpServletResponse.class), eq(facultyAbbr), eq(studyFieldAbbr)
        );
    }

    @Test
    public void testGenerateThesisGroupsReportFailed() throws Exception {
        when(pdfService.generateThesisGroupsReport(any(HttpServletResponse.class), eq(facultyAbbr),
                eq(studyFieldAbbr))).thenReturn(false);

        mockMvc.perform(get(BASE_URL + "/pdf/thesis-groups")
                        .param("facultyAbbr", facultyAbbr)
                        .param("studyFieldAbbr", studyFieldAbbr))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Thesis groups not found"));

        verify(pdfService, times(1)).generateThesisGroupsReport(
                any(HttpServletResponse.class), eq(facultyAbbr), eq(studyFieldAbbr)
        );
    }

    @Test
    public void testGenerateThesisDeclaration() throws Exception {
        Long id = 1L;
        when(pdfService.generateThesisDeclaration(any(HttpServletResponse.class), eq(id))).thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/pdf/thesis-declaration/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string("Declaration generated successfully"));

        verify(pdfService, times(1)).generateThesisDeclaration(
                any(HttpServletResponse.class), eq(id)
        );
    }

    @Test
    public void testGenerateThesisDeclarationFailed() throws Exception {
        Long id = 1L;
        when(pdfService.generateThesisDeclaration(any(HttpServletResponse.class), eq(id))).thenReturn(false);

        mockMvc.perform(get(BASE_URL + "/pdf/thesis-declaration/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Thesis group not found"));

        verify(pdfService, times(1)).generateThesisDeclaration(
                any(HttpServletResponse.class), eq(id)
        );
    }

    @Test
    public void testGetStudentsWithoutThesis() throws Exception {
        when(pdfService.getStudentsWithoutThesis(facultyAbbr, studyFieldAbbr)).thenReturn(studentsWithoutThesis);

        String returnedJson = objectMapper.writeValueAsString(studentsWithoutThesis);

        mockMvc.perform(get(BASE_URL + "/data/students-without-thesis")
                        .param("facultyAbbr", facultyAbbr)
                        .param("studyFieldAbbr", studyFieldAbbr))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(pdfService, times(1)).getStudentsWithoutThesis(facultyAbbr, studyFieldAbbr);
    }

    @Test
    public void testGetThesisGroups() throws Exception {
        when(pdfService.getThesisGroups(facultyAbbr, studyFieldAbbr)).thenReturn(thesisGroups);

        String returnedJson = objectMapper.writeValueAsString(thesisGroups);

        mockMvc.perform(get(BASE_URL + "/data/thesis-groups")
                        .param("facultyAbbr", facultyAbbr)
                        .param("studyFieldAbbr", studyFieldAbbr))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(pdfService, times(1)).getThesisGroups(facultyAbbr, studyFieldAbbr);
    }

    @Test
    public void testGetThesisGroupData() throws Exception {
        Long id = 1L;
        when(pdfService.getThesisGroupDataById(id)).thenReturn(thesisGroup);

        String returnedJson = objectMapper.writeValueAsString(thesisGroup);

        mockMvc.perform(get(BASE_URL + "/data/thesis-declaration/" + id))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(pdfService, times(1)).getThesisGroupDataById(id);
    }
}
