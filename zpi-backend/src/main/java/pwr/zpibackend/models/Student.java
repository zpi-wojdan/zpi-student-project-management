package pwr.zpibackend.models;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String program;
    @Column(nullable = false)
    private String teaching_cycle;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private String role;    //  change String to Role when table exist

    private Date admission_date;
    private String stage;

    @JoinColumn(name = "program_code", referencedColumnName = "code")
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Program> programs;

    @JoinColumn(name = "study_cycle_id", referencedColumnName = "id")
    @ManyToMany
    private List<StudyCycle> studyCycles;
}
