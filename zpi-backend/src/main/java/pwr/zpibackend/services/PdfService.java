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
import pwr.zpibackend.exceptions.NotFoundException;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PdfService {
    private final ThesisRepository thesisRepository;
    private final StudentRepository studentRepository;
    private final ReservationRepository reservationRepository;

    public Map<String, Map<String, List<StudentWithoutThesisDTO>>> getStudentsWithoutThesis(String facultyAbbr,
                                                                                            String studyFieldAbbr) {
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
                            return (faculty != null && (facultyAbbr == null ||
                                    faculty.getAbbreviation().equals(facultyAbbr))) &&
                                    (studyField != null && (studyFieldAbbr == null ||
                                            studyField.getAbbreviation().equals(studyFieldAbbr)));
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

    public Map<String, Map<String, List<ThesisGroupDTO>>> getThesisGroups(String facultyAbbr, String studyFieldAbbr) {
        return thesisRepository.findAllByOrderByNamePLAsc().stream()
                .filter(thesis -> thesis.getReservations().stream().anyMatch(Reservation::isConfirmedBySupervisor))
                .filter(thesis -> thesis.getPrograms().stream()
                        .anyMatch(program -> program.getFaculty() != null &&
                                (facultyAbbr == null || program.getFaculty().getAbbreviation().equals(facultyAbbr))))
                .filter(thesis -> thesis.getPrograms().stream()
                        .anyMatch(program -> program.getStudyField() != null && (studyFieldAbbr == null ||
                                program.getStudyField().getAbbreviation().equals(studyFieldAbbr))))
                .map(thesis -> {
                    ThesisGroupDTO report = new ThesisGroupDTO();
                    report.setThesisNamePL(thesis.getNamePL());

                    Faculty faculty = null;
                    if (facultyAbbr == null) {
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
                                .filter(fac -> Objects.equals(fac.getAbbreviation(), facultyAbbr))
                                .findFirst()
                                .orElse(null);
                    }
                    if (faculty != null) {
                        report.setFacultyAbbreviation(faculty.getAbbreviation());
                        report.setFacultyName(faculty.getName());
                    }

                    StudyField studyField = null;
                    if (studyFieldAbbr == null) {
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
                                .filter(sf -> Objects.equals(sf.getAbbreviation(), studyFieldAbbr))
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

        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250");
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Indeks", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Imię", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Nazwisko", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Mail", font));
        table.addCell(cell);
    }

    private void writeStudentsDataToTheDocument(Map<String, Map<String, List<StudentWithoutThesisDTO>>>
            studentsWithoutThesis, Document document) {
        for (Map.Entry<String, Map<String, List<StudentWithoutThesisDTO>>> facultyEntry : studentsWithoutThesis.entrySet()) {
            for (Map.Entry<String, List<StudentWithoutThesisDTO>> studyFieldEntry : facultyEntry.getValue().entrySet()) {
                Paragraph p = new Paragraph(facultyEntry.getKey() + " - " + studyFieldEntry.getKey(),
                        FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250"));
                document.add(p);

                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100f);
                table.setWidths(new float[]{1.3f, 3.5f, 3.5f, 3.8f});
                table.setSpacingBefore(10);

                createStudentsTableHeader(table);

                for (StudentWithoutThesisDTO student : studyFieldEntry.getValue()) {
                    table.addCell(student.getIndex());
                    table.addCell(student.getName());
                    table.addCell(student.getSurname());
                    table.addCell(student.getMail());
                }

                document.add(table);
                document.add(Chunk.NEWLINE);
            }
        }
    }

    public boolean generateStudentsWithoutThesisReport(HttpServletResponse response, String facultyAbbr,
            String studyFieldAbbr) throws DocumentException, IOException {

        Map<String, Map<String, List<StudentWithoutThesisDTO>>> studentsWithoutThesis =
                getStudentsWithoutThesis(facultyAbbr, studyFieldAbbr);

        if (studentsWithoutThesis.isEmpty())
            return false;
        else {
            response.setContentType("application/pdf");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
            String currentDateTime = dateFormatter.format(new Date());

            String headerKey = "Content-Disposition";

            StringBuilder filename = new StringBuilder("studenci_bez_tematu_zpi");
            if (facultyAbbr != null)
                filename.append("_").append(facultyAbbr);
            if (studyFieldAbbr != null)
                filename.append("_").append(studyFieldAbbr);
            filename.append("_");
            filename.append(currentDateTime);
            filename.append(".pdf");

            String headerValue = "attachment; filename=" + filename;
            response.setHeader(headerKey, headerValue);
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();
            Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250");
            font.setSize(18);

            Paragraph p = new Paragraph("Lista studentów bez tematu zpi", font);
            p.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p);
            document.add(Chunk.NEWLINE);

            writeStudentsDataToTheDocument(studentsWithoutThesis, document);
            document.close();
            return true;
        }
    }
}
