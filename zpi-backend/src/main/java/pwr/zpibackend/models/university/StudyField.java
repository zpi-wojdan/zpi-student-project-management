package pwr.zpibackend.models.university;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "study_field")
public class StudyField {
    @Id
    private String abbreviation;
    @Column(nullable = false)
    private String name;
}