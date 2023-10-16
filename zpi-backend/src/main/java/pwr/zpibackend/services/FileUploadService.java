package pwr.zpibackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pwr.zpibackend.models.UploadedFile;
import pwr.zpibackend.repositories.UploadedFileRepository;

import java.io.IOException;

@Service
public class FileUploadService {

    @Autowired
    private UploadedFileRepository repository;

    public void storeFile(MultipartFile file) {
        try{
            if (!file.isEmpty()){
                System.out.println("I'm here");
                UploadedFile newFile = new UploadedFile();
                newFile.setFileName(file.getOriginalFilename());
                newFile.setFileData(file.getBytes());
                repository.save(newFile);
                System.out.println("Should have been saved");
            }
        }
        catch(IOException err) {
            System.out.println("Error occured while uploading the file\n");
            err.printStackTrace();
        }
    }

    public UploadedFile getFile(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Iterable<UploadedFile> getAllFiles() {
        return repository.findAll();
    }


}
