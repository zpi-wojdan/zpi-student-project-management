package pwr.zpibackend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.dto.university.StudentProgramCycleDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    @Schema(description = "Student's name.", example = "John")
    private String name;

    @Schema(description = "Student's surname.", example = "Doe")
    private String surname;

    @Schema(description = "Student's index number.", example = "123456")
    private String index;

    @Schema(description = "Student's status.", example = "STU")
    private String status;

    @Schema(description = "Student's programs cycles.")
    private List<StudentProgramCycleDTO> programsCycles;
}
