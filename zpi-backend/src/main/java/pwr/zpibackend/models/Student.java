package pwr.zpibackend.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)
    @ManyToOne
    @NotNull(message = "Role cannot be null")
    private Role role;

    private Date admission_date;
    private String stage;

    //@Column(nullable = false)
    //private List<StudyField> study_field;

}
