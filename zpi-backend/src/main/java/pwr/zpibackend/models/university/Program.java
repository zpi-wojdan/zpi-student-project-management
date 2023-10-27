package pwr.zpibackend.models.university;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "program")
public class Program {
    @Id
    @GeneratedValue
    private Long Id;
    @Column(nullable = false)
    @NotNull(message = "Program name cannot be null")
    private String name;
    @JoinColumn(name = "study_field_abbreviation")
    @OneToOne(cascade = CascadeType.ALL)
    private StudyField studyField;
    @JoinColumn(name = "specialization_abbreviation")
    @OneToOne(cascade = CascadeType.ALL)
    private Specialization specialization;

    @JoinColumn(name = "study_cycle_id", referencedColumnName = "id")
    @ManyToMany
    @JoinTable(
            name = "program_cycle",
            joinColumns = @JoinColumn(name = "program_id"),
            inverseJoinColumns = @JoinColumn(name = "cycle_id"))
    private List<StudyCycle> studyCycles;
}
