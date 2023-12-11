package pwr.zpibackend.models.thesis;

import javax.persistence.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
    @Schema(description = "Unique identifier of the thesis.", example = "1")
    private long id;

    @Column(name = "name_pl", nullable = false)
    @Schema(description = "Name of the thesis in Polish.", example = "Temat pracy")
    private String namePL;

    @Column(name = "name_en", nullable = false)
    @Schema(description = "Name of the thesis in English.", example = "Thesis topic")
    private String nameEN;

    @Column(name = "description_pl", nullable = false)
    @Schema(description = "Description of the thesis in Polish.", example = "Opis pracy")
    private String descriptionPL;

    @Column(name = "description_en", nullable = false)
    @Schema(description = "Description of the thesis in English.", example = "Thesis description")
    private String descriptionEN;

    @Column(name ="num_people", nullable = false)
    @Schema(description = "Maximum number of people that can work on the thesis.", example = "4")
    private Integer numPeople;

    @JoinColumn(name = "supervisor" , referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Schema(description = "Employee who is the supervisor of the thesis.")
    private Employee supervisor;

    @JoinColumn(name = "leader", referencedColumnName = "id")
    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Schema(description = "Student who is the leader of the thesis.")
    private Student leader;

    @ManyToMany
    @JoinTable(
            name = "program_thesis",
            joinColumns = @JoinColumn(name = "thesis_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id"))
    @Schema(description = "List of programs for which the thesis is available.")
    private List<Program> programs;

    @JoinColumn(name = "cycle_id", referencedColumnName = "id")
    @ManyToOne
    @Schema(description = "Study cycle for which the thesis is available.")
    private StudyCycle studyCycle;

    @JoinColumn(name = "status", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @Schema(description = "Status of the thesis.")
    private Status status;

    @Column(nullable = false)
    @Schema(description = "Number of students who have reserved the thesis.", example = "0")
    private Integer occupied = 0;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "thesis", orphanRemoval = true)
    @Schema(description = "List of reservations for the thesis.")
    private List<Reservation> reservations;

    @CreationTimestamp
    @Column(name = "creation_time", updatable = false)
    @Schema(description = "Time when the thesis was created.", example = "2024-01-01 12:00:00")
    private LocalDateTime creationTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "thesis", orphanRemoval = true)
    @Schema(description = "List of comments for the thesis.")
    private List<Comment> comments;
}
