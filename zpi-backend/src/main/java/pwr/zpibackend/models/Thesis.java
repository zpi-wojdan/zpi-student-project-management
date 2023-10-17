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
    private Employee supervisor;
    private String faculty; //change String to Faculty when table exist
    private String field; //change String to Field when table exist
    private String edu_cycle;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getNamePL() {
        return namePL;
    }

    public void setNamePL(String namePL) {
        this.namePL = namePL;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNum_people() {
        return num_people;
    }

    public void setNum_people(Integer num_people) {
        this.num_people = num_people;
    }

    public Employee getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Employee supervisor) {
        this.supervisor = supervisor;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getEdu_cycle() {
        return edu_cycle;
    }

    public void setEdu_cycle(String edu_cycle) {
        this.edu_cycle = edu_cycle;
    }
}
