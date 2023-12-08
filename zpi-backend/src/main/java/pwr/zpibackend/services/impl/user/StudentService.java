package pwr.zpibackend.services.impl.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pwr.zpibackend.dto.user.StudentDTO;
import pwr.zpibackend.dto.university.StudentProgramCycleDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudentProgramCycle;
import pwr.zpibackend.models.university.StudentProgramCycleId;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.repositories.thesis.ReservationRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.user.StudentRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.StudentProgramCycleRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;
import pwr.zpibackend.services.user.IStudentService;

import java.util.*;

@Service
@AllArgsConstructor
public class StudentService implements IStudentService {

    private StudentRepository studentRepository;
    private ProgramRepository programRepository;
    private StudyCycleRepository studyCycleRepository;
    private StudentProgramCycleRepository studentProgramCycleRepository;
    private ThesisRepository thesisRepository;
    private ReservationRepository reservationRepository;
    private RoleService roleService;

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student with id " + id + " does not exist"));
    }

    @Transactional(readOnly = true)
    public Student getStudent(String mail) {
        return studentRepository.findByMail(mail)
                .orElseThrow(() -> new NotFoundException("Student with mail " + mail + " does not exist"));
    }

    public boolean exists(String email) {
        return studentRepository.existsByMail(email);
    }

    public Student addStudent(StudentDTO student) {
        if (studentRepository.existsByIndex(student.getIndex())) {
            throw new AlreadyExistsException("Student with index " + student.getIndex() + " already exists");
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
                throw new AlreadyExistsException("Student with index " + updatedStudent.getIndex() + " already exists");
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
            throw new NotFoundException("Student with id " + id + " does not exist");
        }
    }

    private Set<StudentProgramCycle> getStudentProgramCycles(StudentDTO studentDTO, Student newStudent) {
        Set<StudentProgramCycle> newSpcSet = new HashSet<>();
        for (int i = 0; i < studentDTO.getProgramsCycles().size(); i++) {
            StudentProgramCycleDTO spcDTO = studentDTO.getProgramsCycles().get(i);

            Program program = programRepository.findById(spcDTO.getProgramId()).orElseThrow(
                    () -> new NotFoundException("Program with id " + spcDTO.getProgramId() + " does not exist")
            );
            StudyCycle cycle = studyCycleRepository.findById(spcDTO.getCycleId()).orElseThrow(
                    () -> new NotFoundException("Study cycle with id " + spcDTO.getCycleId() + " does not exist")
            );

            StudentProgramCycle spc = new StudentProgramCycle();
            spc.setId(new StudentProgramCycleId(newStudent.getId(), program.getId(), cycle.getId()));
            spc.setStudent(newStudent);
            spc.setProgram(program);
            spc.setCycle(cycle);

            newSpcSet.add(spc);
        }
        return newSpcSet;
    }

    public List<Student> deleteStudentsInBulk(Long cycleId, List<Long> studentsIds) {
        List<Student> studentsByCycle = studentRepository.findByStudentProgramCycles_Cycle_Id(cycleId);
        List<Student> studentsToDelete = studentsByCycle.stream()
                .filter(student -> studentsIds.contains(student.getId()))
                .toList();
        List<Student> deletedStudents = new ArrayList<>();

        studentsToDelete.forEach(stud -> System.out.println(stud.getId()));

        for (Student stud : studentsToDelete) {
            Set<StudentProgramCycle> studentProgramCycles = stud.getStudentProgramCycles();
            List<StudentProgramCycle> spcsToDeleteLater = new ArrayList<>();

            //  przygotowanie do wywalenia studentProgramCycles
            for (StudentProgramCycle spc : studentProgramCycles) {
                if (spc.getCycle().getId().equals(cycleId)) {
                    spcsToDeleteLater.add(spc);
                }
            }

            //  usuwam w ten sposób, bo jak było w pętli to: ConcurrentModificationException
            spcsToDeleteLater.forEach(studentProgramCycles::remove);
            studentProgramCycleRepository.deleteAll(spcsToDeleteLater);
            studentProgramCycleRepository.flush();

            //  odpinam rezerwacje
            List<Reservation> reservations = reservationRepository.findAllByStudent_Id(stud.getId());
            for (Reservation r : reservations){
                if (r.getThesis().getStudyCycle().getId().equals(cycleId)){
                    reservationRepository.delete(r);
                }
            }
            reservationRepository.flush();

            //  odpinam tematy
            List<Thesis> theses = thesisRepository.findAllByLeader_Id(stud.getId());
            for (Thesis t : theses){
                if (t.getStudyCycle().getId().equals(cycleId)){
                    t.setLeader(null);
//                    thesisRepository.delete(t);
                    // nie usuwam tylko aktualizuję, żeby temat został
                    thesisRepository.save(t);   }
            }
            thesisRepository.flush();

            if (studentProgramCycles.isEmpty()) {
                studentRepository.delete(stud);
                studentRepository.flush();
                deletedStudents.add(stud);
            } else {
                studentRepository.save(stud);
            }
        }

        return deletedStudents;
    }

}
