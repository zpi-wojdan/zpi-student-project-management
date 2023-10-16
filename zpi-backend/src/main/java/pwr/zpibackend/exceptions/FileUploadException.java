package pwr.zpibackend.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import pwr.zpibackend.utils.ResponseMessage;

import java.util.Arrays;

@ControllerAdvice
public class FileUploadException {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseMessage> handleMaxSizeExceededException(MaxUploadSizeExceededException exception){
        return ResponseEntity.status(400).body(new ResponseMessage("File was too lqrge\n" + Arrays.toString(exception.getStackTrace())));
    }

}
