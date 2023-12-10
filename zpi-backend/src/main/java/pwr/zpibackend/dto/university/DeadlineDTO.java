package pwr.zpibackend.dto.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeadlineDTO {
    @Schema(description = "Name of the activity in polish.", example = "Aktywność 1")
    private String namePL;

    @Schema(description = "Name of the activity in english.", example = "Activity 1")
    private String nameEN;

    @Schema(description = "Date of the deadline.", example = "2024-01-01")
    private LocalDate deadlineDate;
}
