package pwr.zpibackend.models;

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
    @JoinColumn(name = "study_field_abbreviation")
    @OneToMany(cascade = CascadeType.ALL)
    private List<StudyField> studyFields;
    @JoinColumn(name = "program_id")
    @OneToMany(cascade = CascadeType.ALL)
    private List<Program> programs;
    @JoinColumn(name = "department_code")
    @OneToMany(cascade = CascadeType.ALL)
    private List<Department> departments;
}
