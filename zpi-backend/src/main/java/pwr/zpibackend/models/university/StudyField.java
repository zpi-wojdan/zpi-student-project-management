package pwr.zpibackend.models.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "study_field")
public class StudyField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the study field.", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Abbreviation of the study field.", example = "IST")
    private String abbreviation;

    @Column(nullable = false)
    @Schema(description = "Name of the study field.", example = "Applied Computer Science")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "faculty_id", referencedColumnName = "id")
    @Schema(description = "Faculty to which the study field belongs.")
    private Faculty faculty;
}