package pwr.zpibackend.models.user;


import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.models.thesis.Comment;
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
    private long id;
    @Column(name = "mail")
    private String mail;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_role",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();
    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Department department;
    @JoinColumn(name = "title", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Title title;

    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> comments;

}
