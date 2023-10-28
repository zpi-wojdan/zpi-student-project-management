package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.exceptions.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;

    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    public Program getProgramById(Long id) throws NotFoundException {
        return programRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public Program saveProgram(Program program) {
        return programRepository.save(program);
    }

    public Program deleteProgram(Long id) throws NotFoundException {
        Program program = programRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        programRepository.delete(program);
        return program;
    }

    public Program updateProgram(Long id, Program updatedProgram) throws NotFoundException {
        Program existingProgram = programRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        existingProgram.setName(updatedProgram.getName());
        existingProgram.setStudyField(updatedProgram.getStudyField());
        existingProgram.setSpecialization(updatedProgram.getSpecialization());
        existingProgram.setStudyCycles(updatedProgram.getStudyCycles());
        return programRepository.save(existingProgram);
    }
}
