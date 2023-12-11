package pwr.zpibackend.dto.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramDTO {
    @Schema(description = "Name of the program.", example = "W04-INAP-CCSA-OSME3")
    private String name;

    @Schema(description = "Study field abbreviation of the program.", example = "INAP")
    private String studyFieldAbbr;

    @Schema(description = "Specialization abbreviation of the program.", example = "CCSA")
    private String specializationAbbr;

    @Schema(description = "Study cycles of the program.", example = "[1, 2]")
    private List<Long> studyCycleIds;

    @Schema(description = "Faculty of the program.", example = "1")
    private Long facultyId;
}
