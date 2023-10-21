package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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
