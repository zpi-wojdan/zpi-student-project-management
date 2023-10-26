package pwr.zpibackend.models;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @NotNull(message = "Roles cannot be null")
    private List<Role> roles;

    private String department_symbol;
    private String title;   //  change String to Title when table exist

}
