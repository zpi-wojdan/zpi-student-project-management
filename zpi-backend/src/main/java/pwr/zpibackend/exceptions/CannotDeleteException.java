package pwr.zpibackend.exceptions;

public class CannotDeleteException extends Exception{
    public CannotDeleteException() {}
    public CannotDeleteException(String message) {
        super(message);
    }
}
