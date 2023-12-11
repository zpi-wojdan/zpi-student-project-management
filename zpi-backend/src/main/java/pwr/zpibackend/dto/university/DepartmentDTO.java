package pwr.zpibackend.dto.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDTO {
    @Schema(description = "Code of the department.", example = "K01")
    private String code;

    @Schema(description = "Name of the department.", example = "Department 1")
    private String name;

    @Schema(description = "Abbreviation of the faculty to which the department belongs.", example = "W04N")
    private String facultyAbbreviation;
}
