package pwr.zpibackend.models.thesis;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Student;

import java.util.List;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "thesis")
public class Thesis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long Id;
    @Column(name = "name_pl", nullable = false)
    private String namePL;
    @Column(name = "name_en", nullable = false)
    private String nameEN;
    @Column(name = "description_pl", nullable = false)
    private String descriptionPL;
    @Column(name = "description_en", nullable = false)
    private String descriptionEN;
    @Column(name ="num_people", nullable = false)
    private Integer numPeople;
    @JoinColumn(name = "supervisor" , referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Employee supervisor;
    @JoinColumn(name = "leader", referencedColumnName = "id")
    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Student leader;
    @ManyToMany
    @JoinTable(
            name = "program_thesis",
            joinColumns = @JoinColumn(name = "thesis_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id"))
    private List<Program> programs;
    @JoinColumn(name = "cycle_id", referencedColumnName = "id")
    @ManyToOne
    private StudyCycle studyCycle;
    @JoinColumn(name = "status", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Status status;
    @Column(nullable = false)
    private Integer occupied = 0;
    @JoinColumn(name = "thesis_id")
    @OneToMany(cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationTime;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "thesis")
    private List<Comment> comments;
}
