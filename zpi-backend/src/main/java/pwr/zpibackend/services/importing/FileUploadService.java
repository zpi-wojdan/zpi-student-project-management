package pwr.zpibackend.services.importing;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FileUploadService {

    private final ImportEmployees importEmployees;
    private final ImportStudents importStudents;

    public void processStudentFile(MultipartFile file) throws IOException {
        if (file.isEmpty()){
            throw new IOException("File is empty");
        }
        File tempFile = File.createTempFile("temp", Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(tempFile);
        importStudents.processFile(tempFile.getAbsolutePath());
    }

    public void processEmployeeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()){
            throw new IOException("File is empty");
        }
        File tempFile = File.createTempFile("temp", Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(tempFile);
        importEmployees.processFile(tempFile.getAbsolutePath());
    }

}
