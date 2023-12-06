package pwr.zpibackend.controllers.importing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.controllers.FileUploadController;
import pwr.zpibackend.services.impl.user.EmployeeService;
import pwr.zpibackend.services.impl.user.StudentService;
import pwr.zpibackend.services.impl.importing.FileUploadService;
import pwr.zpibackend.services.impl.importing.ImportEmployees;
import pwr.zpibackend.services.impl.importing.ImportStudents;

import java.io.IOException;
import java.util.Random;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FileUploadControllerTests {

    private static final String BASE_URL_STUDENT = "/api/file/student";
    private static final String BASE_URL_EMPLOYEE = "/api/file/employee";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;
    @MockBean
    private ImportEmployees importEmployees;
    @MockBean
    private ImportStudents importStudents;

    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;

    @Test
    public void testUploadStudentFileSuccess() throws Exception {
        mockMvc.perform(multipart(BASE_URL_STUDENT)
                        .file("file", "test".getBytes()))
                .andExpect(status().isOk());

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "File content".getBytes());
        mockMvc.perform(multipart(BASE_URL_STUDENT)
                        .file(file))
                .andExpect(status().isOk());

        MockMultipartFile new_file_type = new MockMultipartFile("file", "test.jpg", "image/jpeg", "Invalid content".getBytes());
        mockMvc.perform(multipart(BASE_URL_STUDENT)
                        .file(new_file_type))
                .andExpect(status().isOk());

    }

    @Test
    public void testUploadStudentFileFailureEmpty() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        doThrow(new IOException("Empty file")).when(fileUploadService).processStudentFile(emptyFile);

        MvcResult result = mockMvc.perform(multipart(BASE_URL_STUDENT)
                                .file(emptyFile))
                                .andReturn();

        int status = result.getResponse().getStatus();
        assert (status == HttpStatus.EXPECTATION_FAILED.value());
    }

    @Test
    public void testUploadStudentFileFailureHuge() throws Exception {
        byte[] fileContent = new byte[20 * 1024 * 1024]; // 20MB
        new Random().nextBytes(fileContent);
        MockMultipartFile largeFile = new MockMultipartFile("file", "large-file.txt", "text/plain", fileContent);

        doThrow(MaxUploadSizeExceededException.class).when(fileUploadService).processStudentFile(largeFile);

        MvcResult result = mockMvc.perform(multipart(BASE_URL_STUDENT)
                        .file(largeFile))
                .andReturn();

        int status = result.getResponse().getStatus();
        assert (status == HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testUploadEmployeeFileSuccess() throws Exception {
        mockMvc.perform(multipart(BASE_URL_EMPLOYEE)
                        .file("file", "test".getBytes()))
                .andExpect(status().isOk());

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "File content".getBytes());
        mockMvc.perform(multipart(BASE_URL_EMPLOYEE)
                        .file(file))
                .andExpect(status().isOk());

        MockMultipartFile new_file_type = new MockMultipartFile("file", "test.jpg", "image/jpeg", "Invalid content".getBytes());
        mockMvc.perform(multipart(BASE_URL_EMPLOYEE)
                        .file(new_file_type))
                .andExpect(status().isOk());

    }

    @Test
    public void testUploadEmployeeFileFailureEmpty() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        doThrow(new IOException("Empty file")).when(fileUploadService).processEmployeeFile(emptyFile);

        MvcResult result = mockMvc.perform(multipart(BASE_URL_EMPLOYEE)
                        .file(emptyFile))
                .andReturn();

        int status = result.getResponse().getStatus();
        assert (status == HttpStatus.EXPECTATION_FAILED.value());
    }

    @Test
    public void testUploadEmployeeFileFailureHuge() throws Exception {
        byte[] fileContent = new byte[20 * 1024 * 1024]; // 20MB
        new Random().nextBytes(fileContent);
        MockMultipartFile largeFile = new MockMultipartFile("file", "large-file.txt", "text/plain", fileContent);

        doThrow(MaxUploadSizeExceededException.class).when(fileUploadService).processEmployeeFile(largeFile);

        MvcResult result = mockMvc.perform(multipart(BASE_URL_EMPLOYEE)
                        .file(largeFile))
                .andReturn();

        int status = result.getResponse().getStatus();
        assert (status == HttpStatus.BAD_REQUEST.value());
    }
}
