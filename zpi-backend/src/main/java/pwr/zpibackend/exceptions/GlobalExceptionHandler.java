package pwr.zpibackend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        ErrorDetails errorDetails = getErrorDetails(e, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmployeeAndStudentWithTheSameEmailException.class)
    public ResponseEntity<ErrorDetails> handleEmployeeAndStudentWithTheSameEmailException(
            EmployeeAndStudentWithTheSameEmailException e, WebRequest request) {
        ErrorDetails errorDetails = getErrorDetails(e, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    private ErrorDetails getErrorDetails(Exception e, WebRequest request) {
        return new ErrorDetails(LocalDateTime.now(), e.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(SQLException e, WebRequest request) {
        ErrorDetails errorDetails = getErrorDetails(e, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFoundException(NotFoundException e, WebRequest request) {
        ErrorDetails errorDetails = getErrorDetails(e, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CannotDeleteException.class)
    public ResponseEntity<ErrorDetails> handleCannotDeleteException(CannotDeleteException e, WebRequest request) {
        ErrorDetails errorDetails = getErrorDetails(e, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleAlreadyExistsException(AlreadyExistsException e, WebRequest request) {
        ErrorDetails errorDetails = getErrorDetails(e, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeExceededException(MaxUploadSizeExceededException exception){
        return ResponseEntity.status(400).body("File was too large\n" + Arrays.toString(exception.getStackTrace()));
    }
}

