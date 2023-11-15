package pwr.zpibackend.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.reports.StudentInReportsDTO;
import pwr.zpibackend.dto.reports.SupervisorDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.models.Thesis;
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

    public Map<String, Map<String, List<StudentInReportsDTO>>> getStudentsWithoutThesis(String facultyAbbr,
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
                            return setStudentData(student.getName(), student.getSurname(), student.getIndex(),
                                    student.getMail(), program.getFaculty().getAbbreviation(),
                                    program.getStudyField().getAbbreviation());
                        }))
                .collect(Collectors.groupingBy(StudentInReportsDTO::getFacultyAbbreviation,
                        TreeMap::new,
                        Collectors.groupingBy(StudentInReportsDTO::getStudyFieldAbbreviation,
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
                    ThesisGroupDTO thesisGroupData = new ThesisGroupDTO();
                    thesisGroupData.setThesisNamePL(thesis.getNamePL());

                    setFacultyData(thesis, thesisGroupData, facultyAbbr);
                    setStudyFieldData(thesis, thesisGroupData, studyFieldAbbr);
                    setSupervisorData(thesis, thesisGroupData);

                    thesisGroupData.setStudents(thesis.getReservations().stream()
                            .map(reservation -> {
                                return setStudentData(reservation.getStudent().getName(),
                                        reservation.getStudent().getSurname(), reservation.getStudent().getIndex(),
                                        reservation.getStudent().getMail(), null, null);
                            })
                            .collect(Collectors.toList()));
                    return thesisGroupData;
                })
                .filter(report -> report.getFacultyAbbreviation() != null && report.getStudyFieldAbbreviation() != null)
                .collect(Collectors.groupingBy(ThesisGroupDTO::getFacultyAbbreviation,
                        TreeMap::new,
                        Collectors.groupingBy(ThesisGroupDTO::getStudyFieldAbbreviation,
                                TreeMap::new,
                                Collectors.toList())));
    }

    private StudentInReportsDTO setStudentData(String name, String surname, String index, String mail,
                                               String facultyAbbr, String studyFieldAbbr) {
        StudentInReportsDTO student = new StudentInReportsDTO();
        student.setName(name);
        student.setSurname(surname);
        student.setIndex(index);
        student.setMail(mail);
        student.setFacultyAbbreviation(facultyAbbr);
        student.setStudyFieldAbbreviation(studyFieldAbbr);
        return student;
    }

    private void setSupervisorData(Thesis thesis, ThesisGroupDTO thesisGroupData) {
        SupervisorDTO supervisor = new SupervisorDTO();
        supervisor.setName(thesis.getSupervisor().getName());
        supervisor.setSurname(thesis.getSupervisor().getSurname());
        supervisor.setMail(thesis.getSupervisor().getMail());
        supervisor.setTitle(thesis.getSupervisor().getTitle().getName());
        thesisGroupData.setSupervisor(supervisor);
    }

    private void setFacultyData(Thesis thesis, ThesisGroupDTO thesisGroupData, String facultyAbbr) {
        Faculty faculty;
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
            thesisGroupData.setFacultyAbbreviation(faculty.getAbbreviation());
        }
    }

    private void setStudyFieldData(Thesis thesis, ThesisGroupDTO thesisGroupData, String studyFieldAbbr) {
        StudyField studyField;
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
            thesisGroupData.setStudyFieldAbbreviation(studyField.getAbbreviation());
        }
    }

    private void setResponseHeaders(HttpServletResponse response, String baseName, String facultyAbbr,
                                String studyFieldAbbr) {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";

        StringBuilder filename = new StringBuilder(baseName);
        if (facultyAbbr != null)
            filename.append("_").append(facultyAbbr);
        if (studyFieldAbbr != null)
            filename.append("_").append(studyFieldAbbr);
        filename.append("_");
        filename.append(currentDateTime);
        filename.append(".pdf");

        String headerValue = "attachment; filename=" + filename;
        response.setHeader(headerKey, headerValue);
    }

    private void createStudentsTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(WebColors.getRGBColor("#9A342D"));
        cell.setPaddingBottom(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250");
        font.setColor(Color.WHITE);
        font.setSize(12);

        cell.setPhrase(new Phrase("Indeks", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Imię", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Nazwisko", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Mail", font));
        table.addCell(cell);
    }

    private void createStudentsTable(Document document, List<StudentInReportsDTO> students)
            throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, "Cp1250");
        font.setSize(12);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.3f, 3.5f, 3.5f, 3.8f});
        table.setSpacingBefore(10);

        createStudentsTableHeader(table);

        for (StudentInReportsDTO student : students) {
            PdfPCell cell;

            cell = new PdfPCell(new Phrase(student.getIndex(), font));
            cell.setPaddingBottom(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(student.getName(), font));
            cell.setPaddingBottom(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(student.getSurname(), font));
            cell.setPaddingBottom(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(student.getMail(), font));
            cell.setPaddingBottom(5);
            table.addCell(cell);
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void writeStudentsDataToTheDocument(Map<String, Map<String, List<StudentInReportsDTO>>>
            studentsWithoutThesis, Document document) {
        for (Map.Entry<String, Map<String, List<StudentInReportsDTO>>> facultyEntry : studentsWithoutThesis.entrySet()) {
            for (Map.Entry<String, List<StudentInReportsDTO>> studyFieldEntry : facultyEntry.getValue().entrySet()) {
                Paragraph p = new Paragraph(facultyEntry.getKey() + " - " + studyFieldEntry.getKey(),
                        FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250"));
                document.add(p);

                createStudentsTable(document, studyFieldEntry.getValue());
            }
        }
    }

    public boolean generateStudentsWithoutThesisReport(HttpServletResponse response, String facultyAbbr,
            String studyFieldAbbr) throws DocumentException, IOException {

        Map<String, Map<String, List<StudentInReportsDTO>>> studentsWithoutThesis =
                getStudentsWithoutThesis(facultyAbbr, studyFieldAbbr);

        if (studentsWithoutThesis.isEmpty())
            return false;
        else {
            setResponseHeaders(response, "studenci_bez_tematu_zpi", facultyAbbr, studyFieldAbbr);
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();
            Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250");
            font.setSize(18);

            Paragraph p = new Paragraph("Lista studentów bez tematu projektu zpi", font);
            p.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p);
            document.add(Chunk.NEWLINE);

            writeStudentsDataToTheDocument(studentsWithoutThesis, document);
            document.close();
            return true;
        }
    }

    private void writeThesisGroupsDataToTheDocument(Map<String, Map<String, List<ThesisGroupDTO>>>
                                                        studentsWithoutThesis, Document document) {
        for (Map.Entry<String, Map<String, List<ThesisGroupDTO>>> facultyEntry : studentsWithoutThesis.entrySet()) {
            for (Map.Entry<String, List<ThesisGroupDTO>> studyFieldEntry : facultyEntry.getValue().entrySet()) {
                Paragraph p = new Paragraph(facultyEntry.getKey() + " - " + studyFieldEntry.getKey(),
                        FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250"));
                document.add(p);

                Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, "Cp1250");
                font.setSize(12);

                for (ThesisGroupDTO thesisGroup : studyFieldEntry.getValue()) {
                    p = new Paragraph("Temat: " + thesisGroup.getThesisNamePL(), font);
                    document.add(p);

                    p = new Paragraph("Prowadzący: " + thesisGroup.getSupervisor().getTitle() + " " +
                            thesisGroup.getSupervisor().getName() + " " + thesisGroup.getSupervisor().getSurname() +
                            " (" + thesisGroup.getSupervisor().getMail() + ")", font);
                    document.add(p);

                    createStudentsTable(document, thesisGroup.getStudents());
                }
            }
        }
    }


    public boolean generateThesisGroupsReport(HttpServletResponse response, String facultyAbbr,
                                                       String studyFieldAbbr) throws DocumentException, IOException {
        Map<String, Map<String, List<ThesisGroupDTO>>> getThesisGroups = getThesisGroups(facultyAbbr, studyFieldAbbr);

        if (getThesisGroups.isEmpty())
            return false;
        else {
            setResponseHeaders(response, "grupy_zpi", facultyAbbr, studyFieldAbbr);
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();
            Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250");
            font.setSize(18);

            Paragraph p = new Paragraph("Lista grup studentów wraz z przypisanymi prowadzącymi" +
                    "\ni tematem projektu zpi", font);
            p.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p);
            document.add(Chunk.NEWLINE);

            writeThesisGroupsDataToTheDocument(getThesisGroups, document);
            document.close();
            return true;
        }
    }
}
