package pwr.zpibackend.controllers;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.models.UploadedFile;
import pwr.zpibackend.services.EmployeeService;
import pwr.zpibackend.services.FileUploadService;
import pwr.zpibackend.services.StudentService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FileUploadControllerTests {

    private static final String BASE_URL = "/file";
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private FileUploadController fileUploadController;
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

        MockMultipartFile invalidFileType = new MockMultipartFile("file", "test.jpg", "image/jpeg", "Invalid content".getBytes());
        mockMvc.perform(multipart(BASE_URL)
                        .file(invalidFileType))
                .andExpect(status().isOk());

        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        mockMvc.perform(multipart(BASE_URL)
                        .file(emptyFile))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadFileFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "File content".getBytes());
        Mockito.doThrow(new IOException("Error occurred while uploading the file")).when(fileUploadService).storeFile(Mockito.any());

        ResponseEntity<String> response = fileUploadController.uploadFile(file);
        assert response.getStatusCode() == HttpStatus.EXPECTATION_FAILED;
    }

    @Test
    public void testGetFileSuccess() throws Exception {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(1L);
        Mockito.when(fileUploadService.getFile(1L)).thenReturn(uploadedFile);

        MvcResult result = mockMvc.perform(get("/file/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UploadedFile responseEntity = objectMapper.readValue(content, UploadedFile.class);

        // Verify the response status
        assert (result.getResponse().getStatus() == HttpStatus.OK.value());
        // Verify the response body
        assert (responseEntity.getId() == 1L);
    }

    @Test
    public void testGetFileFailure() throws Exception {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(1L);
        Mockito.when(fileUploadService.getFile(1L)).thenReturn(uploadedFile);

        MvcResult result = mockMvc.perform(get("/file/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        UploadedFile responseEntity = objectMapper.readValue(content, UploadedFile.class);

        // Verify the response status
        assert (result.getResponse().getStatus() == HttpStatus.OK.value());
        // Verify the response body
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/file"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List responseFiles = objectMapper.readValue(content, List.class);

        // Verify the response status
        assert (result.getResponse().getStatus() == HttpStatus.OK.value());
        // Verify the number of files in the response
        assert (responseFiles.size() == 4);
    }

//    @Test
//    public void testGetAllFilesFailure() throws Exception {
//        List<UploadedFile> mockFiles = new ArrayList<>();
//        UploadedFile file1 = new UploadedFile();
//        file1.setId(1L);
//        UploadedFile file2 = new UploadedFile();
//        file2.setId(2L);
//        UploadedFile file3 = new UploadedFile();
//        file2.setId(3L);
//        UploadedFile file4 = new UploadedFile();
//        file2.setId(4L);
//        mockFiles.add(file1);
//        mockFiles.add(file2);
//        mockFiles.add(file3);
//        mockFiles.add(file4);
//        Mockito.when(fileUploadService.getAllFiles()).thenReturn(null);
//
//
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/file"))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString();
//        ObjectMapper objectMapper = new ObjectMapper();
//        List responseFiles = objectMapper.readValue(content, List.class);
//
//        // Verify the response status
//        assert (result.getResponse().getStatus() == HttpStatus.OK.value());
//        assert (responseFiles.size() != 4);
//    }
    @Test
    public void testGetAllFilesFailure() throws Exception {
        Mockito.when(fileUploadService.getAllFiles()).thenThrow(new RuntimeException("Simulated error"));

        MvcResult result = mockMvc.perform(get("/file"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value())) // Expect a 404 status due to the exception
                .andReturn();
    }


}




