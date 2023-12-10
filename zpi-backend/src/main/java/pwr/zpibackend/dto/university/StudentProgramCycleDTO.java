package pwr.zpibackend.dto.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentProgramCycleDTO {
    @Schema(description = "Id of the program", example = "1")
    private Long programId;

    @Schema(description = "Id of the cycle", example = "1")
    private Long cycleId;
}
