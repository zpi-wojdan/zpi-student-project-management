package pwr.zpibackend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pwr.zpibackend.services.importing.FileUploadService;

import java.io.IOException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileUploadController {

    private final FileUploadService service;

    @PostMapping("/student")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadStudentFile(@RequestParam("file") MultipartFile file){
        String mess = "";
        try{
            String invalidData = service.processStudentFile(file);

            mess = "The file was uploaded successfully - " + file.getOriginalFilename();
            ObjectNode responseJson = new ObjectMapper().createObjectNode();
            responseJson.put("message", mess);
            responseJson.put("invalidData", invalidData);

            return ResponseEntity.status(HttpStatus.OK).body(responseJson.toString());
        }
        catch(IOException err){
            mess = "Could not upload the file - " + file.getOriginalFilename();
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(mess);
        }
    }

    @PostMapping("/employee")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadEmployeeFile(@RequestParam("file") MultipartFile file){
        String mess = "";
        try{
            String invalidData = service.processEmployeeFile(file);

            mess = "The file was uploaded successfully - " + file.getOriginalFilename();
            ObjectNode responseJson = new ObjectMapper().createObjectNode();
            responseJson.put("message", mess);
            responseJson.put("invalidData", invalidData);

            return ResponseEntity.status(HttpStatus.OK).body(responseJson.toString());
        }
        catch (IOException err){
            mess = "Could not upload the file - " + file.getOriginalFilename();
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(mess);
        }
    }
}
