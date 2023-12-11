package pwr.zpibackend.models.university;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import pwr.zpibackend.models.user.Student;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "student_program_cycle")
public class StudentProgramCycle {

    @EmbeddedId
    @Schema(description = "Composite id of student, program and cycle")
    private StudentProgramCycleId id;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    @Schema(description = "Student that is enrolled in program and cycle")
    private Student student;

    @ManyToOne
    @MapsId("programId")
    @JoinColumn(name = "program_id")
    @Schema(description = "Program that student is enrolled in")
    private Program program;

    @ManyToOne
    @MapsId("cycleId")
    @JoinColumn(name = "cycle_id")
    @Schema(description = "Cycle that student is enrolled in")
    private StudyCycle cycle;
}
