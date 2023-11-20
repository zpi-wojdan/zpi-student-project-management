package pwr.zpibackend.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorDTO {
    private String mail;
    private String name;
    private String surname;
    private String title;
    private String departmentCode;
    private String departmentName;
}
