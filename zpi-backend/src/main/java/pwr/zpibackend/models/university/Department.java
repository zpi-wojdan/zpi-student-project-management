package pwr.zpibackend.models.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the department.", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Unique code of the department.", example = "K01")
    private String code;

    @Column(nullable = false)
    @Schema(description = "Name of the department.", example = "Department 1")
    private String name;

    @JoinColumn(name = "faculty_id", nullable = false)
    @ManyToOne
    @Schema(description = "Faculty to which the department belongs.")
    private Faculty faculty;
}
