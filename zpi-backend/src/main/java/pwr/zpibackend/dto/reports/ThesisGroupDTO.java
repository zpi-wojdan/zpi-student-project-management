package pwr.zpibackend.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Student;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThesisGroupDTO {
    private String thesisNamePL;
    private String facultyAbbreviation;
    private String facultyName;
    private String studyFieldAbbreviation;
    private String studyFieldName;
    private SupervisorDTO supervisor;
    private List<StudentWithThesisDTO> students;
}
