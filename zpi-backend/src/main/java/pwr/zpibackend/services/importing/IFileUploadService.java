package pwr.zpibackend.services.importing;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileUploadService {

    String processStudentFile(MultipartFile file) throws IOException;

    String processEmployeeFile(MultipartFile file) throws IOException;
}
