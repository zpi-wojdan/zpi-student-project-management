package pwr.zpibackend.models;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudyCycle;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "thesis")
public class Thesis {

    @Id
    @GeneratedValue(generator = "thesis_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "thesis_seq", sequenceName = "thesis_seq", allocationSize = 1)
    @Column(name = "thesis_id")
    private long Id;
    @Column(name = "name_pl", nullable = false)
    private String namePL;
    @Column(name = "name_en", nullable = false)
    private String nameEN;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Integer num_people;

    @JoinColumn(name = "supervisor" , referencedColumnName = "mail", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Employee supervisor;
    @JoinColumn(name = "leader", referencedColumnName = "mail")
    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Student leader;
    @JoinColumn(name = "program_code", referencedColumnName = "code" )
    @ManyToMany
    @JoinTable(
            name = "program_thesis",
            joinColumns = @JoinColumn(name = "thesis_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id"))
    private List<Program> programs;
    @JoinColumn(name = "cycle_id", referencedColumnName = "id")
    @ManyToOne
    private StudyCycle studyCycle;
    @Column(nullable = false)
    private String status; //change String to Status when table exist
    @Column(nullable = false)
    private Integer occupied = 0;
    @JoinColumn(name = "thesis_id")
    @OneToMany(cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}
