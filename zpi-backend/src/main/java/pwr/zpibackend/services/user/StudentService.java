package pwr.zpibackend.services.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pwr.zpibackend.dto.user.StudentDTO;
import pwr.zpibackend.dto.university.StudentProgramCycleDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudentProgramCycle;
import pwr.zpibackend.models.university.StudentProgramCycleId;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.repositories.user.StudentRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.StudentProgramCycleRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;

import java.util.*;

@Service
@AllArgsConstructor
public class StudentService {

    private StudentRepository studentRepository;
    private ProgramRepository programRepository;
    private StudyCycleRepository studyCycleRepository;
    private StudentProgramCycleRepository studentProgramCycleRepository;
    private RoleService roleService;

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Student getStudent(String mail) {
        return studentRepository.findByMail(mail)
                .orElseThrow(NotFoundException::new);
    }

    public boolean exists(String email) {
        return studentRepository.existsByMail(email);
    }

    public Student addStudent(StudentDTO student) {
        if (studentRepository.existsByIndex(student.getIndex())) {
            throw new AlreadyExistsException();
        }

        Student newStudent = new Student();
        newStudent.setMail(student.getIndex() + "@student.pwr.edu.pl");
        newStudent.setName(student.getName());
        newStudent.setSurname(student.getSurname());
        newStudent.setIndex(student.getIndex());
        newStudent.setStatus(student.getStatus());
        newStudent.setRole(roleService.getRoleByName("student"));

        Set<StudentProgramCycle> spcSet = getStudentProgramCycles(student, newStudent);
        studentRepository.saveAndFlush(newStudent);

        studentProgramCycleRepository.saveAll(spcSet);
        return newStudent;
    }

    public Student updateStudent(Long id, StudentDTO updatedStudent) {
        if (studentRepository.existsByIndex(updatedStudent.getIndex())) {
            if (!(Objects.equals(studentRepository.findByIndex(updatedStudent.getIndex()).get().getId(), id)))
                throw new AlreadyExistsException();
        }
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);

        student.setMail(updatedStudent.getIndex() + "@student.pwr.edu.pl");
        student.setName(updatedStudent.getName());
        student.setSurname(updatedStudent.getSurname());
        student.setIndex(updatedStudent.getIndex());
        student.setStatus(updatedStudent.getStatus());
        student.setRole(roleService.getRoleByName("student"));

        Set<StudentProgramCycle> spcSet = getStudentProgramCycles(updatedStudent, student);

        List<StudentProgramCycle> existingSpcList = studentProgramCycleRepository.findByStudentId(student.getId());

        for (StudentProgramCycle existingSpc : existingSpcList) {
            if (!spcSet.contains(existingSpc)) {
                studentProgramCycleRepository.delete(existingSpc);
            }
        }

        studentRepository.save(student);
        studentProgramCycleRepository.saveAll(spcSet);

        return student;
    }


    public Student deleteStudent(Long id) {
        Optional<Student> studentOptional = studentRepository.findById(id);

        if (studentOptional.isPresent()) {
            Student deletedStudent = studentOptional.get();
            studentRepository.deleteById(id);
            return deletedStudent;
        } else {
            throw new NotFoundException();
        }
    }

    private Set<StudentProgramCycle> getStudentProgramCycles(StudentDTO studentDTO, Student newStudent) {
        Set<StudentProgramCycle> newSpcSet = new HashSet<>();
        for (int i = 0; i < studentDTO.getProgramsCycles().size(); i++) {
            StudentProgramCycleDTO spcDTO = studentDTO.getProgramsCycles().get(i);

            Program program = programRepository.findById(spcDTO.getProgramId()).orElseThrow(NotFoundException::new);
            StudyCycle cycle = studyCycleRepository.findById(spcDTO.getCycleId()).orElseThrow(NotFoundException::new);

            StudentProgramCycle spc = new StudentProgramCycle();
            spc.setId(new StudentProgramCycleId(newStudent.getId(), program.getId(), cycle.getId()));
            spc.setStudent(newStudent);
            spc.setProgram(program);
            spc.setCycle(cycle);

            newSpcSet.add(spc);
        }
        return newSpcSet;
    }
}
