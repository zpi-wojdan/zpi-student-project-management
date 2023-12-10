package pwr.zpibackend.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentInReportsDTO {
    @Schema(description = "Student's mail", example = "123456@student.pwr.edu.pl")
    private String mail;

    @Schema(description = "Student's name", example = "John")
    private String name;

    @Schema(description = "Student's surname", example = "Doe")
    private String surname;

    @Schema(description = "Student's index number", example = "123456")
    private String index;

    @Schema(description = "Student's faculty abbreviation", example = "W04N")
    private String facultyAbbreviation;

    @Schema(description = "Student's study field abbreviation", example = "IST")
    private String studyFieldAbbreviation;
}
