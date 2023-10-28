package pwr.zpibackend.models;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudyCycle;

import java.util.Date;
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
    @Column(nullable = false)
    private String role;    //  change String to Role when table exist

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
