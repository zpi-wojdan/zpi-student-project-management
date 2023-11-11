package pwr.zpibackend.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.dto.university.TitleDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorDTO {
    private String mail;
    private String name;
    private String surname;
    private String title;
}
