package pwr.zpibackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pwr.zpibackend.services.importing.IFileUploadService;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileUploadController {

    private final IFileUploadService service;

    @PostMapping("/student")
    @Operation(summary = "Upload student file",
            description = "Uploads student file to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadStudentFile(@RequestParam("file") MultipartFile file){
        String mess = "";
        try{
            String invalidData = service.processStudentFile(file);

            mess = "The file was uploaded successfully - " + file.getOriginalFilename();
            ObjectNode responseJson = new ObjectMapper().createObjectNode();
            responseJson.put("message", mess);
            responseJson.put("invalidData", invalidData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(responseJson.toString(), headers, HttpStatus.OK);
        }
        catch(IOException err){
            mess = "Could not upload the file - " + file.getOriginalFilename();
            err.printStackTrace();
            return new ResponseEntity<>(mess, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/employee")
    @Operation(summary = "Upload employee file",
            description = "Uploads employee file to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadEmployeeFile(@RequestParam("file") MultipartFile file){
        String mess = "";
        try{
            String invalidData = service.processEmployeeFile(file);

            mess = "The file was uploaded successfully - " + file.getOriginalFilename();
            ObjectNode responseJson = new ObjectMapper().createObjectNode();
            responseJson.put("message", mess);
            responseJson.put("invalidData", invalidData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(responseJson.toString(), headers, HttpStatus.OK);
        }
        catch (IOException err){
            mess = "Could not upload the file - " + file.getOriginalFilename();
            err.printStackTrace();
            return new ResponseEntity<>(mess, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
