package pwr.zpibackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "studyfield")
public class StudyField {
    @Id
    private String abbreviation;
    @Column(nullable = false)
    private String name;
}