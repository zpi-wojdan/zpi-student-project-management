package pwr.zpibackend.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentWithoutThesisDTO {
    private String mail;
    private String name;
    private String surname;
    private String index;
    private String facultyAbbreviation;
    private String facultyName;
    private String studyFieldAbbreviation;
    private String studyFieldName;
}
