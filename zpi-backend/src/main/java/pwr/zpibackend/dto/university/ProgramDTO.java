package pwr.zpibackend.dto.university;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramDTO {
    private String name;
    private String studyFieldAbbr;
    private String specializationAbbr;
    private List<Long> studyCycleIds;
    private Long facultyId;
}
