package pwr.zpibackend.services.impl.user;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pwr.zpibackend.dto.user.StudentDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.user.Role;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.repositories.university.StudentProgramCycleRepository;
import pwr.zpibackend.repositories.user.StudentRepository;
import pwr.zpibackend.services.impl.user.RoleService;
import pwr.zpibackend.services.impl.user.StudentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTests {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private StudentProgramCycleRepository studentProgramCycleRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    public void testGetAllStudents() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(new Student(), new Student()));

        List<Student> result = studentService.getAllStudents();

        verify(studentRepository, times(1)).findAll();
    }

    @Test
    public void testGetStudentById() {
        Long studentId = 1L;
        Student student = new Student();
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Student result = studentService.getStudent(studentId);

        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    public void testGetStudentById_NotFoundException() {
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studentService.getStudent(studentId));
    }

    @Test
    public void testAddStudent() {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setIndex("123456");
        studentDTO.setProgramsCycles(new ArrayList<>());

        Student student = new Student();
        when(studentRepository.existsByIndex(studentDTO.getIndex())).thenReturn(false);
        when(studentRepository.saveAndFlush(any(Student.class))).thenReturn(student);
        when(roleService.getRoleByName(any())).thenReturn(new Role());


        Student result = studentService.addStudent(studentDTO);

        verify(studentRepository, times(1)).existsByIndex(studentDTO.getIndex());
        verify(studentRepository, times(1)).saveAndFlush(any(Student.class));
    }

    @Test
    public void testAddStudent_AlreadyExistsException() {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setIndex("123456");
        when(studentRepository.existsByIndex(studentDTO.getIndex())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> studentService.addStudent(studentDTO));
    }

    @Test
    public void testUpdateStudent() {
        Long studentId = 1L;
        StudentDTO updatedStudentDTO = new StudentDTO();
        updatedStudentDTO.setIndex("123456");
        updatedStudentDTO.setProgramsCycles(new ArrayList<>());
        Student existingStudent = new Student();
        existingStudent.setId(studentId);
        when(studentRepository.existsByIndex(updatedStudentDTO.getIndex())).thenReturn(true);
        when(studentRepository.findByIndex(updatedStudentDTO.getIndex())).thenReturn(Optional.of(existingStudent));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(existingStudent);
        when(roleService.getRoleByName(any())).thenReturn(new Role());
        when(studentProgramCycleRepository.findByStudentId(any())).thenReturn(new ArrayList<>());


        Student result = studentService.updateStudent(studentId, updatedStudentDTO);

        verify(studentRepository, times(1)).existsByIndex(updatedStudentDTO.getIndex());
        verify(studentRepository, times(1)).findByIndex(updatedStudentDTO.getIndex());
        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testDeleteStudent() {
        Long studentId = 1L;
        Student existingStudent = new Student();
        existingStudent.setId(studentId);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));

        Student result = studentService.deleteStudent(studentId);

        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, times(1)).deleteById(studentId);
    }

    @Test
    public void testDeleteStudent_NotFoundException() {
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studentService.deleteStudent(studentId));
    }
}