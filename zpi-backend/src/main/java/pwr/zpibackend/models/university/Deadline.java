package pwr.zpibackend.models.university;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deadline")
public class Deadline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long Id;
    @Column(name = "name_pl", nullable = false, unique = true)
    private String namePL;
    @Column(name = "name_en", nullable = false, unique = true)
    private String nameEN;
    @Column(name = "deadline_date", nullable = false)
    private LocalDate deadlineDate;
}
