package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.repositories.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudent(String mail) throws NotFoundException
    {
        return studentRepository.findById(mail)
                .orElseThrow(NotFoundException::new);
    }
    
    public boolean exists(String email) {
        return studentRepository.existsById(email);
    }

    public Student addStudent(Student student) throws AlreadyExistsException
    {
        if (studentRepository.existsById(student.getMail())) {
            throw new AlreadyExistsException();
        }
        studentRepository.saveAndFlush(student);
        return student;
    }

    public Student updateStudent(String mail, Student updatedStudent) throws NotFoundException {
        Student student = studentRepository.findById(mail).orElse(null);
        if (student != null) {
            student.setMail(updatedStudent.getMail());
            student.setName(updatedStudent.getName());
            student.setSurname(updatedStudent.getSurname());
            student.setIndex(updatedStudent.getIndex());
            student.setProgram(updatedStudent.getProgram());
            student.setTeaching_cycle(updatedStudent.getTeaching_cycle());
            student.setStatus(updatedStudent.getStatus());
            student.setRole(updatedStudent.getRole());
            student.setAdmission_date(updatedStudent.getAdmission_date());
            student.setStage(updatedStudent.getStage());

            return studentRepository.save(student);
        }
        throw new NotFoundException();
    }

    public Student deleteStudent(String mail) throws NotFoundException
    {
        Optional<Student> studentOptional = studentRepository.findById(mail);

        if (studentOptional.isPresent()) {
            Student deletedStudent = studentOptional.get();
            studentRepository.deleteById(mail);
            return deletedStudent;
        } else {
            throw new NotFoundException();
        }
    }
}