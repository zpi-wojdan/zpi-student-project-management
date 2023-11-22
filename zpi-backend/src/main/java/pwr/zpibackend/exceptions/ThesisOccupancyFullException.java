package pwr.zpibackend.exceptions;

public class ThesisOccupancyFullException extends RuntimeException {
    public ThesisOccupancyFullException() {
        super("Thesis occupancy is full");
    }
}
