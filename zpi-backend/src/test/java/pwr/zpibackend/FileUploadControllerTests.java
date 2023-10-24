package pwr.zpibackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.controllers.FileUploadController;
import pwr.zpibackend.exceptions.EmptyFileException;
import pwr.zpibackend.models.UploadedFile;
import pwr.zpibackend.services.EmployeeService;
import pwr.zpibackend.services.FileUploadService;
import pwr.zpibackend.services.StudentService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FileUploadControllerTests {

    private static final String BASE_URL = "http://localhost:808/file";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;

    @Test
    public void testUploadFileSuccess() throws Exception {
        mockMvc.perform(multipart(BASE_URL)
                        .file("file", "test".getBytes()))
                .andExpect(status().isOk());

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "File content".getBytes());
        mockMvc.perform(multipart(BASE_URL)
                        .file(file))
                .andExpect(status().isOk());

        MockMultipartFile new_file_type = new MockMultipartFile("file", "test.jpg", "image/jpeg", "Invalid content".getBytes());
        mockMvc.perform(multipart(BASE_URL)
                        .file(new_file_type))
                .andExpect(status().isOk());

    }

    @Test
    public void testUploadFileFailure() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        doThrow(new EmptyFileException("Empty file")).when(fileUploadService).storeFile(emptyFile);

        MvcResult result = mockMvc.perform(multipart(BASE_URL)
                                .file(emptyFile))
                                .andReturn();

        int status = result.getResponse().getStatus();
        assert (status == HttpStatus.EXPECTATION_FAILED.value());
    }

    @Test
    public void testGetFileSuccess() throws Exception {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(1L);
        Mockito.when(fileUploadService.getFile(1L)).thenReturn(uploadedFile);

        MvcResult result = mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UploadedFile responseEntity = objectMapper.readValue(content, UploadedFile.class);

        assert (result.getResponse().getStatus() == HttpStatus.OK.value());
        assert (responseEntity.getId() == 1L);
    }

    @Test
    public void testGetFileFailure() throws Exception {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(1L);
        Mockito.when(fileUploadService.getFile(1L)).thenReturn(uploadedFile);

        MvcResult result = mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UploadedFile responseEntity = objectMapper.readValue(content, UploadedFile.class);

        assert (result.getResponse().getStatus() == HttpStatus.OK.value());
        assert (responseEntity.getId() == 1L);
    }

    @Test
    public void testGetAllFilesSuccess() throws Exception {
        List<UploadedFile> mockFiles = new ArrayList<>();
        UploadedFile file1 = new UploadedFile();
        file1.setId(1L);
        UploadedFile file2 = new UploadedFile();
        file2.setId(2L);
        UploadedFile file3 = new UploadedFile();
        file2.setId(3L);
        UploadedFile file4 = new UploadedFile();
        file2.setId(4L);
        mockFiles.add(file1);
        mockFiles.add(file2);
        mockFiles.add(file3);
        mockFiles.add(file4);

        Mockito.when(fileUploadService.getAllFiles()).thenReturn(mockFiles);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List responseFiles = objectMapper.readValue(content, List.class);

        assert (result.getResponse().getStatus() == HttpStatus.OK.value());
        assert (responseFiles.size() == 4);
    }

    @Test
    public void testGetAllFilesFailure() throws Exception {
        Mockito.when(fileUploadService.getAllFiles()).thenThrow(new RuntimeException("NOT FOUND"));

        MvcResult result = mockMvc.perform(get(BASE_URL))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();

        int status = result.getResponse().getStatus();
        assert (status == HttpStatus.NOT_FOUND.value());
    }


}




