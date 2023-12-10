package pwr.zpibackend.models.university;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "program")
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the program.", example = "1")
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Program name cannot be null")
    @Schema(description = "Name of the program.", example = "W04-INAP-CCSA-OSME3")
    private String name;

    @JoinColumn(name = "study_field_id")
    @OneToOne(cascade = CascadeType.ALL)
    @Schema(description = "Study field of the program.")
    private StudyField studyField;

    @JoinColumn(name = "specialization_id")
    @OneToOne(cascade = CascadeType.ALL)
    @Schema(description = "Specialization of the program.")
    private Specialization specialization;

    @JoinColumn(name = "study_cycle_id", referencedColumnName = "id")
    @ManyToMany
    @JoinTable(
            name = "program_cycle",
            joinColumns = @JoinColumn(name = "program_id"),
            inverseJoinColumns = @JoinColumn(name = "cycle_id"))
    @Schema(description = "Study cycles of the program.")
    private List<StudyCycle> studyCycles;

    @JoinColumn(name = "faculty_id")
    @ManyToOne
    @Schema(description = "Faculty of the program.")
    private Faculty faculty;

    public String language() {
        String[] parts = name.split("-");
        int length = parts[1].length();
        String departmentCode = parts[1];
        String language = departmentCode.substring(length - 1);
        if (Pattern.matches("[A-Z]", language)) {
            switch (language) {
                case "P":
                    return "pl";
                case "A":
                    return "en";
            }
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return Objects.equals(name, program.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
