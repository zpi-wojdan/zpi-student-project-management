package pwr.zpibackend.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Unique identifier of the role.", example = "1")
    private long Id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Name of the role.", example = "admin")
    private String name;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<Student> students;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<Employee> employees;

    public Role(long id, String name) {
        Id = id;
        this.name = name;
    }
    public Role(String name) {
        this.name = name;
    }
}
