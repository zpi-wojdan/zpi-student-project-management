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
import java.util.Objects;

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

    public Program getProgramById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Program with id " + id + " does not exist"));
    }

    public Program saveProgram(ProgramDTO program) {
        if (programRepository.findByName(program.getName()).isPresent()) {
            throw new AlreadyExistsException("Program with name " + program.getName() + " already exists");
        }
        Program newProgram = new Program();
        newProgram.setName(program.getName());
        if (program.getSpecializationAbbr() != null && !program.getSpecializationAbbr().equals("")) {
            Specialization specialization = specializationRepository.findByAbbreviation(program.getSpecializationAbbr())
                    .orElseThrow(() -> new NotFoundException("Specialization with abbreviation " + program.getSpecializationAbbr() + " does not exist"));
            newProgram.setSpecialization(specialization);
        } else {
            if (program.getStudyFieldAbbr() != null && !program.getStudyFieldAbbr().equals("")) {
                StudyField studyField = studyFieldRepository.findByAbbreviation(program.getStudyFieldAbbr())
                        .orElseThrow(() -> new NotFoundException("Study field with abbreviation " + program.getStudyFieldAbbr() + " does not exist"));
                newProgram.setStudyField(studyField);
            } else {
                throw new IllegalArgumentException("Program must have either study field or specialization");
            }
        }
        newProgram.setStudyCycles(studyCycleRepository.findAllById(program.getStudyCycleIds()));
        newProgram.setFaculty(facultyRepository.findById(program.getFacultyId()).orElseThrow(
                () -> new NotFoundException("Faculty with id " + program.getFacultyId() + " does not exist")
        ));
        return programRepository.saveAndFlush(newProgram);
    }

    public Program deleteProgram(Long id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Program with id " + id + " does not exist"));
        program.setStudyField(null);
        program.setSpecialization(null);
        program.setStudyCycles(null);
        programRepository.delete(program);
        return program;
    }

    public Program updateProgram(Long id, ProgramDTO updatedProgram) {
        if (programRepository.findByName(updatedProgram.getName()).isPresent()) {
            if (!(Objects.equals(programRepository.findByName(updatedProgram.getName()).get().getId(), id))) {
                throw new AlreadyExistsException("Program with name " + updatedProgram.getName() + " already exists");
            }
        }
        Program existingProgram = programRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Program with id " + id + " does not exist"));
        existingProgram.setName(updatedProgram.getName());
        if (updatedProgram.getSpecializationAbbr() != null && !updatedProgram.getSpecializationAbbr().equals("")) {
            Specialization specialization = specializationRepository.findByAbbreviation(updatedProgram.getSpecializationAbbr())
                    .orElseThrow(() -> new NotFoundException("Specialization with abbreviation " + updatedProgram.getSpecializationAbbr() + " does not exist"));
            existingProgram.setSpecialization(specialization);
        } else {
            if (updatedProgram.getStudyFieldAbbr() != null && !updatedProgram.getStudyFieldAbbr().equals("")) {
                StudyField studyField = studyFieldRepository.findByAbbreviation(updatedProgram.getStudyFieldAbbr())
                        .orElseThrow(() -> new NotFoundException("Study field with abbreviation " + updatedProgram.getStudyFieldAbbr() + " does not exist"));
                existingProgram.setStudyField(studyField);
            } else {
                throw new IllegalArgumentException("Program must have either study field or specialization");
            }
        }
        existingProgram.setStudyCycles(studyCycleRepository.findAllById(updatedProgram.getStudyCycleIds()));
        existingProgram.setFaculty(facultyRepository.findById(updatedProgram.getFacultyId()).orElseThrow(
                () -> new NotFoundException("Faculty with id " + updatedProgram.getFacultyId() + " does not exist")
        ));
        return programRepository.saveAndFlush(existingProgram);
    }
}
