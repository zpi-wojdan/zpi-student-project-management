package pwr.zpibackend.models.university;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "department")
public class Department {
    @Id
    private String code;
    @Column(nullable = false)
    private String name;
}
