package pwr.zpibackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
//import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "thesis")
public class Thesis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thesis_id")
    private long Id;
    @Column(name = "name_pl")
    private String namePL;
    @Column(name = "name_en")
    private String nameEN;
    private String description;
    private Integer num_people;

    @JoinColumn(name = "supervisor" , referencedColumnName = "mail")
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Employee supervisor;
    private String faculty; //change String to Faculty when table exist
    private String field; //change String to Field when table exist
    private String edu_cycle;

}
