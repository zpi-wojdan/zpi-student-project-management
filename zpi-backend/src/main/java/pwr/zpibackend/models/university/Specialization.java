package pwr.zpibackend.models.university;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "specialization")
public class Specialization {
    @Id
    private String abbreviation;
    @Column(nullable = false)
    private String name;

    @JoinColumn(name = "study_field_abbreviation")
    @ManyToOne(cascade = CascadeType.ALL)
    private StudyField studyField;
}
