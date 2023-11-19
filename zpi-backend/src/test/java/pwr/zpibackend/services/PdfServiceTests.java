package pwr.zpibackend.services;

import com.lowagie.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import pwr.zpibackend.dto.reports.StudentInReportsDTO;
import pwr.zpibackend.dto.reports.SupervisorDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.*;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.repositories.thesis.ReservationRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.user.StudentRepository;
import pwr.zpibackend.services.reports.PdfService;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PdfServiceTests {
    @MockBean
    private ReservationRepository reservationRepository;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private ThesisRepository thesisRepository;
    @Autowired
    private PdfService pdfService;

    private String facultyAbbr;
    private String studyFieldAbbr;
    private Map<String, Map<String, List<StudentInReportsDTO>>> studentsWithoutThesisW04NIST;
    private Map<String, Map<String, List<StudentInReportsDTO>>> studentsWithoutThesisW04N;
    private Map<String, Map<String, List<StudentInReportsDTO>>> studentsWithoutThesis;
    private Map<String, Map<String, List<ThesisGroupDTO>>> thesisGroupsW04NIST;
    private Map<String, Map<String, List<ThesisGroupDTO>>> thesisGroupsW04N;
    private Map<String, Map<String, List<ThesisGroupDTO>>> thesisGroups;
    private List<Reservation> reservations;
    private List<Student> studentsOrdered;
    private List<Thesis> theses;

    @BeforeEach
    public void setUp() {
        facultyAbbr = "W04N";
        studyFieldAbbr = "IST";

        StudyCycle studyCycle1 = new StudyCycle();
        studyCycle1.setId(1L);
        studyCycle1.setName("2020/21");

        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setAbbreviation("W04N");
        faculty1.setName("Wydział Informatyki i Telekomunikacji");

        Faculty faculty2 = new Faculty();
        faculty2.setId(2L);
        faculty2.setAbbreviation("W01");
        faculty2.setName("Wydział Architektury");

        StudyField studyField1 = new StudyField();
        studyField1.setId(1L);
        studyField1.setAbbreviation("IST");
        studyField1.setName("Informatyka Stosowana");

        StudyField studyField2 = new StudyField();
        studyField2.setId(2L);
        studyField2.setAbbreviation("INA");
        studyField2.setName("Informatyka Algorytmiczna");

        StudyField studyField3 = new StudyField();
        studyField3.setId(3L);
        studyField3.setAbbreviation("ARCH");
        studyField3.setName("Architektura");

        Program program1 = new Program();
        program1.setId(1L);
        program1.setFaculty(faculty1);
        program1.setStudyField(studyField1);

        Program program2 = new Program();
        program2.setId(2L);
        program2.setFaculty(faculty1);
        program2.setStudyField(studyField2);

        Program program3 = new Program();
        program3.setId(3L);
        program3.setFaculty(faculty2);
        program3.setStudyField(studyField3);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setIndex("123456");
        student1.setName("John");
        student1.setSurname("Doe");
        student1.setMail("123456@student.pwr.edu.pl");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setIndex("121212");
        student2.setName("Adam");
        student2.setSurname("Smith");
        student2.setMail("121212@student.pwr.edu.pl");

        Student student3 = new Student();
        student3.setId(3L);
        student3.setIndex("111111");
        student3.setName("John");
        student3.setSurname("Smith");
        student3.setMail("111111@student.pwr.edu.pl");

        Student student4 = new Student();
        student4.setId(4L);
        student4.setIndex("222222");
        student4.setName("Adam");
        student4.setSurname("Doe");
        student4.setMail("222222@student.pwr.edu.pl");

        Student student5 = new Student();
        student5.setId(5L);
        student5.setIndex("333333");
        student5.setName("Adam");
        student5.setSurname("White");
        student5.setMail("333333@student.pwr.edu.pl");

        StudentProgramCycle studentProgramCycle1 = new StudentProgramCycle();
        studentProgramCycle1.setId(new StudentProgramCycleId(1L, 1L, 1L));
        studentProgramCycle1.setStudent(student1);
        studentProgramCycle1.setProgram(program1);
        studentProgramCycle1.setCycle(studyCycle1);

        StudentProgramCycle studentProgramCycle2FirstProgram = new StudentProgramCycle();
        studentProgramCycle2FirstProgram.setId(new StudentProgramCycleId(2L, 1L, 1L));
        studentProgramCycle2FirstProgram.setStudent(student2);
        studentProgramCycle2FirstProgram.setProgram(program1);
        studentProgramCycle2FirstProgram.setCycle(studyCycle1);

        StudentProgramCycle studentProgramCycle2SecondProgram = new StudentProgramCycle();
        studentProgramCycle2SecondProgram.setId(new StudentProgramCycleId(2L, 3L, 1L));
        studentProgramCycle2SecondProgram.setStudent(student2);
        studentProgramCycle2SecondProgram.setProgram(program3);
        studentProgramCycle2SecondProgram.setCycle(studyCycle1);

        StudentProgramCycle studentProgramCycle3 = new StudentProgramCycle();
        studentProgramCycle3.setId(new StudentProgramCycleId(3L, 2L, 1L));
        studentProgramCycle3.setStudent(student3);
        studentProgramCycle3.setProgram(program2);
        studentProgramCycle3.setCycle(studyCycle1);

        StudentProgramCycle studentProgramCycle4 = new StudentProgramCycle();
        studentProgramCycle4.setId(new StudentProgramCycleId(4L, 3L, 1L));
        studentProgramCycle4.setStudent(student4);
        studentProgramCycle4.setProgram(program3);
        studentProgramCycle4.setCycle(studyCycle1);

        StudentProgramCycle studentProgramCycle5 = new StudentProgramCycle();
        studentProgramCycle5.setId(new StudentProgramCycleId(5L, 1L, 1L));
        studentProgramCycle5.setStudent(student5);
        studentProgramCycle5.setProgram(program1);
        studentProgramCycle5.setCycle(studyCycle1);

        student1.setStudentProgramCycles(Set.of(studentProgramCycle1));
        student2.setStudentProgramCycles(Set.of(studentProgramCycle2FirstProgram, studentProgramCycle2SecondProgram));
        student3.setStudentProgramCycles(Set.of(studentProgramCycle3));
        student4.setStudentProgramCycles(Set.of(studentProgramCycle4));
        student5.setStudentProgramCycles(Set.of(studentProgramCycle5));


        StudentInReportsDTO studentInReportsDTO1 = new StudentInReportsDTO();
        studentInReportsDTO1.setIndex("123456");
        studentInReportsDTO1.setName("John");
        studentInReportsDTO1.setSurname("Doe");
        studentInReportsDTO1.setMail("123456@student.pwr.edu.pl");
        studentInReportsDTO1.setFacultyAbbreviation("W04N");
        studentInReportsDTO1.setStudyFieldAbbreviation("IST");

        StudentInReportsDTO studentInReportsDTO2FirstProgram = new StudentInReportsDTO();
        studentInReportsDTO2FirstProgram.setIndex("121212");
        studentInReportsDTO2FirstProgram.setName("Adam");
        studentInReportsDTO2FirstProgram.setSurname("Smith");
        studentInReportsDTO2FirstProgram.setMail("121212@student.pwr.edu.pl");
        studentInReportsDTO2FirstProgram.setFacultyAbbreviation("W04N");
        studentInReportsDTO2FirstProgram.setStudyFieldAbbreviation("IST");

        StudentInReportsDTO studentInReportsDTO2SecondProgram = new StudentInReportsDTO();
        studentInReportsDTO2SecondProgram.setIndex("121212");
        studentInReportsDTO2SecondProgram.setName("Adam");
        studentInReportsDTO2SecondProgram.setSurname("Smith");
        studentInReportsDTO2SecondProgram.setMail("121212@student.pwr.edu.pl");
        studentInReportsDTO2SecondProgram.setFacultyAbbreviation("W01");
        studentInReportsDTO2SecondProgram.setStudyFieldAbbreviation("ARCH");

        StudentInReportsDTO studentInReportsDTO3 = new StudentInReportsDTO();
        studentInReportsDTO3.setIndex("111111");
        studentInReportsDTO3.setName("John");
        studentInReportsDTO3.setSurname("Smith");
        studentInReportsDTO3.setMail("111111@student.pwr.edu.pl");
        studentInReportsDTO3.setFacultyAbbreviation("W04N");
        studentInReportsDTO3.setStudyFieldAbbreviation("INA");

        StudentInReportsDTO studentInReportsDTO4 = new StudentInReportsDTO();
        studentInReportsDTO4.setIndex("222222");
        studentInReportsDTO4.setName("Adam");
        studentInReportsDTO4.setSurname("Doe");
        studentInReportsDTO4.setMail("222222@student.pwr.edu.pl");
        studentInReportsDTO4.setFacultyAbbreviation("W01");
        studentInReportsDTO4.setStudyFieldAbbreviation("ARCH");

        StudentInReportsDTO studentInReportsDTO5 = new StudentInReportsDTO();
        studentInReportsDTO5.setIndex("333333");
        studentInReportsDTO5.setName("Adam");
        studentInReportsDTO5.setSurname("White");
        studentInReportsDTO5.setMail("333333@student.pwr.edu.pl");
        studentInReportsDTO5.setFacultyAbbreviation("W04N");
        studentInReportsDTO5.setStudyFieldAbbreviation("IST");

        studentsWithoutThesisW04NIST = Map.of(
                "W04N", Map.of(
                        "IST", List.of(studentInReportsDTO2FirstProgram, studentInReportsDTO1)
                )
        );

        studentsWithoutThesisW04N = Map.of(
                "W04N", Map.of(
                        "INA", List.of(studentInReportsDTO3),
                        "IST", List.of(studentInReportsDTO2FirstProgram, studentInReportsDTO1)
                )
        );

        studentsWithoutThesis = Map.of(
                "W01", Map.of(
                        "ARCH", List.of(studentInReportsDTO2SecondProgram, studentInReportsDTO4)
                ),
                "W04N", Map.of(
                        "INA", List.of(studentInReportsDTO3),
                        "IST", List.of(studentInReportsDTO2FirstProgram, studentInReportsDTO1)
                )
        );

        Title title1 = new Title();
        title1.setId(1L);
        title1.setName("dr");

        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("Joe");
        employee1.setSurname("Damon");
        employee1.setMail("j.d@pwr.edu.pl");
        employee1.setTitle(title1);

        SupervisorDTO supervisorDTO1 = new SupervisorDTO();
        supervisorDTO1.setName("Joe");
        supervisorDTO1.setSurname("Damon");
        supervisorDTO1.setMail("j.d@pwr.edu.pl");
        supervisorDTO1.setTitle("dr");


        thesisGroupsW04NIST = Map.of(
                "W04N", Map.of(
                        "IST", List.of(
                                new ThesisGroupDTO("Temat 1", "Thesis 1", "W04N", "IST",
                                        supervisorDTO1, List.of(studentInReportsDTO1, studentInReportsDTO2FirstProgram)
                                ),
                                new ThesisGroupDTO("Temat 6", "Thesis 6", "W04N", "IST",
                                        supervisorDTO1, List.of(studentInReportsDTO5)
                                )
                        )
                )
        );

        thesisGroupsW04N = Map.of(
                "W04N", Map.of(
                        "IST", List.of(
                                new ThesisGroupDTO("Temat 1", "Thesis 1", "W04N", "IST",
                                        supervisorDTO1, List.of(studentInReportsDTO1, studentInReportsDTO2FirstProgram)
                                ),
                                new ThesisGroupDTO("Temat 6", "Thesis 6", "W04N", "IST",
                                        supervisorDTO1, List.of(studentInReportsDTO5)
                                )
                        ),
                        "INA", List.of(
                                new ThesisGroupDTO("Temat 4", "Thesis 4", "W04N", "INA",
                                        supervisorDTO1, List.of(studentInReportsDTO3)
                                )
                        )
                )
        );

        thesisGroups = Map.of(
                "W04N", Map.of(
                        "IST", List.of(
                                new ThesisGroupDTO("Temat 1", "Thesis 1", "W04N", "IST",
                                        supervisorDTO1, List.of(studentInReportsDTO1, studentInReportsDTO2FirstProgram)
                                ),
                                new ThesisGroupDTO("Temat 6", "Thesis 6", "W04N", "IST",
                                        supervisorDTO1, List.of(studentInReportsDTO5)
                                )
                        ),
                        "INA", List.of(
                                new ThesisGroupDTO("Temat 4", "Thesis 4", "W04N", "INA",
                                        supervisorDTO1, List.of(studentInReportsDTO3)
                                )
                        )
                ),
                "W01", Map.of(
                        "ARCH", List.of(
                                new ThesisGroupDTO("Temat 5", "Thesis 5", "W01", "ARCH",
                                        supervisorDTO1, List.of(studentInReportsDTO4)
                                )
                        )
                )
        );

        reservations = Arrays.asList(
                new Reservation(1L, true, false, true,
                        true, null, null, student1, null),
                new Reservation(2L, true, true, true,
                        true, null, null, student5, null),
                new Reservation(3L, true, false, true,
                        true, null, null, student2, null)
        );

        studentsOrdered = Arrays.asList(student3, student2, student1, student4, student5);

        theses = Arrays.asList(
                // valid thesis for the report
                new Thesis(1L, "Temat 1", "Thesis 1", "Opis1", "Description1",
                        4, employee1, student1, List.of(program1, program2), studyCycle1, new Status(), 2,
                        List.of(
                                new Reservation(1L, true, true, true,
                                        true, null, null, student1, null),
                                new Reservation(2L, true, true, true,
                                        true, null, null, student2, null)
                        ), null, null),
                new Thesis(4L, "Temat 4", "Thesis 4", "Opis4", "Description4",
                        4, employee1, student3, List.of(program2), studyCycle1, new Status(), 1,
                        List.of(
                                new Reservation(1L, true, true, true,
                                        true, null, null, student3, null)
                        ), null, null),
                new Thesis(5L, "Temat 5", "Thesis 5", "Opis5", "Description5",
                        4, employee1, student4, List.of(program2, program3), studyCycle1, new Status(), 1,
                        List.of(
                                new Reservation(1L, true, true, true,
                                        true, null, null, student4, null)
                        ), null, null),
                new Thesis(6L, "Temat 6", "Thesis 6", "Opis6", "Description6",
                        4, employee1, student5, List.of(program1), studyCycle1, new Status(), 1,
                        List.of(
                                new Reservation(1L, true, true, true,
                                        true, null, null, student5, null)
                        ), null, null),
                // invalid thesis for the report
                new Thesis(2L, "Temat 2", "Thesis 2", "Opis2", "Description2",
                        4, employee1, new Student(), List.of(program1, program2), studyCycle1, new Status(), 1,
                        List.of(
                                new Reservation(1L, true, false, true,
                                        true, null, null, new Student(), null)
                        ), null, null),
                new Thesis(3L, "Temat 3", "Thesis 3", "Opis3", "Description3",
                        4, employee1, null, List.of(program1, program2), studyCycle1, new Status(), 0,
                        null, null, null),
                new Thesis(7L, "Temat 7", "Thesis 7", "Opis7", "Description7",
                        4, employee1, student1, List.of(program3), studyCycle1, new Status(), 0,
                        List.of(
                                new Reservation(1L, true, true, true,
                                        true, null, null, student1, null)
                        ), null, null)
        );
    }

    @Test
    public void testGetStudentsWithoutThesisFromFacultyAndField() {
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        Map<String, Map<String, List<StudentInReportsDTO>>> result =
                pdfService.getStudentsWithoutThesis(facultyAbbr, studyFieldAbbr);

        assertEquals(studentsWithoutThesisW04NIST, result);
    }

    @Test
    public void testGetStudentsWithoutThesisFromFaculty() {
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        Map<String, Map<String, List<StudentInReportsDTO>>> result =
                pdfService.getStudentsWithoutThesis(facultyAbbr, null);

        assertEquals(studentsWithoutThesisW04N, result);
    }

    @Test
    public void testGetStudentsWithoutThesisFromField() {
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        Map<String, Map<String, List<StudentInReportsDTO>>> result =
                pdfService.getStudentsWithoutThesis(null, studyFieldAbbr);

        assertEquals(studentsWithoutThesisW04NIST, result);
    }

    @Test
    public void testGetStudentsWithoutThesisFromAllFaculties() {
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        Map<String, Map<String, List<StudentInReportsDTO>>> result =
                pdfService.getStudentsWithoutThesis(null, null);

        assertEquals(studentsWithoutThesis, result);
    }

    @Test
    public void testGetStudentsWithoutThesisDataNotFound() {
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        Map<String, Map<String, List<StudentInReportsDTO>>> result =
                pdfService.getStudentsWithoutThesis("aa", "aa");

        assertEquals(Collections.emptyMap(), result);
    }

    @Test
    public void testGetThesisGroupsFromFacultyAndField() {
        when(thesisRepository.findAllByOrderByNamePLAsc()).thenReturn(theses);

        Map<String, Map<String, List<ThesisGroupDTO>>> result =
                pdfService.getThesisGroups(facultyAbbr, studyFieldAbbr);

        assertEquals(thesisGroupsW04NIST, result);
    }

    @Test
    public void testGetThesisGroupsFromFaculty() {
        when(thesisRepository.findAllByOrderByNamePLAsc()).thenReturn(theses);

        Map<String, Map<String, List<ThesisGroupDTO>>> result =
                pdfService.getThesisGroups(facultyAbbr, null);

        assertEquals(thesisGroupsW04N, result);
    }

    @Test
    public void testGetThesisGroupsFromField() {
        when(thesisRepository.findAllByOrderByNamePLAsc()).thenReturn(theses);

        Map<String, Map<String, List<ThesisGroupDTO>>> result =
                pdfService.getThesisGroups(null, studyFieldAbbr);

        assertEquals(thesisGroupsW04NIST, result);
    }

    @Test
    public void testGetThesisGroupsFromAllFaculties() {
        when(thesisRepository.findAllByOrderByNamePLAsc()).thenReturn(theses);

        Map<String, Map<String, List<ThesisGroupDTO>>> result =
                pdfService.getThesisGroups(null, null);

        assertEquals(thesisGroups, result);
    }

    @Test
    public void testGetThesisGroupsDataNotFound() {
        when(thesisRepository.findAllByOrderByNamePLAsc()).thenReturn(theses);

        Map<String, Map<String, List<ThesisGroupDTO>>> result =
                pdfService.getThesisGroups("aa", "aa");

        assertEquals(Collections.emptyMap(), result);
    }

    @Test
    public void testGenerateThesisGroupsReportFromW04NIST() throws DocumentException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(thesisRepository.findAllByOrderByNamePLAsc()).thenReturn(theses);

        boolean result = pdfService.generateThesisGroupsReport(response, facultyAbbr, studyFieldAbbr);

        assertTrue(result);
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.containsHeader("Content-Disposition"));
        assertTrue(response.getHeader("Content-Disposition").startsWith("attachment; filename="));
        assertTrue(response.getContentAsByteArray().length > 0);
        String expectedFilename = "grupy_zpi_" + facultyAbbr + "_" + studyFieldAbbr + "_";
        assertTrue(response.getHeader("Content-Disposition").contains(expectedFilename));
    }

    @Test
    public void testGenerateThesisGroupsReportFromW04N() throws DocumentException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(thesisRepository.findAllByOrderByNamePLAsc()).thenReturn(theses);

        boolean result = pdfService.generateThesisGroupsReport(response, facultyAbbr, null);

        assertTrue(result);
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.containsHeader("Content-Disposition"));
        assertTrue(response.getHeader("Content-Disposition").startsWith("attachment; filename="));
        assertTrue(response.getContentAsByteArray().length > 0);
        String expectedFilename = "grupy_zpi_" + facultyAbbr + "_";
        assertTrue(response.getHeader("Content-Disposition").contains(expectedFilename));
        assertFalse(response.getHeader("Content-Disposition").contains(studyFieldAbbr));
    }

    @Test
    public void testGenerateThesisGroupsReportFromIST() throws DocumentException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(thesisRepository.findAllByOrderByNamePLAsc()).thenReturn(theses);

        boolean result = pdfService.generateThesisGroupsReport(response, null, studyFieldAbbr);

        assertTrue(result);
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.containsHeader("Content-Disposition"));
        assertTrue(response.getHeader("Content-Disposition").startsWith("attachment; filename="));
        assertTrue(response.getContentAsByteArray().length > 0);
        String expectedFilename = "grupy_zpi_" + studyFieldAbbr + "_";
        assertTrue(response.getHeader("Content-Disposition").contains(expectedFilename));
        assertFalse(response.getHeader("Content-Disposition").contains(facultyAbbr));
    }

    @Test
    public void testGenerateThesisGroupsReportFromAllFaculties() throws DocumentException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(thesisRepository.findAllByOrderByNamePLAsc()).thenReturn(theses);

        boolean result = pdfService.generateThesisGroupsReport(response, null, null);

        assertTrue(result);
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.containsHeader("Content-Disposition"));
        assertTrue(response.getHeader("Content-Disposition").startsWith("attachment; filename="));
        assertTrue(response.getContentAsByteArray().length > 0);
        String expectedFilename = "grupy_zpi_";
        assertTrue(response.getHeader("Content-Disposition").contains(expectedFilename));
        assertFalse(response.getHeader("Content-Disposition").contains(facultyAbbr));
        assertFalse(response.getHeader("Content-Disposition").contains(studyFieldAbbr));
    }

    @Test
    public void testGenerateThesisGroupsReportDataNotFound() throws DocumentException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(thesisRepository.findAllByOrderByNamePLAsc()).thenReturn(theses);

        boolean result = pdfService.generateThesisGroupsReport(response, "aa", "aa");

        assertFalse(result);
        assertNull(response.getContentType());
        assertNull(response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentAsByteArray().length);
    }

    @Test
    public void testGenerateStudentsWithoutThesisReportFROMW40NIST() throws DocumentException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        boolean result = pdfService.generateStudentsWithoutThesisReport(response, facultyAbbr, studyFieldAbbr);

        assertTrue(result);
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.containsHeader("Content-Disposition"));
        assertTrue(response.getHeader("Content-Disposition").startsWith("attachment; filename="));
        assertTrue(response.getContentAsByteArray().length > 0);
        String expectedFilename = "studenci_bez_tematu_zpi_" + facultyAbbr + "_" + studyFieldAbbr + "_";
        assertTrue(response.getHeader("Content-Disposition").contains(expectedFilename));
    }

    @Test
    public void testGenerateStudentsWithoutThesisReportFromW04N() throws DocumentException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        boolean result = pdfService.generateStudentsWithoutThesisReport(response, facultyAbbr, null);

        assertTrue(result);
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.containsHeader("Content-Disposition"));
        assertTrue(response.getHeader("Content-Disposition").startsWith("attachment; filename="));
        assertTrue(response.getContentAsByteArray().length > 0);
        String expectedFilename = "studenci_bez_tematu_zpi_" + facultyAbbr + "_";
        assertTrue(response.getHeader("Content-Disposition").contains(expectedFilename));
        assertFalse(response.getHeader("Content-Disposition").contains(studyFieldAbbr));
    }

    @Test
    public void testGenerateStudentsWithoutThesisReportFromIST() throws DocumentException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        boolean result = pdfService.generateStudentsWithoutThesisReport(response, null, studyFieldAbbr);

        assertTrue(result);
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.containsHeader("Content-Disposition"));
        assertTrue(response.getHeader("Content-Disposition").startsWith("attachment; filename="));
        assertTrue(response.getContentAsByteArray().length > 0);
        String expectedFilename = "studenci_bez_tematu_zpi_" + studyFieldAbbr + "_";
        assertTrue(response.getHeader("Content-Disposition").contains(expectedFilename));
        assertFalse(response.getHeader("Content-Disposition").contains(facultyAbbr));
    }

    @Test
    public void testGenerateStudentsWithoutThesisReportFromAllFaculties() throws DocumentException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        boolean result = pdfService.generateStudentsWithoutThesisReport(response, null, null);

        assertTrue(result);
        assertEquals("application/pdf", response.getContentType());
        assertTrue(response.containsHeader("Content-Disposition"));
        assertTrue(response.getHeader("Content-Disposition").startsWith("attachment; filename="));
        assertTrue(response.getContentAsByteArray().length > 0);
        String expectedFilename = "studenci_bez_tematu_zpi_";
        assertTrue(response.getHeader("Content-Disposition").contains(expectedFilename));
        assertFalse(response.getHeader("Content-Disposition").contains(facultyAbbr));
        assertFalse(response.getHeader("Content-Disposition").contains(studyFieldAbbr));
    }

    @Test
    public void testGenerateStudentsWithoutThesisReportDataNotFound() throws DocumentException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        boolean result = pdfService.generateStudentsWithoutThesisReport(response, "aa", "aa");

        assertFalse(result);
        assertNull(response.getContentType());
        assertNull(response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentAsByteArray().length);
    }
}
