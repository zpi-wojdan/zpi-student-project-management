package pwr.zpibackend.models.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class StudentProgramCycleId implements Serializable {
    @Schema(description = "Id of the student", example = "1")
    private Long studentId;

    @Schema(description = "Id of the program", example = "1")
    private Long programId;

    @Schema(description = "Id of the cycle", example = "1")
    private Long cycleId;
}
