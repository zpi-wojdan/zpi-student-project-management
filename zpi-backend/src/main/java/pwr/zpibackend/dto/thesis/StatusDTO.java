package pwr.zpibackend.dto.thesis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusDTO {
    @Schema(description = "Name of the status.", example = "Draft")
    private String name;
}
