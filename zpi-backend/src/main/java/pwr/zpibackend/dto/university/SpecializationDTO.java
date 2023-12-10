package pwr.zpibackend.dto.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecializationDTO {
    @Schema(description = "Abbreviation of the specialization.", example = "INF")
    private String abbreviation;

    @Schema(description = "Name of the specialization.", example = "Informatics")
    private String name;

    @Schema(description = "Abbreviation of the study field of the specialization.", example = "IST")
    private String studyFieldAbbr;
}
