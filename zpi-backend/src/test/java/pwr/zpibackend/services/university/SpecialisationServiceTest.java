package pwr.zpibackend.services.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Specialization;
import pwr.zpibackend.repositories.university.SpecializationRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpecialisationServiceTest {

    @MockBean
    private SpecializationRepository specializationRepository;

    @Autowired
    private SpecialisationService specializationService;

    private Specialization specialization;

    @BeforeEach
    public void setup() {
        specialization = new Specialization();
        specialization.setAbbreviation("TST");
        specialization.setName("Test Specialization");
    }

    @Test
    public void testGetAllSpecializations() {
        when(specializationRepository.findAll()).thenReturn(List.of(specialization));

        List<Specialization> result = specializationService.getAllSpecializations();

        assertEquals(1, result.size());
        assertEquals(specialization, result.get(0));
    }

    @Test
    public void testGetSpecializationByAbbreviationSuccess() throws NotFoundException {
        when(specializationRepository.findById(specialization.getAbbreviation())).thenReturn(Optional.of(specialization));

        Specialization result = specializationService.getSpecializationByAbbreviation(specialization.getAbbreviation());

        assertEquals(specialization, result);
    }

    @Test
    public void testGetSpecializationByAbbreviationNotFound() {
        when(specializationRepository.findById(specialization.getAbbreviation())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specializationService.getSpecializationByAbbreviation(specialization.getAbbreviation()));
    }

//    @Test
//    public void testSaveSpecializationSuccess() {
//        when(specializationRepository.save(any())).thenReturn(specialization);
//
//        Specialization result = specializationService.saveSpecialization(specialization);
//
//        assertEquals(specialization, result);
//    }

    @Test
    public void testDeleteSpecializationSuccess() throws NotFoundException {
        when(specializationRepository.findById(specialization.getAbbreviation())).thenReturn(Optional.of(specialization));

        Specialization result = specializationService.deleteSpecialization(specialization.getAbbreviation());

        assertEquals(specialization, result);
    }

    @Test
    public void testDeleteSpecializationNotFound() {
        when(specializationRepository.findById(specialization.getAbbreviation())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specializationService.deleteSpecialization(specialization.getAbbreviation()));
    }

    @Test
    public void testUpdateSpecializationSuccess() throws NotFoundException {
        Specialization updatedSpecialization = new Specialization();
        updatedSpecialization.setAbbreviation(specialization.getAbbreviation());
        updatedSpecialization.setName("Updated Test Specialization");

        when(specializationRepository.findById(specialization.getAbbreviation())).thenReturn(Optional.of(specialization));
        when(specializationRepository.save(any())).thenReturn(updatedSpecialization);

        Specialization result = specializationService.updateSpecialization(specialization.getAbbreviation(), updatedSpecialization);

        assertEquals(updatedSpecialization, result);
    }

    @Test
    public void testUpdateSpecializationNotFound() {
        Specialization updatedSpecialization = new Specialization();
        updatedSpecialization.setAbbreviation(specialization.getAbbreviation());
        updatedSpecialization.setName("Updated Test Specialization");

        when(specializationRepository.findById(specialization.getAbbreviation())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specializationService.updateSpecialization(specialization.getAbbreviation(), updatedSpecialization));
    }
}
