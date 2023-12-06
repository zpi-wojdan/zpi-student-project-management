package pwr.zpibackend.services.impl.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import pwr.zpibackend.exceptions.EmployeeAndStudentWithTheSameEmailException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.services.user.IAuthService;
import pwr.zpibackend.services.user.IEmployeeService;
import pwr.zpibackend.services.user.IStudentService;

import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class AuthService implements IAuthService {

    private final IStudentService studentService;
    private final IEmployeeService employeeService;

    public Object getUserDetails(String email) {
        Employee employee = null;
        Student student = null;

        if (!Pattern.matches("^[a-z0-9-]{1,50}(\\.[a-z0-9-]{1,50}){0,4}@(?:student\\.)" +
                "?(pwr\\.edu\\.pl|pwr\\.wroc\\.pl)$", email)) {
            throw new IllegalArgumentException("Email must be from pwr.edu.pl or pwr.wroc.pl domain");
        }

        String googleEmail = (String) RequestContextHolder.currentRequestAttributes()
                .getAttribute("googleEmail", RequestAttributes.SCOPE_REQUEST);

        if (googleEmail == null || !googleEmail.equals(email)) {
            throw new IllegalArgumentException("Email does not match the token email");
        }

        try {
            employee = employeeService.getEmployee(email);
        } catch (Exception ignored) {}

        try {
            student = studentService.getStudent(email);
        } catch (Exception ignored) {}

        if (employee != null && student != null) {
            throw new EmployeeAndStudentWithTheSameEmailException(email);
        } else if (employee != null) {
            return employee;
        } else if (student != null) {
            return student;
        } else {
            throw new NotFoundException("User with email: " + email + " not found");
        }
    }

}
