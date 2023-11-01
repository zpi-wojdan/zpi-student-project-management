package pwr.zpibackend.models;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.models.university.Department;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    private String mail;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_role",
            joinColumns = @JoinColumn(name = "mail"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @JoinColumn(name = "department_code", referencedColumnName = "code", nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private Department department;
    private String title;   //  change String to Title when table exist

}
