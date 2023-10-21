package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.repositories.StudentRepository;

import java.util.List;

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

    public void deleteStudent(String mail) throws NotFoundException
    {
        if (studentRepository.existsById(mail)) {
            studentRepository.deleteById(mail);
        }
        else throw new NotFoundException();
    }
}
