package pwr.zpibackend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.dto.university.TitleDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    @Schema(description = "Employee's email address.", example = "john.doe@pwr.edu.pl")
    private String mail;

    @Schema(description = "Employee's name.", example = "John")
    private String name;

    @Schema(description = "Employee's surname.", example = "Doe")
    private String surname;

    @Schema(description = "Employee's roles.")
    private List<RoleDTO> roles;

    @Schema(description = "Employee's department code.", example = "K01")
    private String departmentCode;

    @Schema(description = "Employee's title.")
    private TitleDTO title;

    @Schema(description = "Maximum number of theses that the employee can supervise.", example = "2")
    private Integer numTheses;
}
