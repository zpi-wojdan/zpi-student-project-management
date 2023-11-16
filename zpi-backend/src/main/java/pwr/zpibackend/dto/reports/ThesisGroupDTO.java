package pwr.zpibackend.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThesisGroupDTO {
    private String thesisNamePL;
    private String facultyAbbreviation;
    private String studyFieldAbbreviation;
    private SupervisorDTO supervisor;
    private List<StudentInReportsDTO> students;
}
