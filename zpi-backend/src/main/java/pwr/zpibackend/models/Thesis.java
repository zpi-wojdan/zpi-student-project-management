package pwr.zpibackend.models;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

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
    @Column(name = "name_pl", nullable = false)
    private String namePL;
    @Column(name = "name_en", nullable = false)
    private String nameEN;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Integer num_people;

    @JoinColumn(name = "supervisor" , referencedColumnName = "mail", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Employee supervisor;
    @Column(nullable = false)
    private String faculty; //change String to lIST of faculties when table exist
    @Column(nullable = false)
    private String field; //change String to Field when table exist
    @Column(nullable = false)
    private String edu_cycle;

    @Column(nullable = false)
    private String status; //change String to Status when table exist

}
