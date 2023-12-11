package pwr.zpibackend.services.impl.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pwr.zpibackend.dto.university.ProgramDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.*;
import pwr.zpibackend.repositories.university.FacultyRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.SpecializationRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;
import pwr.zpibackend.services.impl.university.ProgramService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProgramServiceTests {

    @MockBean
    private ProgramRepository programRepository;
    @MockBean
    private SpecializationRepository specializationRepository;
    @MockBean
    private StudyCycleRepository studyCycleRepository;
    @MockBean
    private FacultyRepository facultyRepository;
    @Autowired
    private ProgramService programService;

    private Program program;
    private ProgramDTO programDTO;

    @BeforeEach
    public void setup() {
        program = new Program();
        program.setId(1L);
        program.setName("Test Program");

        programDTO = new ProgramDTO();
        programDTO.setName("Test Program");
        programDTO.setSpecializationAbbr("TST");
    }

    @Test
    public void testGetAllPrograms() {
        when(programRepository.findAll()).thenReturn(List.of(program));

        List<Program> result = programService.getAllPrograms();

        assertEquals(1, result.size());
        assertEquals(program, result.get(0));
    }

    @Test
    public void testGetProgramByIdSuccess() throws NotFoundException {
        when(programRepository.findById(program.getId())).thenReturn(Optional.of(program));

        Program result = programService.getProgramById(program.getId());

        assertEquals(program, result);
    }

    @Test
    public void testGetProgramByIdNotFound() {
        when(programRepository.findById(program.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> programService.getProgramById(program.getId()));
    }

    @Test
    public void testSaveProgramSuccess() throws NotFoundException, AlreadyExistsException {
        when(programRepository.saveAndFlush(any())).thenReturn(program);
        when(specializationRepository.findByAbbreviation(any())).thenReturn(Optional.of(new Specialization()));
        when(studyCycleRepository.findAllById(any())).thenReturn(List.of());
        when(facultyRepository.findById(any())).thenReturn(Optional.of(new Faculty()));

        Program result = programService.saveProgram(programDTO);

        assertEquals(program, result);
    }

    @Test
    public void testDeleteProgramSuccess() throws NotFoundException {
        when(programRepository.findById(program.getId())).thenReturn(Optional.of(program));

        Program result = programService.deleteProgram(program.getId());

        assertEquals(program, result);
    }

    @Test
    public void testDeleteProgramNotFound() {
        when(programRepository.findById(program.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> programService.deleteProgram(program.getId()));
    }

    @Test
    public void testUpdateProgramSuccess() throws NotFoundException {
        Program updatedProgram = new Program();
        updatedProgram.setId(program.getId());
        updatedProgram.setName("Updated Test Program");

        ProgramDTO updatedProgramDTO = new ProgramDTO();
        updatedProgramDTO.setName("Updated Test Program");
        updatedProgramDTO.setSpecializationAbbr("TST");

        when(programRepository.findById(program.getId())).thenReturn(Optional.of(program));
        when(specializationRepository.findByAbbreviation(any())).thenReturn(Optional.of(new Specialization()));
        when(programRepository.saveAndFlush(any())).thenReturn(updatedProgram);
        when(specializationRepository.findByAbbreviation(any())).thenReturn(Optional.of(new Specialization()));
        when(studyCycleRepository.findAllById(any())).thenReturn(List.of());
        when(facultyRepository.findById(any())).thenReturn(Optional.of(new Faculty()));

        Program result = programService.updateProgram(1L, updatedProgramDTO);

        assertEquals(updatedProgram, result);
    }

    @Test
    public void testUpdateProgramNotFound() {
        ProgramDTO updatedProgram = new ProgramDTO();
        updatedProgram.setName("Updated Test Program");

        when(programRepository.findById(program.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> programService.updateProgram(program.getId(), updatedProgram));
    }
}

