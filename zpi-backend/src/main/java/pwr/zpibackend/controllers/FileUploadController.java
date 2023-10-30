package pwr.zpibackend.controllers;


import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pwr.zpibackend.exceptions.EmptyFileException;
import pwr.zpibackend.models.UploadedFile;
import pwr.zpibackend.services.FileUploadService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private FileUploadService service;

    public FileUploadController(FileUploadService fileUploadService) {
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String mess = "";
        try {
            service.storeFile(file);
            mess = "The file was uploaded successfully - " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(mess);
        } catch (EmptyFileException err) {
            mess = "Could not upload the file - " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(mess);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UploadedFile> getFile(@PathVariable Long id){
        try{
            UploadedFile file = service.getFile(id);
            return ResponseEntity.ok().body(file);
        }
        catch(Exception err){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UploadedFile>> getAllFiles(){
        try{
            List<UploadedFile> files = IterableUtils.toList(service.getAllFiles());
            return ResponseEntity.ok().body(files);
        }
        catch(Exception err){
            return ResponseEntity.notFound().build();
        }
    }

}
