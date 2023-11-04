package pwr.zpibackend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorDetails> handleNoSuchElementException(NoSuchElementException e, WebRequest request) {
        ErrorDetails errorDetails = getErrorDetails(e, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity<>("A data integrity violation occurred.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorDetails> handleNotFoundException(NotFoundException e, WebRequest request) {
        ErrorDetails errorDetails = getErrorDetails(e, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
