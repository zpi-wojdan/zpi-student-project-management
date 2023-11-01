package pwr.zpibackend.services;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import pwr.zpibackend.utils.ImportEmployees;
import pwr.zpibackend.utils.ImportStudents;

@Service
public class FileUploadService {

    public void processStudentFile(MultipartFile file) throws IOException {
        if (file.isEmpty()){
            throw new IOException("File is empty");
        }
        File tempFile = File.createTempFile("temp", Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(tempFile);

        ImportStudents.processFile(tempFile.getAbsolutePath());
    }

    public void processEmployeeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()){
            throw new IOException("File is empty");
        }
        File tempFile = File.createTempFile("temp", Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(tempFile);

        ImportEmployees.processFile(tempFile.getAbsolutePath());
    }

}
