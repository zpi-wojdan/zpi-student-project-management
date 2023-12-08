package pwr.zpibackend.services.reports;

import com.lowagie.text.DocumentException;
import pwr.zpibackend.dto.reports.StudentInReportsDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IPdfService {
    Map<String, Map<String, List<StudentInReportsDTO>>> getStudentsWithoutThesis(String facultyAbbr,
                                                                                 String studyFieldAbbr);
    Map<String, Map<String, List<ThesisGroupDTO>>> getThesisGroups(String facultyAbbr, String studyFieldAbbr);
    boolean generateStudentsWithoutThesisReport(HttpServletResponse response, String facultyAbbr,
                                                String studyFieldAbbr) throws DocumentException, IOException;
    boolean generateThesisGroupsReport(HttpServletResponse response, String facultyAbbr,
                                       String studyFieldAbbr) throws DocumentException, IOException;
    ThesisGroupDTO getThesisGroupDataById(Long id);
    boolean generateThesisDeclaration(HttpServletResponse response, Long thesisId) throws DocumentException, IOException;
}
