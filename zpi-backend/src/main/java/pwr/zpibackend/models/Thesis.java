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
@Table(name = "thesis")
public class Thesis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "the_id")
    private long Id;
    private String namePL;
    private String nameEN;
    private String description;
    private Integer num_people;
    @JoinColumn(name = "supervisor" , referencedColumnName = "emp_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Employee supervisor;
    private String faculty; //change String to Faculty when table exist
    private String field; //change String to Field when table exist
    private String edu_cycle;

}
