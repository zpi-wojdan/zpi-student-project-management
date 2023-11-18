package pwr.zpibackend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pwr.zpibackend.dto.reports.StudentInReportsDTO;
import pwr.zpibackend.dto.reports.SupervisorDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.models.university.*;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.repositories.thesis.ReservationRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.user.StudentRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
    private Map<String, Map<String, List<ThesisGroupDTO>>> thesisGroups;
    private List<Reservation> reservations;
    private List<Student> studentsOrdered;

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

        student1.setStudentProgramCycles(Set.of(studentProgramCycle1));
        student2.setStudentProgramCycles(Set.of(studentProgramCycle2FirstProgram, studentProgramCycle2SecondProgram));
        student3.setStudentProgramCycles(Set.of(studentProgramCycle3));
        student4.setStudentProgramCycles(Set.of(studentProgramCycle4));

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

        thesisGroups = Map.of(
                "W04N", Map.of(
                        "IST", List.of(
                                new ThesisGroupDTO("Thesis 1", "W04N", "IST",
                                        new SupervisorDTO("j.d@pwr.edu.pl", "Joe", "Damon", "dr"),
                                        List.of(studentInReportsDTO1, studentInReportsDTO2FirstProgram)
                                )
                        )
                )
        );

        reservations = Arrays.asList(
                new Reservation(1L, true, false, true,
                        true, null, null, student1, null),
                new Reservation(2L, true, true, true,
                        true, null, null, new Student(), null),
                new Reservation(3L, true, false, true,
                        true, null, null, student2, null)
        );

        studentsOrdered = Arrays.asList(student3, student2, student1, student4);
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
    public void testGetStudentsWithoutThesis() {
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(studentRepository.findAllByOrderByIndexAsc()).thenReturn(studentsOrdered);

        Map<String, Map<String, List<StudentInReportsDTO>>> result =
                pdfService.getStudentsWithoutThesis(null, null);

        assertEquals(studentsWithoutThesis, result);
    }

}
