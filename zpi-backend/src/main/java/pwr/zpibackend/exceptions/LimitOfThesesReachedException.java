package pwr.zpibackend.exceptions;

public class LimitOfThesesReachedException extends RuntimeException{
    public LimitOfThesesReachedException(String s) {
        super(s);
    }
    public LimitOfThesesReachedException() { }
}
