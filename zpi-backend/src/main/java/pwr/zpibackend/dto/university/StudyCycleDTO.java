package pwr.zpibackend.dto.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyCycleDTO {
    @Schema(description = "Name of the study cycle.", example = "2023/24-Z")
    private String name;
}
