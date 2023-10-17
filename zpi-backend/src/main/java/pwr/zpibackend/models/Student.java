package pwr.zpibackend.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student {

    @Id
    private String mail;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String index;
    @Column(nullable = false)
    private String program;
    @Column(nullable = false)
    private String teaching_cycle;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private String role;    //  change String to Role when table exist

    private Date admission_date;
    private String stage;

    //@Column(nullable = false)
    //private List<StudyField> study_field;

}
