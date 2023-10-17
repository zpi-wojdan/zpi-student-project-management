package pwr.zpibackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pwr.zpibackend.models.UploadedFile;
import pwr.zpibackend.repositories.UploadedFileRepository;

import java.io.IOException;
import java.util.Arrays;

@Service
public class FileUploadService {

    @Autowired
    private UploadedFileRepository repository;

    public void storeFile(MultipartFile file) {
        try{
            if (!file.isEmpty()){
                UploadedFile newFile = new UploadedFile();

                newFile.setId(repository.count() + 1);
                newFile.setFileName(file.getOriginalFilename());
                newFile.setFileData(file.getBytes());

                System.out.println(newFile.getId());
                System.out.println(newFile.getFileName());
                System.out.println(Arrays.toString(newFile.getFileData()));

                repository.save(newFile);
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
