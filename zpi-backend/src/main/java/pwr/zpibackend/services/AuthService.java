package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final StudentService studentService;
    private final EmployeeService employeeService;

    public Object getUserDetails(String email) {
        Object user = studentService.getStudent(email);
        if (user == null) {
            user = employeeService.getEmployee(email);
        }
        return user;
    }

}
