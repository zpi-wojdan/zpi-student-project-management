package pwr.zpibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentProgramCycleDTO {
    private Long programId;
    private Long cycleId;
}
