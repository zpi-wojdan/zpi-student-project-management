package pwr.zpibackend.services.impl.importing;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pwr.zpibackend.services.importing.IFileUploadService;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FileUploadService implements IFileUploadService {

    private final ImportEmployees importEmployees;
    private final ImportStudents importStudents;

    public String processStudentFile(MultipartFile file) throws IOException {
        if (file.isEmpty()){
            throw new IOException("File is empty");
        }
        File tempFile = File.createTempFile("temp", Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(tempFile);
        return importStudents.processFile(tempFile.getAbsolutePath());
    }

    public String processEmployeeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()){
            throw new IOException("File is empty");
        }
        File tempFile = File.createTempFile("temp", Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(tempFile);
        return importEmployees.processFile(tempFile.getAbsolutePath());
    }

}
