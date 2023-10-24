package pwr.zpibackend.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pwr.zpibackend.repositories.EmployeeRepository;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    public void testEmployeeExists() {
        String email = "123456@pwr.edu.pl";
        when(employeeRepository.existsById(email)).thenReturn(true);

        boolean result = employeeService.exists(email);

        assertSame(true, result);
    }

    @Test
    public void testEmployeeDoesNotExist() {
        String email = "123456@pwr.edu.pl";
        when(employeeRepository.existsById(email)).thenReturn(false);

        boolean result = employeeService.exists(email);

        assertSame(false, result);
    }
}
