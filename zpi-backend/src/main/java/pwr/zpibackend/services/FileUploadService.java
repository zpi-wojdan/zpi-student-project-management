package pwr.zpibackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pwr.zpibackend.exceptions.EmptyFileException;
import pwr.zpibackend.models.UploadedFile;
import pwr.zpibackend.repositories.UploadedFileRepository;

import java.io.IOException;
import java.util.Arrays;

@Service
public class FileUploadService {

    @Autowired
    private UploadedFileRepository repository;

    public void storeFile(MultipartFile file) throws EmptyFileException, IOException {
        if (file.isEmpty()){
            throw new EmptyFileException("File is empty");
        }
        else{
            try {
                UploadedFile newFile = new UploadedFile();
//                newFile.setId(repository.count() + 1);
                newFile.setFileName(file.getOriginalFilename());
                newFile.setFileData(file.getBytes());

                repository.save(newFile);
            }
            catch (IOException e){
                throw new IOException("Could not store file " + file.getOriginalFilename());
            }
        }
    }

    public UploadedFile getFile(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Iterable<UploadedFile> getAllFiles(){
        return repository.findAll();
    }


}
