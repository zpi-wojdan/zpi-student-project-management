package pwr.zpibackend.services.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.repositories.university.StudyCycleRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StudyCycleServiceTest {

    @MockBean
    private StudyCycleRepository studyCycleRepository;

    @Autowired
    private StudyCycleService studyCycleService;

    private StudyCycle studyCycle;

    @BeforeEach
    public void setup() {
        studyCycle = new StudyCycle();
        studyCycle.setId(1L);
        studyCycle.setName("Test Study Cycle");
    }

    @Test
    public void testGetAllStudyCycles() {
        when(studyCycleRepository.findAll()).thenReturn(List.of(studyCycle));

        List<StudyCycle> result = studyCycleService.getAllStudyCycles();

        assertEquals(1, result.size());
        assertEquals(studyCycle, result.get(0));
    }

    @Test
    public void testGetStudyCycleByIdSuccess() throws NotFoundException {
        when(studyCycleRepository.findById(studyCycle.getId())).thenReturn(Optional.of(studyCycle));

        StudyCycle result = studyCycleService.getStudyCycleById(studyCycle.getId());

        assertEquals(studyCycle, result);
    }

    @Test
    public void testGetStudyCycleByIdNotFound() {
        when(studyCycleRepository.findById(studyCycle.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studyCycleService.getStudyCycleById(studyCycle.getId()));
    }

    @Test
    public void testSaveStudyCycleSuccess() {
        when(studyCycleRepository.save(any())).thenReturn(studyCycle);

        StudyCycle result = studyCycleService.saveStudyCycle(studyCycle);

        assertEquals(studyCycle, result);
    }

    @Test
    public void testDeleteStudyCycleSuccess() throws NotFoundException {
        when(studyCycleRepository.findById(studyCycle.getId())).thenReturn(Optional.of(studyCycle));

        StudyCycle result = studyCycleService.deleteStudyCycle(studyCycle.getId());

        assertEquals(studyCycle, result);
    }

    @Test
    public void testDeleteStudyCycleNotFound() {
        when(studyCycleRepository.findById(studyCycle.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studyCycleService.deleteStudyCycle(studyCycle.getId()));
    }

    @Test
    public void testUpdateStudyCycleSuccess() throws NotFoundException {
        StudyCycle updatedStudyCycle = new StudyCycle();
        updatedStudyCycle.setId(studyCycle.getId());
        updatedStudyCycle.setName("Updated Test Study Cycle");

        when(studyCycleRepository.findById(studyCycle.getId())).thenReturn(Optional.of(studyCycle));
        when(studyCycleRepository.save(any())).thenReturn(updatedStudyCycle);

        StudyCycle result = studyCycleService.updateStudyCycle(studyCycle.getId(), updatedStudyCycle);

        assertEquals(updatedStudyCycle, result);
    }

    @Test
    public void testUpdateStudyCycleNotFound() {
        StudyCycle updatedStudyCycle = new StudyCycle();
        updatedStudyCycle.setId(studyCycle.getId());
        updatedStudyCycle.setName("Updated Test Study Cycle");

        when(studyCycleRepository.findById(studyCycle.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studyCycleService.updateStudyCycle(studyCycle.getId(), updatedStudyCycle));
    }
}
