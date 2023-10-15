package pwr.zpibackend.models;

import jakarta.persistence.*;
import lombok.Data;
//import org.springframework.data.annotation.Id;

@Data
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
    @JoinColumn(name = "employee" , referencedColumnName = "emp_id")
    @OneToOne
    private Employee supervisor;
    private String faculty; //change String to Faculty when table exist
    private String field; //change String to Field when table exist
    private String edu_cycle;


    public Thesis() {
    }

    public Thesis(String namePL,String nameEN,String description,Integer num_people,
                  Employee supervisor,String faculty,String field,String edu_cycle) {
        this.namePL = namePL;
        this.nameEN = nameEN;
        this.description = description;
        this.num_people = num_people;
        this.supervisor = supervisor;
        this.faculty = faculty;
        this.field = field;
        this.edu_cycle = edu_cycle;
    }

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
