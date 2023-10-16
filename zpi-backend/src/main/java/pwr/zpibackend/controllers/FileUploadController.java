package pwr.zpibackend.controllers;


import org.apache.commons.collections4.IterableUtils;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pwr.zpibackend.models.UploadedFile;
import pwr.zpibackend.services.FileUploadService;
import pwr.zpibackend.utils.ResponseMessage;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:8080")
public class FileUploadController {

    @Autowired
    private FileUploadService service;

    @PostMapping("/uploadFile")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file){
        String mess = "";
        try{
            service.storeFile(file);
            mess = "The file was uploaded successfully - " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(mess));
        }
        catch(Exception err){
            mess = "Could not upload the file - " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(mess));
        }
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id){
        try{
            UploadedFile file = service.getFile(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"" )
                    .body(file.getFileData());
        }
        catch(Exception err){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/files")
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
