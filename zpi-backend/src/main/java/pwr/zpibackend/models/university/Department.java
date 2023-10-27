package pwr.zpibackend.models.university;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "department")
public class Department {
    @Id
    private String code;
    @Column(nullable = false)
    private String name;
    @JoinColumn(name = "faculty_abbreviation", nullable = false)
    @ManyToOne
    @JsonIgnore
    private Faculty faculty;
}
