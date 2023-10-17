package pwr.zpibackend.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    private String mail;

    private String name;
    private String surname;
    private String title;   //  change String to Title when table exist
    private String role;    //  change String to Role when table exist
    private String department_symbol;
}
