package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import pwr.zpibackend.exceptions.EmployeeAndStudentWithTheSameEmailException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Student;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class AuthService {

    private final StudentService studentService;
    private final EmployeeService employeeService;

    public Object getUserDetails(String email) throws EmployeeAndStudentWithTheSameEmailException {
        Employee employee = null;
        Student student = null;

        if (!email.endsWith("pwr.edu.pl")) {
            throw new IllegalArgumentException("Email must be from pwr.edu.pl domain");
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
            throw new NoSuchElementException("User with email: " + email + " not found");
        }
    }

}
