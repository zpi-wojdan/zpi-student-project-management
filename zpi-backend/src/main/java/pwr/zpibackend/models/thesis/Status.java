package pwr.zpibackend.models.thesis;

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
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Unique identifier of the status.", example = "1")
    private long id;

    @Column(nullable = false)
    @Schema(description = "Name of the status.", example = "Draft")
    private String name;

    @OneToMany(mappedBy = "status")
    @JsonIgnore
    private List<Thesis> theses;

    public Status(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Status(String name) {
        this.name = name;
    }
}
