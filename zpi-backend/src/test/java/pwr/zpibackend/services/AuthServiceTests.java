package pwr.zpibackend.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import pwr.zpibackend.exceptions.EmployeeAndStudentWithTheSameEmailException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Student;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTests {
    @InjectMocks
    private AuthService authService;

    @Mock
    private StudentService studentService;

    @Mock
    private EmployeeService employeeService;

    @Test
    public void testGetUserDetailsWithNoUserFound() throws Exception {
        String email = "123456@pwr.edu.pl";

        when(studentService.getStudent(anyString())).thenReturn(null);
        when(employeeService.getEmployee(anyString())).thenReturn(null);

        RequestAttributes requestAttributes = Mockito.mock(RequestAttributes.class);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        when(requestAttributes.getAttribute("googleEmail", RequestAttributes.SCOPE_REQUEST)).thenReturn(email);

        assertThrows(NoSuchElementException.class, () -> {
            authService.getUserDetails(email);
        });
    }

    @Test
    public void testGetUserDetailsWithEmployeeFound() throws Exception {
        String email = "123456@pwr.edu.pl";

        Employee employee = new Employee();
        when(employeeService.getEmployee(anyString())).thenReturn(employee);
        when(studentService.getStudent(anyString())).thenReturn(null);

        RequestAttributes requestAttributes = Mockito.mock(RequestAttributes.class);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        when(requestAttributes.getAttribute("googleEmail", RequestAttributes.SCOPE_REQUEST)).thenReturn(email);

        Object result = authService.getUserDetails(email);
        assertSame(employee, result);
    }

    @Test
    public void testGetUserDetailsWithStudentFound() throws Exception {
        String email = "123456@pwr.edu.pl";

        Student student = new Student();
        when(studentService.getStudent(anyString())).thenReturn(student);
        when(employeeService.getEmployee(anyString())).thenReturn(null);

        RequestAttributes requestAttributes = Mockito.mock(RequestAttributes.class);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        when(requestAttributes.getAttribute("googleEmail", RequestAttributes.SCOPE_REQUEST)).thenReturn(email);

        Object result = authService.getUserDetails(email);
        assertSame(student, result);
    }

    @Test
    public void testGetUserDetailsWithInvalidDomain() {
        String email = "123456@gmail.com";

        RequestAttributes requestAttributes = Mockito.mock(RequestAttributes.class);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        when(requestAttributes.getAttribute("googleEmail", RequestAttributes.SCOPE_REQUEST)).thenReturn(email);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.getUserDetails(email);
        });
    }

    @Test
    public void testGetUserDetailsWithTokenEmailMismatch() {
        String email = "123456@pwr.edu.pl";

        assertThrows(IllegalArgumentException.class, () -> {
            authService.getUserDetails(email);
        });
    }

    @Test
    public void testGetUserDetailsWithEmployeeAndStudentWithTheSameEmail() throws Exception {
        String email = "123456@pwr.edu.pl";

        when(employeeService.getEmployee(anyString())).thenReturn(new Employee());
        when(studentService.getStudent(anyString())).thenReturn(new Student());

        RequestAttributes requestAttributes = Mockito.mock(RequestAttributes.class);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        when(requestAttributes.getAttribute("googleEmail", RequestAttributes.SCOPE_REQUEST)).thenReturn(email);

        assertThrows(EmployeeAndStudentWithTheSameEmailException.class, () -> {
            authService.getUserDetails(email);
        });
    }
}
