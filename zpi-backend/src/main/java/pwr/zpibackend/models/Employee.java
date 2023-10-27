package pwr.zpibackend.models;


import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.models.university.Department;

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
    private String role;    //  change String to Role when table exist
    @JoinColumn(name = "department_code", referencedColumnName = "code", nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private Department department;
    private String title;   //  change String to Title when table exist
}
