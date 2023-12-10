package pwr.zpibackend.models.university;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Unique identifier of the title.", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Name of the title.", example = "dr")
    private String name;

    @Column(name ="num_theses", nullable = false)
    @Schema(description = "Maximum number of theses that can be supervised by a person with this title.", example = "2")
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
