package pwr.zpibackend.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThesisGroupDTO {
    @Schema(description = "Name of the thesis in polish", example = "Temat pracy")
    private String thesisNamePL;

    @Schema(description = "Name of the thesis in english", example = "Thesis name")
    private String thesisNameEN;

    @Schema(description = "Abbreviation of the faculty", example = "W04N")
    private String facultyAbbreviation;

    @Schema(description = "Abbreviation of the study field", example = "IST")
    private String studyFieldAbbreviation;

    @Schema(description = "Supervisor of the thesis")
    private SupervisorDTO supervisor;

    @Schema(description = "List of students in the thesis group")
    private List<StudentInReportsDTO> students;
}
