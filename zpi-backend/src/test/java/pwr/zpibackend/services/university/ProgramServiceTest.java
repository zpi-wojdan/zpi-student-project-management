package pwr.zpibackend.services.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.repositories.university.ProgramRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProgramServiceTest {

    @MockBean
    private ProgramRepository programRepository;

    @Autowired
    private ProgramService programService;

    private Program program;

    @BeforeEach
    public void setup() {
        program = new Program();
        program.setId(1L);
        program.setName("Test Program");
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
    public void testSaveProgramSuccess() {
        when(programRepository.save(any())).thenReturn(program);

        Program result = programService.saveProgram(program);

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

        when(programRepository.findById(program.getId())).thenReturn(Optional.of(program));
        when(programRepository.save(any())).thenReturn(updatedProgram);

        Program result = programService.updateProgram(program.getId(), updatedProgram);

        assertEquals(updatedProgram, result);
    }

    @Test
    public void testUpdateProgramNotFound() {
        Program updatedProgram = new Program();
        updatedProgram.setId(program.getId());
        updatedProgram.setName("Updated Test Program");

        when(programRepository.findById(program.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> programService.updateProgram(program.getId(), updatedProgram));
    }
}

