package pwr.zpibackend.dto.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyDTO {
    @Schema(description = "Abbreviation of the faculty", example = "W04N")
    private String abbreviation;

    @Schema(description = "Name of the faculty", example = "Faculty 1")
    private String name;
}
