package pwr.zpibackend.dto.university;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyDTO {
    private String abbreviation;
    private String name;
}
