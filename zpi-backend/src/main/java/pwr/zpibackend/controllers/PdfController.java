package pwr.zpibackend.controllers;

import com.lowagie.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pwr.zpibackend.dto.reports.StudentInReportsDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.services.PdfService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/report")
public class PdfController {

    private final PdfService pdfService;

    @GetMapping("pdf/students-without-thesis")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> generateStudentsWithoutThesisReport(HttpServletResponse response,
            @RequestParam(required = false) String facultyAbbr, @RequestParam(required = false) String studyFieldAbbr)
            throws DocumentException, IOException {
        if (pdfService.generateStudentsWithoutThesisReport(response, facultyAbbr, studyFieldAbbr))
            return new ResponseEntity<>("Report generated successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Students without thesis not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("pdf/thesis-groups")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> generateThesisGroupsReport(HttpServletResponse response,
            @RequestParam(required = false) String facultyAbbr, @RequestParam(required = false) String studyFieldAbbr)
            throws DocumentException, IOException {
        if (pdfService.generateThesisGroupsReport(response, facultyAbbr, studyFieldAbbr))
            return new ResponseEntity<>("Report generated successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Thesis groups not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("data/students-without-thesis")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Map<String, List<StudentInReportsDTO>>>> getStudentsWithoutThesis(
            @RequestParam(required = false) String facultyAbbr, @RequestParam(required = false) String studyFieldAbbr) {
        return new ResponseEntity<>(pdfService.getStudentsWithoutThesis(facultyAbbr, studyFieldAbbr), HttpStatus.OK);
    }

    @GetMapping("data/thesis-groups")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Map<String, List<ThesisGroupDTO>>>> getThesisGroups(
            @RequestParam(required = false) String facultyAbbr, @RequestParam(required = false) String studyFieldAbbr) {
        return new ResponseEntity<>(pdfService.getThesisGroups(facultyAbbr, studyFieldAbbr), HttpStatus.OK);
    }
}
