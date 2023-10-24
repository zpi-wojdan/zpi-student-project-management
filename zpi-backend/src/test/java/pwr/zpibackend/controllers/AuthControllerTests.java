package pwr.zpibackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.exceptions.EmployeeAndStudentWithTheSameEmailException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.services.AuthService;
import pwr.zpibackend.services.EmployeeService;
import pwr.zpibackend.services.StudentService;

import java.util.Date;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTests {

    private static final String BASE_URL = "/user";
    @MockBean
    private GoogleAuthService googleAuthService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private EmployeeService employeeService;

    @Test
    void getUserDetailsOfStudent() throws Exception {
        String studentMail = "123456@student.pwr.edu.pl";
        Student student = new Student(studentMail, "John", "Doe", "123456", "Program 1",
                "Cycle 1", "Active", "Role 1", new Date(), "Stage 1");

        Mockito.when(authService.getUserDetails(studentMail)).thenReturn(student);

        mockMvc.perform(get(BASE_URL + "/" + studentMail + "/details").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(student)));

        verify(authService).getUserDetails(studentMail);
    }

    @Test
    void getUserDetailsOfEmployee() throws Exception {
        String employeeMail = "john.doe@pwr.edu.pl";
        Employee employee = new Employee(employeeMail, "John", "Doe", "Role 1",
                "Department 1", "Title 1");

        Mockito.when(authService.getUserDetails(employeeMail)).thenReturn(employee);

        mockMvc.perform(get(BASE_URL + "/" + employeeMail + "/details").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(employee)));

        verify(authService).getUserDetails(employeeMail);
    }

    @Test
    void getUserDetailsOfEmployeeAndStudentWithTheSameEmail() throws Exception {
        String email = "123456@pwr.edu.pl";
        Mockito.when(authService.getUserDetails(email)).thenThrow(EmployeeAndStudentWithTheSameEmailException.class);

        mockMvc.perform(get(BASE_URL + "/" + email + "/details").contentType("application/json"))
                .andExpect(status().isConflict());

        verify(authService).getUserDetails(email);
    }

    @Test
    void getUserDetailsOfNonExistingUser() throws Exception {
        String email = "123456@pwr.edu.pl";
        Mockito.when(authService.getUserDetails(email)).thenThrow(NoSuchElementException.class);

        mockMvc.perform(get(BASE_URL + "/" + email + "/details").contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(authService).getUserDetails(email);
    }

    @Test
    void getUserDetailsOfUserWithNonPwrEmail() throws Exception {
        String email = "123456@gmail.com";
        Mockito.when(authService.getUserDetails(email)).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(get(BASE_URL + "/" + email + "/details").contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(authService).getUserDetails(email);
    }

    @Test
    void getUserDetailsOfUserWithNonMatchingEmail() throws Exception {
        String email = "123456@pwr.edu.pl";
        Mockito.when(authService.getUserDetails(email)).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(get(BASE_URL + "/" + email + "/details").contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(authService).getUserDetails(email);
    }
}



