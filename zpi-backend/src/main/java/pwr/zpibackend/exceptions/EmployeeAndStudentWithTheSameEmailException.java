package pwr.zpibackend.exceptions;

public class EmployeeAndStudentWithTheSameEmailException extends RuntimeException{
    public EmployeeAndStudentWithTheSameEmailException(String email) {
        super("User with email: " + email + " is both employee and student");
    }
}
