package pwr.zpibackend.services.university;

import pwr.zpibackend.dto.university.ProgramDTO;
import pwr.zpibackend.models.university.Program;

import java.util.List;

public interface IProgramService {
    List<Program> getAllPrograms();
    Program getProgramById(Long id);
    Program saveProgram(ProgramDTO program);
    Program deleteProgram(Long id);
    Program updateProgram(Long id, ProgramDTO program);
}
