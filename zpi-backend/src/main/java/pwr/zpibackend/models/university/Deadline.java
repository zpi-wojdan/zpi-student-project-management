package pwr.zpibackend.models.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deadline")
public class Deadline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Unique identifier of the deadline.", example = "1")
    private long Id;

    @Column(name = "name_pl", nullable = false, unique = true)
    @Schema(description = "Name of the activity in polish.", example = "Aktywność 1")
    private String namePL;

    @Column(name = "name_en", nullable = false, unique = true)
    @Schema(description = "Name of the activity in english.", example = "Activity 1")
    private String nameEN;

    @Column(name = "deadline_date", nullable = false)
    @Schema(description = "Date of the deadline.", example = "2024-01-01")
    private LocalDate deadlineDate;
}
