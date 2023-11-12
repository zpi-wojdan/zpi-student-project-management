package pwr.zpibackend.models.university;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "program")
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotNull(message = "Program name cannot be null")
    private String name;
    @JoinColumn(name = "study_field_id")
    @OneToOne(cascade = CascadeType.ALL)
    private StudyField studyField;
    @JoinColumn(name = "specialization_id")
    @OneToOne(cascade = CascadeType.ALL)
    private Specialization specialization;

    @JoinColumn(name = "study_cycle_id", referencedColumnName = "id")
    @ManyToMany
    @JoinTable(
            name = "program_cycle",
            joinColumns = @JoinColumn(name = "program_id"),
            inverseJoinColumns = @JoinColumn(name = "cycle_id"))
    private List<StudyCycle> studyCycles;

    @JoinColumn(name = "faculty_id")
    @ManyToOne
    private Faculty faculty;

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
