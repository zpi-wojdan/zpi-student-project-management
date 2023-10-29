package pwr.zpibackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.controllers.ThesisController;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Role;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.services.EmployeeService;
import pwr.zpibackend.services.StudentService;
import pwr.zpibackend.services.ThesisService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTests {
    private static final String BASE_URL = "/student";
    @MockBean
    private GoogleAuthService googleAuthService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private StudentService studentService;
    @MockBean
    private EmployeeService employeeService;

    @Test
    void getAllStudents() throws Exception {
        List<Student> students = List.of(
                new Student("123456@student.pwr.edu.pl", "John", "Doe", "123456", "Program 1", "Cycle 1", "Active", new Role("Role 1"), null, "Stage 1"),
                new Student("456789@student.pwr.edu.pl", "Jane", "Smith", "456789", "Program 2", "Cycle 2", "Inactive", new Role("Role 2"), null, "Stage 2")
        );

        Mockito.when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mail").value("123456@student.pwr.edu.pl"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].mail").value("456789@student.pwr.edu.pl"));

        verify(studentService).getAllStudents();
    }

    @Test
    void getStudentById() throws Exception {
        String studentMail = "123456@student.pwr.edu.pl";
        Student student = new Student(studentMail, "John", "Doe", "123456", "Program 1", "Cycle 1", "Active", new Role("Role 1"), new Date(), "Stage 1");

        Mockito.when(studentService.getStudent(studentMail)).thenReturn(student);

        mockMvc.perform(get(BASE_URL + "/{mail}", studentMail).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mail").value(studentMail));

        verify(studentService).getStudent(studentMail);
    }

    @Test
    void getStudentByIdNotFound() throws Exception {
        String nonExistingMail = "000000@student.pwr.edu.pl";

        Mockito.when(studentService.getStudent(nonExistingMail)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/{mail}", nonExistingMail).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(studentService).getStudent(nonExistingMail);
    }

    @Test
    void addStudent() throws Exception {
        Student newStudent = new Student("123456@student.pwr.edu.pl", "Alice", "Johnson", "123456", "Program 2", "Cycle 2", "Active", new Role("Role 2"), null, "Stage 2");

        String requestBody = objectMapper.writeValueAsString(newStudent);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(studentService).addStudent(newStudent);
    }

    @Test
    void addStudentAlreadyExists() throws Exception {
        Student existingStudent = new Student("123456@student.pwr.edu.pl", "Bob", "Smith", "123456", "Program 1", "Cycle 1", "Active", new Role("Role 1"), null, "Stage 1");

        Mockito.when(studentService.addStudent(existingStudent)).thenThrow(AlreadyExistsException.class);

        String requestBody = objectMapper.writeValueAsString(existingStudent);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(studentService).addStudent(existingStudent);
    }

    @Test
    void updateStudent() throws Exception {
        String studentMail = "123456@student.pwr.edu.pl";
        Student updatedStudent = new Student(studentMail, "Updated", "Name", "123456", "Program 2", "Cycle 2", "Active", new Role("Role 2"), null, "Stage 2");

        Mockito.when(studentService.updateStudent(studentMail, updatedStudent)).thenReturn(updatedStudent);

        String requestBody = objectMapper.writeValueAsString(updatedStudent);

        mockMvc.perform(put(BASE_URL + "/{mail}", studentMail)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(requestBody));

        verify(studentService).updateStudent(studentMail, updatedStudent);
    }

    @Test
    void updateStudentNotFound() throws Exception {
        String nonExistingMail = "000000@student.pwr.edu.pl";
        Student updatedStudent = new Student(nonExistingMail, "Updated", "Name", "000000", "Program 2", "Cycle 2", "Active", new Role("Role 2"), null, "Stage 2");

        Mockito.when(studentService.updateStudent(nonExistingMail, updatedStudent)).thenThrow(NotFoundException.class);

        String requestBody = objectMapper.writeValueAsString(updatedStudent);

        mockMvc.perform(put(BASE_URL + "/{mail}", nonExistingMail)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(studentService).updateStudent(nonExistingMail, updatedStudent);
    }

    @Test
    void deleteStudent() throws Exception {
        String studentMail = "123456@student.pwr.edu.pl";
        Student deletedStudent = new Student(studentMail, "John", "Doe", "123456", "Program 1", "Cycle 1", "Active", new Role("Role 1"), null, "Stage 1");

        Mockito.when(studentService.deleteStudent(studentMail)).thenReturn(deletedStudent);

        mockMvc.perform(delete(BASE_URL + "/{mail}", studentMail).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mail").value(studentMail));

        verify(studentService).deleteStudent(studentMail);
    }

    @Test
    void deleteStudentNotFound() throws Exception {
        String nonExistingMail = "000000@student.pwr.edu.pl";

        Mockito.when(studentService.deleteStudent(nonExistingMail)).thenThrow(NotFoundException.class);

        mockMvc.perform(delete(BASE_URL + "/{mail}", nonExistingMail).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(studentService).deleteStudent(nonExistingMail);
    }
}
