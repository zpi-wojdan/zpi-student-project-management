package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pwr.zpibackend.dto.university.ProgramDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.Specialization;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.repositories.university.*;
import pwr.zpibackend.exceptions.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final StudyCycleRepository studyCycleRepository;
    private final StudyFieldRepository studyFieldRepository;
    private final SpecializationRepository specializationRepository;
    private final FacultyRepository facultyRepository;

    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    public Program getProgramById(Long id) throws NotFoundException {
        return programRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public Program saveProgram(ProgramDTO program) throws NotFoundException, AlreadyExistsException {
        if (programRepository.findByName(program.getName()).isPresent()) {
            throw new AlreadyExistsException();
        }
        Program newProgram = new Program();
        newProgram.setName(program.getName());
        if (program.getStudyFieldAbbr() != null && !program.getStudyFieldAbbr().equals("")) {
            StudyField studyField = studyFieldRepository.findByAbbreviation(program.getStudyFieldAbbr())
                    .orElseThrow(NotFoundException::new);
            newProgram.setStudyField(studyField);
        } else if (program.getSpecializationAbbr() != null && !program.getSpecializationAbbr().equals("")) {
            Specialization specialization = specializationRepository.findByAbbreviation(program.getSpecializationAbbr())
                    .orElseThrow(NotFoundException::new);
            newProgram.setSpecialization(specialization);
        } else {
            throw new IllegalArgumentException("Program must have either study field or specialization");
        }
        newProgram.setStudyCycles(studyCycleRepository.findAllById(program.getStudyCycleIds()));
        newProgram.setFaculty(facultyRepository.findById(program.getFacultyId()).orElseThrow(NotFoundException::new));
        return programRepository.saveAndFlush(newProgram);
    }

    public Program deleteProgram(Long id) throws NotFoundException {
        Program program = programRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        program.setStudyField(null);
        program.setSpecialization(null);
        program.setStudyCycles(null);
        programRepository.delete(program);
        return program;
    }

    public Program updateProgram(Long id, ProgramDTO updatedProgram) throws NotFoundException {
        Program existingProgram = programRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        existingProgram.setName(updatedProgram.getName());
        if (updatedProgram.getStudyFieldAbbr() != null && !updatedProgram.getStudyFieldAbbr().equals("")) {
            StudyField studyField = studyFieldRepository.findByAbbreviation(updatedProgram.getStudyFieldAbbr())
                    .orElseThrow(NotFoundException::new);
            existingProgram.setStudyField(studyField);
        } else if (updatedProgram.getSpecializationAbbr() != null && !updatedProgram.getSpecializationAbbr().equals("")) {
            Specialization specialization = specializationRepository.findByAbbreviation(updatedProgram.getSpecializationAbbr())
                    .orElseThrow(NotFoundException::new);
            existingProgram.setSpecialization(specialization);
        } else {
            throw new IllegalArgumentException("Program must have either study field or specialization");
        }
        existingProgram.setStudyCycles(studyCycleRepository.findAllById(updatedProgram.getStudyCycleIds()));
        existingProgram.setFaculty(facultyRepository.findById(updatedProgram.getFacultyId()).orElseThrow(NotFoundException::new));
        return programRepository.saveAndFlush(existingProgram);
    }
}
