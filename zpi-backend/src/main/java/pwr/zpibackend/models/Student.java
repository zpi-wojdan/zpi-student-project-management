package pwr.zpibackend.models;

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

    private String name;
    private String surname;
    private String index;
    private String program;
    private String teaching_cycle;
    private String stage;
    private Date admission_date;
    private String role;    //  change String to Role when table exist
    private String status;

}
