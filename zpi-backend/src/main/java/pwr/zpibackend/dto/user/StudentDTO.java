package pwr.zpibackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.dto.university.StudentProgramCycleDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private String name;
    private String surname;
    private String index;
    private String status;
    private List<StudentProgramCycleDTO> programsCycles;
}
