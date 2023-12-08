package pwr.zpibackend.dto.university;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeadlineDTO {
    private String namePL;
    private String nameEN;
    private LocalDate deadlineDate;
}
