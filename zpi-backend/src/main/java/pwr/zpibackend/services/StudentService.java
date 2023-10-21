package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.repositories.StudentRepository;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student getStudent(String email) {
        return studentRepository.findById(email).orElseThrow(
                () -> new NoSuchElementException("Student with email " + email + " does not exist")
        );
    }
}
