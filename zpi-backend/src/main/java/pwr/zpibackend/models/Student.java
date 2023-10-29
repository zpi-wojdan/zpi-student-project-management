package pwr.zpibackend.models;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudentProgramCycle;
import pwr.zpibackend.models.university.StudyCycle;

import java.util.*;

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
    private String status;
    @Column(nullable = false)
    private String role;    //  change String to Role when table exist
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<StudentProgramCycle> studentProgramCycles = new HashSet<>();
}
