package pwr.zpibackend.models.university;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import pwr.zpibackend.models.user.Student;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "student_program_cycle")
public class StudentProgramCycle {

    @EmbeddedId
    private StudentProgramCycleId id;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private Student student;

    @ManyToOne
    @MapsId("programId")
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @MapsId("cycleId")
    @JoinColumn(name = "cycle_id")
    private StudyCycle cycle;
}
