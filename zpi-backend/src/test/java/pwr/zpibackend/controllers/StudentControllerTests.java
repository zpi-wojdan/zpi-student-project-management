package pwr.zpibackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.controllers.user.StudentController;
import pwr.zpibackend.dto.user.StudentDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.user.Role;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.services.user.EmployeeService;
import pwr.zpibackend.services.user.StudentService;

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

    private Student student;
    private StudentDTO studentDTO;
    private List<Student> students;

    @BeforeEach
    public void setUp() {
        Role role = new Role("student");

        student = new Student();
        student.setId(1L);
        student.setMail("123456@student.pwr.edu.pl");
        student.setName("John");
        student.setSurname("Doe");
        student.setIndex("123456");
        student.setRole(role);

        studentDTO = new StudentDTO();
        studentDTO.setName("John");
        studentDTO.setSurname("Doe");
        studentDTO.setIndex("123456");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setMail("456789@student.pwr.edu.pl");
        student2.setName("John");
        student2.setSurname("Doe");
        student2.setIndex("456789");
        student2.setRole(role);

        students = List.of(student, student2);
    }

    @Test
    void getAllStudents() throws Exception {
        Mockito.when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mail").value("123456@student.pwr.edu.pl"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].mail").value("456789@student.pwr.edu.pl"));

        verify(studentService).getAllStudents();
    }

    @Test
    void getStudentById() throws Exception {
        Long id = 1L;

        Mockito.when(studentService.getStudent(id)).thenReturn(student);

        mockMvc.perform(get(BASE_URL + "/{id}", id).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mail").value(student.getMail()));

        verify(studentService).getStudent(id);
    }

    @Test
    void getStudentByIdNotFound() throws Exception {
        Long nonExistingId = 0L;

        Mockito.when(studentService.getStudent(nonExistingId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/{id}", nonExistingId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(studentService).getStudent(nonExistingId);
    }

    @Test
    void addStudent() throws Exception {
        String requestBody = objectMapper.writeValueAsString(studentDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(studentService).addStudent(studentDTO);
    }

    @Test
    void addStudentAlreadyExists() throws Exception {
        Mockito.when(studentService.addStudent(studentDTO)).thenThrow(AlreadyExistsException.class);

        String requestBody = objectMapper.writeValueAsString(studentDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());

        verify(studentService).addStudent(studentDTO);
    }

    @Test
    void updateStudent() throws Exception {
        Long id = 1L;
        studentDTO.setName("Updated");
        student.setName("Updated");

        Mockito.when(studentService.updateStudent(id, studentDTO)).thenReturn(student);

        String requestBody = objectMapper.writeValueAsString(studentDTO);
        String responseBody = objectMapper.writeValueAsString(student);

        mockMvc.perform(put(BASE_URL + "/{id}", id)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(studentService).updateStudent(id, studentDTO);
    }

    @Test
    void updateStudentNotFound() throws Exception {
        Long nonExistingId = 0L;

        Mockito.when(studentService.updateStudent(nonExistingId, studentDTO)).thenThrow(NotFoundException.class);

        String requestBody = objectMapper.writeValueAsString(studentDTO);

        mockMvc.perform(put(BASE_URL + "/{id}", nonExistingId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(studentService).updateStudent(nonExistingId, studentDTO);
    }

    @Test
    void deleteStudent() throws Exception {
        Long id = 1L;

        Mockito.when(studentService.deleteStudent(id)).thenReturn(student);

        mockMvc.perform(delete(BASE_URL + "/{id}", id).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mail").value(student.getMail()));

        verify(studentService).deleteStudent(id);
    }

    @Test
    void deleteStudentNotFound() throws Exception {
        Long nonExistingId = 0L;

        Mockito.when(studentService.deleteStudent(nonExistingId)).thenThrow(NotFoundException.class);

        mockMvc.perform(delete(BASE_URL + "/{id}", nonExistingId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(studentService).deleteStudent(nonExistingId);
    }
}
