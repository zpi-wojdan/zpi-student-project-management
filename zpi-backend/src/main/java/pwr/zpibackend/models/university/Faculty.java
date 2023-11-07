package pwr.zpibackend.models.university;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "faculty")
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long Id;
    @Column(nullable = false, unique = true)
    private String abbreviation;
    @Column(nullable = false)
    private String name;
    @JoinColumn(name = "id")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "program",
            inverseJoinColumns = @JoinColumn(name = "id"))
    @JsonIgnore
    private List<Program> programs;
    @JoinColumn(name = "faculty_id")
    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Department> departments;
}
