package pwr.zpibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private String title;
}
