package pwr.zpibackend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pwr.zpibackend.services.importing.FileUploadService;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileUploadController {

    private final FileUploadService service;

    @PostMapping("/student")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadStudentFile(@RequestParam("file") MultipartFile file){
        String mess = "";
        try{
            service.processStudentFile(file);
            mess = "The file was uploaded successfully - " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(mess);
        }
        catch(IOException err){
            mess = "Could not upload the file - " + file.getOriginalFilename();
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(mess);
        }
    }

    @PostMapping("/employee")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadEmployeeFile(@RequestParam("file") MultipartFile file){
        String mess = "";
        try{
            service.processEmployeeFile(file);
            mess = "The file was uploaded successfully - " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(mess);
        }
        catch (IOException err){
            mess = "Could not upload the file - " + file.getOriginalFilename();
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(mess);
        }
    }
}
