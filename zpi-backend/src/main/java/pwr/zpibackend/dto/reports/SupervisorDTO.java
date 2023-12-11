package pwr.zpibackend.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorDTO {
    @Schema(description = "Supervisor's mail", example = "john.doe@pwr.edu.pl")
    private String mail;

    @Schema(description = "Supervisor's name", example = "John")
    private String name;

    @Schema(description = "Supervisor's surname", example = "Doe")
    private String surname;

    @Schema(description = "Supervisor's title", example = "dr")
    private String title;
}
