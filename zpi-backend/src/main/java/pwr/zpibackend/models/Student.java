package pwr.zpibackend.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudyCycle;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student {

    @Id
    private String mail;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String index;
    @Column(nullable = false)
    private String status;

    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull(message = "Role cannot be null")
    private Role role;

    @JoinColumn(name = "program_code", referencedColumnName = "code")
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "student_program",
            joinColumns = @JoinColumn(name = "student_mail"),
            inverseJoinColumns = @JoinColumn(name = "program_id"))
    private List<Program> programs;

    @JoinColumn(name = "study_cycle_id", referencedColumnName = "id")
    @ManyToMany
    @JoinTable(
            name = "student_cycle",
            joinColumns = @JoinColumn(name = "student_mail"),
            inverseJoinColumns = @JoinColumn(name = "cycle_id"))
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private List<StudyCycle> studyCycles;
}
