package pwr.zpibackend.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.reports.StudentWithThesisDTO;
import pwr.zpibackend.dto.reports.StudentWithoutThesisDTO;
import pwr.zpibackend.dto.reports.SupervisorDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.models.university.StudentProgramCycle;
import pwr.zpibackend.repositories.ReservationRepository;
import pwr.zpibackend.repositories.StudentRepository;
import pwr.zpibackend.repositories.ThesisRepository;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PdfService {
    private final ThesisRepository thesisRepository;
    private final StudentRepository studentRepository;
    private final ReservationRepository reservationRepository;

    public Map<String, Map<String, List<StudentWithoutThesisDTO>>> getStudentsWithoutThesis(Long facultyId,
                                                                                            Long studyFieldId) {
        Set<Student> studentsWithConfirmedReservations = reservationRepository.findAll().stream()
                .filter(Reservation::isConfirmedBySupervisor)
                .map(Reservation::getStudent)
                .collect(Collectors.toSet());

        return studentRepository.findAllByOrderByIndexAsc().stream()
                .filter(student -> !studentsWithConfirmedReservations.contains(student))
                .flatMap(student -> student.getStudentProgramCycles().stream()
                        .map(StudentProgramCycle::getProgram)
                        .filter(Objects::nonNull)
                        .filter(program -> {
                            Faculty faculty = program.getFaculty();
                            StudyField studyField = program.getStudyField();
                            return (faculty != null && (facultyId == null || faculty.getId() == facultyId)) &&
                                    (studyField != null && (studyFieldId == null ||
                                            studyField.getId().equals(studyFieldId)));
                        })
                        .map(program -> {
                            StudentWithoutThesisDTO report = new StudentWithoutThesisDTO();
                            report.setIndex(student.getIndex());
                            report.setName(student.getName());
                            report.setSurname(student.getSurname());
                            report.setMail(student.getMail());
                            report.setFacultyAbbreviation(program.getFaculty().getAbbreviation());
                            report.setFacultyName(program.getFaculty().getName());
                            report.setStudyFieldAbbreviation(program.getStudyField().getAbbreviation());
                            report.setStudyFieldName(program.getStudyField().getName());
                            return report;
                        }))
                .collect(Collectors.groupingBy(StudentWithoutThesisDTO::getFacultyAbbreviation,
                        TreeMap::new,
                        Collectors.groupingBy(StudentWithoutThesisDTO::getStudyFieldAbbreviation,
                                TreeMap::new,
                                Collectors.toList())));
    }

    public Map<String, Map<String, List<ThesisGroupDTO>>> getThesisGroups(Long facultyId, Long studyFieldId) {
        return thesisRepository.findAllByOrderByNamePLAsc().stream()
                .filter(thesis -> thesis.getReservations().stream().anyMatch(Reservation::isConfirmedBySupervisor))
                .filter(thesis -> thesis.getPrograms().stream()
                        .anyMatch(program -> program.getFaculty() != null &&
                                (facultyId == null || program.getFaculty().getId() == facultyId)))
                .filter(thesis -> thesis.getPrograms().stream()
                        .anyMatch(program -> program.getStudyField() != null &&
                                (studyFieldId == null || program.getStudyField().getId().equals(studyFieldId))))
                .map(thesis -> {
                    ThesisGroupDTO report = new ThesisGroupDTO();
                    report.setThesisNamePL(thesis.getNamePL());

                    Faculty faculty = null;
                    if (facultyId == null) {
                        List<Faculty> thesisFaculties = thesis.getPrograms().stream()
                                .map(Program::getFaculty)
                                .toList();
                        List<Faculty> leaderFaculties = thesis.getLeader().getStudentProgramCycles().stream()
                                .map(programCycle -> programCycle.getProgram().getFaculty())
                                .toList();
                        faculty = thesisFaculties.stream()
                                .filter(leaderFaculties::contains)
                                .findFirst()
                                .orElse(null);
                    } else {
                        faculty = thesis.getPrograms().stream()
                                .map(Program::getFaculty)
                                .filter(fac -> Objects.equals(fac.getId(), facultyId))
                                .findFirst()
                                .orElse(null);
                    }
                    if (faculty != null) {
                        report.setFacultyAbbreviation(faculty.getAbbreviation());
                        report.setFacultyName(faculty.getName());
                    }

                    StudyField studyField = null;
                    if (studyFieldId == null) {
                        List<StudyField> thesisStudyFields = thesis.getPrograms().stream()
                                .map(Program::getStudyField)
                                .toList();
                        List<StudyField> leaderStudyFields = thesis.getLeader().getStudentProgramCycles().stream()
                                .map(programCycle -> programCycle.getProgram().getStudyField())
                                .toList();
                        studyField = thesisStudyFields.stream()
                                .filter(leaderStudyFields::contains)
                                .findFirst()
                                .orElse(null);
                    } else {
                        studyField = thesis.getPrograms().stream()
                                .map(Program::getStudyField)
                                .filter(sf -> Objects.equals(sf.getId(), studyFieldId))
                                .findFirst()
                                .orElse(null);
                    }
                    if (studyField != null) {
                        report.setStudyFieldAbbreviation(studyField.getAbbreviation());
                        report.setStudyFieldName(studyField.getName());
                    }

                    SupervisorDTO supervisor = new SupervisorDTO();
                    supervisor.setName(thesis.getSupervisor().getName());
                    supervisor.setSurname(thesis.getSupervisor().getSurname());
                    supervisor.setMail(thesis.getSupervisor().getMail());
                    supervisor.setTitle(thesis.getSupervisor().getTitle().getName());
                    report.setSupervisor(supervisor);

                    report.setStudents(thesis.getReservations().stream()
                            .map(reservation -> {
                                StudentWithThesisDTO student = new StudentWithThesisDTO();
                                student.setName(reservation.getStudent().getName());
                                student.setSurname(reservation.getStudent().getSurname());
                                student.setMail(reservation.getStudent().getMail());
                                student.setIndex(reservation.getStudent().getIndex());
                                return student;
                            })
                            .collect(Collectors.toList()));
                    return report;
                })
                .filter(report -> report.getFacultyAbbreviation() != null && report.getStudyFieldAbbreviation() != null)
                .collect(Collectors.groupingBy(ThesisGroupDTO::getFacultyAbbreviation,
                        TreeMap::new,
                        Collectors.groupingBy(ThesisGroupDTO::getStudyFieldAbbreviation,
                                TreeMap::new,
                                Collectors.toList())));
    }

    private void createStudentsTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(WebColors.getRGBColor("#9A342D"));
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Index", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Surname", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Mail", font));
        table.addCell(cell);
    }

    private void writeStudentsTableData(PdfPTable table) {
        List<StudentWithoutThesisDTO> students = getStudentsWithoutThesis(null, null).values().stream()
                .flatMap(map -> map.values().stream())
                .flatMap(List::stream)
                .collect(Collectors.toList());
        for (StudentWithoutThesisDTO student : students) {
            table.addCell(String.valueOf(student.getIndex()));
            table.addCell(student.getName());
            table.addCell(student.getSurname());
            table.addCell(student.getMail());
        }
    }

    public void generateStudentsWithoutThesisReport(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);
        font.setSize(18);

        Paragraph p = new Paragraph("List of students without the thesis", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.3f, 3.5f, 3.5f, 3.8f});
        table.setSpacingBefore(10);

        createStudentsTableHeader(table);
        writeStudentsTableData(table);

        document.add(table);

        document.close();
    }

}
