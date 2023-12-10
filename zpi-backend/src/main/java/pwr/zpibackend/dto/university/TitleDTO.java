package pwr.zpibackend.dto.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TitleDTO {
    @Schema(description = "Name of the title.", example = "dr")
    private String name;

    @Schema(description = "Maximum number of theses that can be supervised by a person with this title.", example = "2")
    private Integer numTheses;
}
