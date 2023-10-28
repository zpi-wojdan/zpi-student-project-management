package pwr.zpibackend.models.university;

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
    private String abbreviation;
    @Column(nullable = false)
    private String name;
    @JoinColumn(name = "abbreviation")
    @OneToMany(cascade = CascadeType.ALL)
    private List<StudyField> studyFields;
    @JoinColumn(name = "id")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "program",
            inverseJoinColumns = @JoinColumn(name = "id"))
    private List<Program> programs;
    @JoinColumn(name = "code")
    @OneToMany(cascade = CascadeType.ALL)
    private List<Department> departments;
}
