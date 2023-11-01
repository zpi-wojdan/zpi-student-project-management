package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pwr.zpibackend.dto.StudentDTO;
import pwr.zpibackend.dto.StudentProgramCycleDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudentProgramCycle;
import pwr.zpibackend.models.university.StudentProgramCycleId;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.repositories.StudentRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.StudentProgramCycleRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public Student getStudent(String mail) throws NotFoundException {
        return studentRepository.findById(mail)
                .orElseThrow(NotFoundException::new);
    }

    public boolean exists(String email) {
        return studentRepository.existsById(email);
    }

    public Student addStudent(StudentDTO student) throws AlreadyExistsException, NotFoundException {
        if (studentRepository.existsById(student.getMail())) {
            throw new AlreadyExistsException();
        }

        Student newStudent = new Student();
        newStudent.setMail(student.getMail());
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

    public Student updateStudent(String mail, StudentDTO updatedStudent) throws NotFoundException {
        Student student = studentRepository.findById(mail).orElseThrow(NotFoundException::new);

        student.setName(updatedStudent.getName());
        student.setSurname(updatedStudent.getSurname());
        student.setIndex(updatedStudent.getIndex());
        student.setStatus(updatedStudent.getStatus());
        student.setRole(roleService.getRoleByName("student"));

        Set<StudentProgramCycle> spcSet = getStudentProgramCycles(updatedStudent, student);
        studentRepository.save(student);

        studentProgramCycleRepository.saveAll(spcSet);

        return student;
    }


    public Student deleteStudent(String mail) throws NotFoundException {
        Optional<Student> studentOptional = studentRepository.findById(mail);

        if (studentOptional.isPresent()) {
            Student deletedStudent = studentOptional.get();
            studentRepository.deleteById(mail);
            return deletedStudent;
        } else {
            throw new NotFoundException();
        }
    }

    private Set<StudentProgramCycle> getStudentProgramCycles(StudentDTO student, Student newStudent) throws NotFoundException {
        Set<StudentProgramCycle> newSpcSet = new HashSet<>();
        for (int i = 0; i < student.getProgramsCycles().size(); i++) {
            StudentProgramCycleDTO spcDTO = student.getProgramsCycles().get(i);

            Program program = programRepository.findById(spcDTO.getProgramId()).orElseThrow(NotFoundException::new);
            StudyCycle cycle = studyCycleRepository.findById(spcDTO.getCycleId()).orElseThrow(NotFoundException::new);

            StudentProgramCycle spc = new StudentProgramCycle();
            spc.setId(new StudentProgramCycleId(student.getMail(), program.getId(), cycle.getId()));
            spc.setStudent(newStudent);
            spc.setProgram(program);
            spc.setCycle(cycle);

            newSpcSet.add(spc);
        }
        return newSpcSet;
    }
}
