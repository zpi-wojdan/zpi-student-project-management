package pwr.zpibackend.models.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import pwr.zpibackend.models.university.StudentProgramCycle;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the student.", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Student's email address.", example = "123456@student.pwr.edu.pl")
    private String mail;

    @Column(nullable = false)
    @Schema(description = "Student's name.", example = "John")
    private String name;

    @Column(nullable = false)
    @Schema(description = "Student's surname.", example = "Doe")
    private String surname;

    @Column(nullable = false)
    @Schema(description = "Student's index number.", example = "123456")
    private String index;

    @Column(nullable = false)
    @Schema(description = "Student's status.", example = "STU")
    private String status;

    @NotNull(message = "Role cannot be null")
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @Schema(description = "Student's role.")
    private Role role;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(description = "Student's programs cycles.")
    private Set<StudentProgramCycle> studentProgramCycles = new HashSet<>();
}
