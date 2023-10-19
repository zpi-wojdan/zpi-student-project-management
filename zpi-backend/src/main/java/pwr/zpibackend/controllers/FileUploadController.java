package pwr.zpibackend.controllers;


import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pwr.zpibackend.models.UploadedFile;
import pwr.zpibackend.services.FileUploadService;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private FileUploadService service;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        String mess = "";
        System.out.println("I'm in file upload controller");
        try{
            service.storeFile(file);
            mess = "The file was uploaded successfully - " + file.getOriginalFilename();
            System.out.println("Should have been saved - controller");
            return ResponseEntity.status(HttpStatus.OK).body(mess);
        }
        catch(Exception err){
            mess = "Could not upload the file - " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(mess);
        }
    }

    @GetMapping("/{id}")
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
