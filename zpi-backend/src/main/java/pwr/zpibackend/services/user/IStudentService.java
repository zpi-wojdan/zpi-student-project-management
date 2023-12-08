package pwr.zpibackend.services.user;

import pwr.zpibackend.dto.user.StudentDTO;
import pwr.zpibackend.models.user.Student;

import java.util.List;

public interface IStudentService {
    List<Student> getAllStudents();
    Student getStudent(Long id);
    Student getStudent(String mail);
    boolean exists(String email);
    Student addStudent(StudentDTO student);
    Student updateStudent(Long id, StudentDTO student);
    Student deleteStudent(Long id);
    List<Student> deleteStudentsInBulk(Long cycleId, List<Long> studentsIds);
}
