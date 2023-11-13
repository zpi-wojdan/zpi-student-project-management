package pwr.zpibackend.controllers;

import com.lowagie.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.services.PdfService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/pdf")
public class PdfController {

    private final PdfService pdfService;

    @GetMapping("/students-without-thesis")
    public void generateStudentsWithoutThesisReport(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=students_without_thesis_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        pdfService.generateStudentsWithoutThesisReport(response);
    }

    @GetMapping("/thesis-groups")
    public ResponseEntity<Map<String, Map<String, List<ThesisGroupDTO>>>> getThesisGroups(
            @RequestParam(required = false) Long facultyId, @RequestParam(required = false) Long studyFieldId) {
        return new ResponseEntity<>(pdfService.getThesisGroups(facultyId, studyFieldId), HttpStatus.OK);
    }
}
