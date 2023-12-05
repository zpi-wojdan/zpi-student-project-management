package pwr.zpibackend.models.university;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.models.user.Employee;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "title")
public class Title {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(name ="num_theses", nullable = false)
    private Integer numTheses;
    @JoinColumn(name = "title")
    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    List<Employee> employees;

    public Title(Long id, String name, Integer numTheses) {
        this.id = id;
        this.name = name;
        this.numTheses = numTheses;
    }
    public Title(String name, Integer numTheses) {
        this.name = name;
        this.numTheses = numTheses;
    }
}
