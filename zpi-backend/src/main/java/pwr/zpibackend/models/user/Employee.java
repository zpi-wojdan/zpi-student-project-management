package pwr.zpibackend.models.user;


import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pwr.zpibackend.models.thesis.Comment;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.models.university.Title;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the employee.", example = "1")
    private long id;

    @Column(name = "mail")
    @Schema(description = "Employee's email address.", example = "john.doe@pwr.edu.pl")
    private String mail;

    @Column(nullable = false)
    @Schema(description = "Employee's name.", example = "John")
    private String name;

    @Column(nullable = false)
    @Schema(description = "Employee's surname.", example = "Doe")
    private String surname;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_role",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Schema(description = "Employee's roles.")
    private List<Role> roles = new ArrayList<>();

    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    @Schema(description = "Employee's department.")
    private Department department;

    @JoinColumn(name = "title", referencedColumnName = "id", nullable = false)
    @ManyToOne
    @Schema(description = "Employee's title.")
    private Title title;

    @Column(name ="num_theses", nullable = false)
    @Schema(description = "Maximum number of theses that the employee can supervise.", example = "2")
    private Integer numTheses;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> comments;

    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Thesis> supervisedTheses = new ArrayList<>();

}
