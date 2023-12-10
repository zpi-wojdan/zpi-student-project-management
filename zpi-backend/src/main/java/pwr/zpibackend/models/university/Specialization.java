package pwr.zpibackend.models.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "specialization")
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the specialization.", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Abbreviation of the specialization.", example = "INF")
    private String abbreviation;

    @Column(nullable = false)
    @Schema(description = "Name of the specialization.", example = "Informatics")
    private String name;

    @JoinColumn(name = "study_field_id")
    @ManyToOne(cascade = CascadeType.ALL)
    @Schema(description = "Study field of the specialization.")
    private StudyField studyField;
}
