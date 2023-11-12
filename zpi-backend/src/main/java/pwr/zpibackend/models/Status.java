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
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long Id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "status")
    @JsonIgnore
    private List<Thesis> theses;

    public Status(long id, String name) {
        Id = id;
        this.name = name;
    }

    public Status(String name) {
        this.name = name;
    }
}