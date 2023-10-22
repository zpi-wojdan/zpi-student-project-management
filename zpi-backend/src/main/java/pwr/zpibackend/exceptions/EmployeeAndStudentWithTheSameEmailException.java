package pwr.zpibackend.exceptions;

public class EmployeeAndStudentWithTheSameEmailException extends Exception{
    public EmployeeAndStudentWithTheSameEmailException(String email) {
        super("User with email: " + email + " is both employee and student");
    }
}
