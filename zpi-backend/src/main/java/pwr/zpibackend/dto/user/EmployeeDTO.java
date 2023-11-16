package pwr.zpibackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.dto.university.TitleDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private String mail;
    private String name;
    private String surname;
    private List<RoleDTO> roles;
    private String departmentCode;
    private TitleDTO title;
}
