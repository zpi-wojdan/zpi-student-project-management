package pwr.zpibackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_role_id_seq")
    @SequenceGenerator(name = "role_role_id_seq", sequenceName = "role_role_id_seq", allocationSize = 1)
    @Column(name = "role_id")
    private long Id;

    @Column(nullable = false)
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
