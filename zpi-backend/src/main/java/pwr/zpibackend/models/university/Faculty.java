package pwr.zpibackend.models.university;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "faculty")
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Unique identifier of the faculty", example = "1")
    private long Id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Abbreviation of the faculty", example = "W04N")
    private String abbreviation;

    @Column(nullable = false)
    @Schema(description = "Name of the faculty", example = "Faculty 1")
    private String name;

    @JoinColumn(name = "id")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "program",
            inverseJoinColumns = @JoinColumn(name = "id"))
    @JsonIgnore
    private List<Program> programs;

    @JoinColumn(name = "faculty_id")
    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Department> departments;
}
