package pwr.zpibackend.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pwr.zpibackend.repositories.StudentRepository;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StudentServiceTests {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    public void testStudentExists() {
        String email = "123456@pwr.edu.pl";
        when(studentRepository.existsById(email)).thenReturn(true);

        boolean result = studentService.exists(email);

        assertSame(true, result);
    }

    @Test
    public void testStudentDoesNotExist() {
        String email = "123456@pwr.edu.pl";
        when(studentRepository.existsById(email)).thenReturn(false);

        boolean result = studentService.exists(email);

        assertSame(false, result);
    }
}
