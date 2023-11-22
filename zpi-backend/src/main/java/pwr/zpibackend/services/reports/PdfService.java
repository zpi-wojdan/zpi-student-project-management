package pwr.zpibackend.services.reports;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.reports.StudentInReportsDTO;
import pwr.zpibackend.dto.reports.SupervisorDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.models.university.*;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.repositories.thesis.ReservationRepository;
import pwr.zpibackend.repositories.user.StudentRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;

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

    private static final int numColumns = 4;
    private static final float tableWidth = 100f;
    private static final float[] columnWidths = {1.3f, 3.5f, 3.5f, 3.8f};
    private static final float spacing = 10f;
    private static final float padding = 5f;
    private static final Font titleFont = FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250", 18);
    private static final Font smallerTitleFont = FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250", 15);
    private static final Font tableHeaderFont = FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250", 12,
            Font.NORMAL, Color.WHITE);
    private static final Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, "Cp1250", 12);
    private static final Font sectionFont = FontFactory.getFont(FontFactory.TIMES_BOLD, "Cp1250");
    private static final Color headerColor = WebColors.getRGBColor("#9A342D");
    private static final String imageLogoPlPath = "src/main/resources/images/logoPl.png";
    private static final String imageLogoEnPath = "src/main/resources/images/logoEn.png";
    private static final float imageSize = 200.0f;
    private static final String thesisGroupsReportName = "grupy_zpi";
    private static final String studentsWithoutThesisReportName = "studenci_bez_tematu_zpi";
    private static final String thesisDeclarationName = "deklaracja_zpi";


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
                        .map(program -> setStudentData(student.getName(), student.getSurname(), student.getIndex(),
                                student.getMail(), program.getFaculty().getAbbreviation(),
                                program.getStudyField().getAbbreviation())))
                .collect(Collectors.groupingBy(StudentInReportsDTO::getFacultyAbbreviation,
                        TreeMap::new,
                        Collectors.groupingBy(StudentInReportsDTO::getStudyFieldAbbreviation,
                                TreeMap::new,
                                Collectors.toList())));
    }

    public Map<String, Map<String, List<ThesisGroupDTO>>> getThesisGroups(String facultyAbbr, String studyFieldAbbr) {
        return thesisRepository.findAllByOrderByNamePLAsc().stream()
                .filter(thesis -> thesis.getReservations() != null && !thesis.getReservations().isEmpty()
                        && thesis.getReservations().stream().allMatch(Reservation::isConfirmedBySupervisor))
                .filter(thesis -> thesis.getPrograms() != null && thesis.getPrograms().stream()
                        .anyMatch(program -> program.getFaculty() != null && program.getStudyField() != null &&
                                (facultyAbbr == null || program.getFaculty().getAbbreviation().equals(facultyAbbr)) &&
                                (studyFieldAbbr == null || program.getStudyField().getAbbreviation().equals(studyFieldAbbr))))
                .filter(thesis -> thesis.getLeader() != null && thesis.getLeader().getStudentProgramCycles() != null &&
                        thesis.getLeader().getStudentProgramCycles().stream()
                                .anyMatch(studentProgramCycle -> studentProgramCycle.getProgram() != null &&
                                        studentProgramCycle.getProgram().getFaculty() != null &&
                                        studentProgramCycle.getProgram().getStudyField() != null &&
                                        (facultyAbbr == null || studentProgramCycle.getProgram().getFaculty()
                                                .getAbbreviation().equals(facultyAbbr)) &&
                                        (studyFieldAbbr == null || studentProgramCycle.getProgram().getStudyField()
                                                .getAbbreviation().equals(studyFieldAbbr))))
                .map(thesis -> {
                    ThesisGroupDTO thesisGroupData = new ThesisGroupDTO();
                    thesisGroupData.setThesisNamePL(thesis.getNamePL());
                    thesisGroupData.setThesisNameEN(thesis.getNameEN());

                    setFacultyData(thesis, thesisGroupData, facultyAbbr);
                    setStudyFieldData(thesis, thesisGroupData, studyFieldAbbr);
                    setSupervisorData(thesis, thesisGroupData);

                    thesisGroupData.setStudents(thesis.getReservations().stream()
                            .map(reservation -> setStudentData(reservation.getStudent().getName(),
                                    reservation.getStudent().getSurname(), reservation.getStudent().getIndex(),
                                    reservation.getStudent().getMail(), thesisGroupData.getFacultyAbbreviation(),
                                    thesisGroupData.getStudyFieldAbbreviation()))
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
        Department department = thesis.getSupervisor().getDepartment();
        if(department != null) {
            supervisor.setDepartmentCode(department.getCode());
            supervisor.setDepartmentName(department.getName());
        }
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
        cell.setBackgroundColor(headerColor);
        cell.setPaddingBottom(padding);

        cell.setPhrase(new Phrase("Indeks", tableHeaderFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Imię", tableHeaderFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Nazwisko", tableHeaderFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Mail", tableHeaderFont));
        table.addCell(cell);
    }

    private void createStudentsTable(Document document, List<StudentInReportsDTO> students)
            throws DocumentException {
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(tableWidth);
        table.setWidths(columnWidths);
        table.setSpacingBefore(spacing);

        createStudentsTableHeader(table);

        for (StudentInReportsDTO student : students) {
            PdfPCell cell;

            cell = new PdfPCell(new Phrase(student.getIndex(), dataFont));
            cell.setPaddingBottom(padding);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(student.getName(), dataFont));
            cell.setPaddingBottom(padding);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(student.getSurname(), dataFont));
            cell.setPaddingBottom(padding);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(student.getMail(), dataFont));
            cell.setPaddingBottom(padding);
            table.addCell(cell);
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void writeStudentsDataToTheDocument(Map<String, Map<String, List<StudentInReportsDTO>>>
            studentsWithoutThesis, Document document) {
        for (Map.Entry<String, Map<String, List<StudentInReportsDTO>>> facultyEntry : studentsWithoutThesis.entrySet()) {
            for (Map.Entry<String, List<StudentInReportsDTO>> studyFieldEntry : facultyEntry.getValue().entrySet()) {
                Paragraph p = new Paragraph(facultyEntry.getKey() + " - " + studyFieldEntry.getKey(), sectionFont);
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
            setResponseHeaders(response, studentsWithoutThesisReportName, facultyAbbr, studyFieldAbbr);
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            Paragraph p = new Paragraph("Lista studentów bez tematu projektu zpi", titleFont);
            p.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p);
            document.add(Chunk.NEWLINE);

            writeStudentsDataToTheDocument(studentsWithoutThesis, document);
            document.close();
            return true;
        }
    }

    private void writeThesisGroupsDataToTheDocument(Map<String, Map<String, List<ThesisGroupDTO>>>
                                                        thesisGroups, Document document) {
        for (Map.Entry<String, Map<String, List<ThesisGroupDTO>>> facultyEntry : thesisGroups.entrySet()) {
            for (Map.Entry<String, List<ThesisGroupDTO>> studyFieldEntry : facultyEntry.getValue().entrySet()) {
                Paragraph p = new Paragraph(facultyEntry.getKey() + " - " + studyFieldEntry.getKey(), sectionFont);
                document.add(p);

                for (ThesisGroupDTO thesisGroup : studyFieldEntry.getValue()) {
                    p = new Paragraph("Temat: " + thesisGroup.getThesisNamePL(), dataFont);
                    document.add(p);

                    p = new Paragraph("Prowadzący: " + thesisGroup.getSupervisor().getTitle() + " " +
                            thesisGroup.getSupervisor().getName() + " " + thesisGroup.getSupervisor().getSurname() +
                            " (" + thesisGroup.getSupervisor().getMail() + ")", dataFont);
                    document.add(p);

                    createStudentsTable(document, thesisGroup.getStudents());
                }
            }
        }
    }


    public boolean generateThesisGroupsReport(HttpServletResponse response, String facultyAbbr,
                                                       String studyFieldAbbr) throws DocumentException, IOException {
        Map<String, Map<String, List<ThesisGroupDTO>>> thesisGroups = getThesisGroups(facultyAbbr, studyFieldAbbr);

        if (thesisGroups.isEmpty())
            return false;
        else {
            setResponseHeaders(response, thesisGroupsReportName, facultyAbbr, studyFieldAbbr);
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            Paragraph p = new Paragraph("Lista grup studentów wraz z przypisanymi prowadzącymi" +
                    "\ni tematem projektu zpi", titleFont);
            p.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p);
            document.add(Chunk.NEWLINE);

            writeThesisGroupsDataToTheDocument(thesisGroups, document);
            document.close();
            return true;
        }
    }

    public ThesisGroupDTO getThesisGroupDataById(Long id) {
        return thesisRepository.findById(id)
                .map(thesis -> {
                    if (thesis.getReservations() == null || thesis.getReservations().isEmpty() ||
                            thesis.getLeader() == null || thesis.getLeader().getStudentProgramCycles() == null ||
                            thesis.getLeader().getStudentProgramCycles().isEmpty() ||
                            thesis.getReservations().stream().anyMatch(reservation ->
                                    !reservation.isConfirmedBySupervisor())) {
                        return null;
                    }
                    ThesisGroupDTO thesisGroupData = new ThesisGroupDTO();
                    thesisGroupData.setThesisNamePL(thesis.getNamePL());
                    thesisGroupData.setThesisNameEN(thesis.getNameEN());
                    setSupervisorData(thesis, thesisGroupData);
                    setFacultyData(thesis, thesisGroupData, null);
                    setStudyFieldData(thesis, thesisGroupData, null);

                    thesisGroupData.setStudents(thesis.getReservations().stream()
                            .map(reservation -> setStudentData(reservation.getStudent().getName(),
                                    reservation.getStudent().getSurname(), reservation.getStudent().getIndex(),
                                    reservation.getStudent().getMail(), thesisGroupData.getFacultyAbbreviation(),
                                    thesisGroupData.getStudyFieldAbbreviation()))
                            .collect(Collectors.toList()));
                    return thesisGroupData;
                })
                .orElse(null);
    }


    private void addSpace(Document document, float size) throws DocumentException {
        Paragraph space = new Paragraph();
        space.setSpacingBefore(size);
        document.add(space);
    }

    private void createDeclarationContent(ThesisGroupDTO thesisGroupData, String language, Document document,
                                          PdfWriter writer) throws DocumentException, IOException {
        String imagePath = language.equals("pl") ? imageLogoPlPath : imageLogoEnPath;
        Image image = Image.getInstance(imagePath);
        image.scaleToFit(imageSize, imageSize);
        float x = document.left();
        float y = document.top() - image.getScaledHeight();
        image.setAbsolutePosition(x, y);
        document.add(image);

        Paragraph p = new Paragraph("Wrocław, " + new SimpleDateFormat("dd.MM.yyyy").format(new Date()),
                dataFont);
        p.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(p);
        addSpace(document, 50);

        String facultyText = language.equals("pl") ? "Wydział" : "Faculty";
        Paragraph faculty = new Paragraph(facultyText + ": " + thesisGroupData.getFacultyAbbreviation(), dataFont);
        document.add(faculty);

        String studyFieldText = language.equals("pl") ? "Kierunek" : "Study field";
        Paragraph studyField = new Paragraph(studyFieldText + ": " + thesisGroupData.getStudyFieldAbbreviation(),
                dataFont);
        document.add(studyField);

        String supervisorText = language.equals("pl") ? "Opiekun" : "Supervisor";
        Paragraph supervisor = new Paragraph(supervisorText + ": " + thesisGroupData.getSupervisor().getTitle() +
                " " + thesisGroupData.getSupervisor().getName() + " " + thesisGroupData.getSupervisor().getSurname(),
                dataFont);
        document.add(supervisor);

        String departmentText = language.equals("pl") ? "Katedra" : "Department";
        Paragraph department = new Paragraph(departmentText + ": " +
                thesisGroupData.getSupervisor().getDepartmentCode() + " - " +
                thesisGroupData.getSupervisor().getDepartmentName(), dataFont);
        document.add(department);
        addSpace(document, 50);

        String titleText = language.equals("pl") ? "Deklaracja realizacji Zespołowego Przedsięwzięcia Inżynierskiego" :
                "Declaration of the Team Engineering Project realization";
        Paragraph title = new Paragraph(titleText, smallerTitleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        addSpace(document, 50);

        String thesisNameText = language.equals("pl") ? "Temat pracy" : "Topic";
        Paragraph thesisNamePL = new Paragraph(thesisNameText + " (PL): " + thesisGroupData.getThesisNamePL(),
                dataFont);
        document.add(thesisNamePL);
        Paragraph thesisNameEN = new Paragraph(thesisNameText + " (EN): " + thesisGroupData.getThesisNameEN(),
                dataFont);
        document.add(thesisNameEN);
        addSpace(document, 30);

        String infoText = language.equals("pl") ? "Podpisy należy złożyć po prawej stronie obok swojego nazwiska" :
                "Signatures should be placed on the right side next to your name";
        Paragraph info = new Paragraph(infoText + ":", dataFont);
        document.add(info);
        addSpace(document, 20);

        for(StudentInReportsDTO student : thesisGroupData.getStudents()) {
            Paragraph studentData = new Paragraph("Student: " + student.getName() + " " + student.getSurname() +
                    " (" + student.getIndex() + ")", dataFont);
            document.add(studentData);
            addSpace(document, 20);
        }

        PdfContentByte cb = writer.getDirectContent();
        cb.beginText();
        cb.setFontAndSize(dataFont.getBaseFont(), 12);
        String signatureText = language.equals("pl") ? "Podpis opiekuna" : "Supervisor's signature";
        cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, signatureText, document.right() - 50,
                document.bottom() + 50, 0);
        cb.endText();
    }

    public boolean generateThesisDeclaration(HttpServletResponse response, Long thesisId) throws DocumentException,
            IOException {
        ThesisGroupDTO thesisGroupData = getThesisGroupDataById(thesisId);

        if (thesisGroupData == null)
            return false;
        else {
            setResponseHeaders(response, thesisDeclarationName, null, null);
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

            document.open();
            createDeclarationContent(thesisGroupData, "pl", document, writer);
            document.newPage();
            createDeclarationContent(thesisGroupData, "en", document, writer);
            document.close();
            return true;
        }
    }
}
