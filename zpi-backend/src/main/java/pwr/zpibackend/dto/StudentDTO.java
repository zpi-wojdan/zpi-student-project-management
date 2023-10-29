package pwr.zpibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.models.university.StudentProgramCycle;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private String mail;
    private String name;
    private String surname;
    private String index;
    private String status;
    private String role;
    private List<StudentProgramCycleDTO> programsCycles;
}
