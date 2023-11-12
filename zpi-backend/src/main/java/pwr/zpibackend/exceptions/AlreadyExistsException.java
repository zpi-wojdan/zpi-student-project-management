package pwr.zpibackend.exceptions;

public class AlreadyExistsException extends RuntimeException{
    public AlreadyExistsException(String s) {
        super(s);
    }
    public AlreadyExistsException() { }
}
