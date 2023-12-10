package pwr.zpibackend.controllers;

import com.lowagie.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.dto.reports.StudentInReportsDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.services.reports.IPdfService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/report")
public class PdfController {

    private final IPdfService pdfService;

    @GetMapping("/pdf/students-without-thesis")
    @Operation(summary = "Generate students without thesis report",
            description = "Generates pdf report with students without thesis from particular faculty and study field. " +
                    "If no faculty and study field are provided, all students without thesis are returned. <br>" +
                    "Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> generateStudentsWithoutThesisReport(HttpServletResponse response,
            @RequestParam(required = false) String facultyAbbr, @RequestParam(required = false) String studyFieldAbbr)
            throws DocumentException, IOException {
        if (pdfService.generateStudentsWithoutThesisReport(response, facultyAbbr, studyFieldAbbr))
            return new ResponseEntity<>("Report generated successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Students without thesis not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/pdf/thesis-groups")
    @Operation(summary = "Generate thesis groups report",
            description = "Generates pdf report with thesis groups from particular faculty and study field. " +
                    "If no faculty and study field are provided, all thesis groups are returned. <br>" +
                    "Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> generateThesisGroupsReport(HttpServletResponse response,
            @RequestParam(required = false) String facultyAbbr, @RequestParam(required = false) String studyFieldAbbr)
            throws DocumentException, IOException {
        if (pdfService.generateThesisGroupsReport(response, facultyAbbr, studyFieldAbbr))
            return new ResponseEntity<>("Report generated successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Thesis groups not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/pdf/thesis-declaration/{id}")
    @Operation(summary = "Generate thesis declaration",
            description = "Generates pdf declaration for thesis group with given id. <br>" +
                    "Requires ADMIN, STUDENT or SUPERVISOR role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_SUPERVISOR')")
    public ResponseEntity<String> generateThesisDeclaration(HttpServletResponse response, @PathVariable Long id)
            throws DocumentException, IOException {
        if (pdfService.generateThesisDeclaration(response, id))
            return new ResponseEntity<>("Declaration generated successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Thesis group not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/data/students-without-thesis")
    @Operation(summary = "Get students without thesis",
            description = "Returns list of students without thesis from particular faculty and study field. " +
                    "If no faculty and study field are provided, all students without thesis are returned. <br>" +
                    "Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Map<String, List<StudentInReportsDTO>>>> getStudentsWithoutThesis(
            @RequestParam(required = false) String facultyAbbr, @RequestParam(required = false) String studyFieldAbbr) {
        return new ResponseEntity<>(pdfService.getStudentsWithoutThesis(facultyAbbr, studyFieldAbbr), HttpStatus.OK);
    }

    @GetMapping("/data/thesis-groups")
    @Operation(summary = "Get thesis groups",
            description = "Returns list of thesis groups from particular faculty and study field. " +
                    "If no faculty and study field are provided, all thesis groups are returned. <br>" +
                    "Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Map<String, List<ThesisGroupDTO>>>> getThesisGroups(
            @RequestParam(required = false) String facultyAbbr, @RequestParam(required = false) String studyFieldAbbr) {
        return new ResponseEntity<>(pdfService.getThesisGroups(facultyAbbr, studyFieldAbbr), HttpStatus.OK);
    }

    @GetMapping("/data/thesis-declaration/{id}")
    @Operation(summary = "Get thesis group data",
            description = "Returns data of thesis group with given id. <br>" +
                    "Requires ADMIN, STUDENT or SUPERVISOR role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_SUPERVISOR')")
    public ResponseEntity<ThesisGroupDTO> getThesisGroupData(@PathVariable Long id) {
        ThesisGroupDTO thesisGroupDTO = pdfService.getThesisGroupDataById(id);
        if(thesisGroupDTO != null)
            return new ResponseEntity<>(thesisGroupDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
