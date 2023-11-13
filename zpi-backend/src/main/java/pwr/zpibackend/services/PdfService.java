package pwr.zpibackend.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.reports.StudentWithThesisDTO;
import pwr.zpibackend.dto.reports.SupervisorDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.repositories.ThesisRepository;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PdfService {

    private final StudentService studentService;
    private final ThesisRepository thesisRepository;

    public Map<String, Map<String, List<ThesisGroupDTO>>> getThesisGroups(Long facultyId, Long studyFieldId) {
        return thesisRepository.findAllByOrderByNamePLAsc().stream()
                .filter(thesis -> thesis.getReservations().stream().anyMatch(Reservation::isConfirmedBySupervisor))
                .filter(thesis -> {
                    if (facultyId != null) {
                        return thesis.getPrograms().stream().anyMatch(program -> program.getFaculty() != null &&
                                program.getFaculty().getId() == facultyId);
                    } else {
                        return thesis.getPrograms().stream().anyMatch(program -> program.getFaculty() != null);
                    }
                })
                .filter(thesis -> {
                    if (studyFieldId != null) {
                        return thesis.getPrograms().stream().anyMatch(program -> program.getStudyField() != null &&
                                program.getStudyField().getId().equals(studyFieldId));
                    } else {
                        return thesis.getPrograms().stream().anyMatch(program -> program.getStudyField() != null);
                    }
                })
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
                        Collectors.groupingBy(ThesisGroupDTO::getStudyFieldAbbreviation)));
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Id", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Surname", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Mail", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Index", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        List<Student> students = studentService.getStudentsWithoutThesis();
        for (Student student : students) {
            table.addCell(String.valueOf(student.getId()));
            table.addCell(student.getName());
            table.addCell(student.getSurname());
            table.addCell(student.getMail());
            table.addCell(String.valueOf(student.getIndex()));
        }
    }

    public void generateStudentsWithoutThesisReport(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("List of students without the thesis", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);

        document.close();
    }

}
