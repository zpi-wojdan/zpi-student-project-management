package pwr.zpibackend.dto.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyFieldDTO {
    @Schema(description = "Abbreviation of the study field.", example = "IST")
    private String abbreviation;

    @Schema(description = "Name of the study field.", example = "Applied Computer Science")
    private String name;

    @Schema(description = "Abbreviation of the faculty to which the study field belongs.", example = "W04N")
    private String facultyAbbr;
}
