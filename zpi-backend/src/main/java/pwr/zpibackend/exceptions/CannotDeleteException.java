package pwr.zpibackend.exceptions;

public class CannotDeleteException extends RuntimeException{
    public CannotDeleteException() {}
    public CannotDeleteException(String message) {
        super(message);
    }
}
