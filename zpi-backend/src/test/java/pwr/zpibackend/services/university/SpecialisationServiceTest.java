package pwr.zpibackend.services.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pwr.zpibackend.dto.university.SpecializationDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Specialization;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.repositories.university.SpecializationRepository;
import pwr.zpibackend.repositories.university.StudyFieldRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpecialisationServiceTest {

    @MockBean
    private SpecializationRepository specializationRepository;

    @MockBean
    private StudyFieldRepository studyFieldRepository;

    @Autowired
    private SpecialisationService specializationService;

    private Specialization specialization;
    private SpecializationDTO specializationDTO;

    @BeforeEach
    public void setup() {
        specialization = new Specialization();
        specialization.setId(1L);
        specialization.setAbbreviation("TST");
        specialization.setName("Test Specialization");

        specializationDTO = new SpecializationDTO();
        specializationDTO.setAbbreviation("TST");
        specializationDTO.setName("Test Specialization");
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
        when(specializationRepository.findByAbbreviation(specialization.getAbbreviation())).thenReturn(Optional.of(specialization));

        Specialization result = specializationService.getSpecializationByAbbreviation(specialization.getAbbreviation());

        assertEquals(specialization, result);
    }

    @Test
    public void testGetSpecializationByAbbreviationNotFound() {
        when(specializationRepository.findById(specialization.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specializationService.getSpecializationByAbbreviation(specialization.getAbbreviation()));
    }

    @Test
    public void testSaveSpecializationSuccess() throws AlreadyExistsException, NotFoundException {
        when(specializationRepository.save(any())).thenReturn(specialization);
        when(studyFieldRepository.findByAbbreviation(any())).thenReturn(Optional.of(new StudyField()));

        Specialization result = specializationService.saveSpecialization(specializationDTO);

        assertEquals(specialization, result);
    }

    @Test
    public void testDeleteSpecializationSuccess() throws NotFoundException {
        when(specializationRepository.findById(specialization.getId())).thenReturn(Optional.of(specialization));

        Specialization result = specializationService.deleteSpecialization(specialization.getId());

        assertEquals(specialization, result);
    }

    @Test
    public void testDeleteSpecializationNotFound() {
        when(specializationRepository.findById(specialization.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specializationService.deleteSpecialization(specialization.getId()));
    }

    @Test
    public void testUpdateSpecializationSuccess() throws NotFoundException {
        Specialization updatedSpecialization = new Specialization();
        updatedSpecialization.setAbbreviation(specialization.getAbbreviation());
        updatedSpecialization.setName("Updated Test Specialization");

        SpecializationDTO updatedSpecializationDTO = new SpecializationDTO();
        updatedSpecializationDTO.setAbbreviation(specialization.getAbbreviation());
        updatedSpecializationDTO.setName("Updated Test Specialization");

        when(specializationRepository.findById(specialization.getId())).thenReturn(Optional.of(specialization));
        when(specializationRepository.save(any())).thenReturn(updatedSpecialization);
        when(studyFieldRepository.findByAbbreviation(any())).thenReturn(Optional.of(new StudyField()));

        Specialization result = specializationService.updateSpecialization(specialization.getId(), updatedSpecializationDTO);

        assertEquals(updatedSpecialization, result);
    }

    @Test
    public void testUpdateSpecializationNotFound() {
        SpecializationDTO updatedSpecializationDTO = new SpecializationDTO();
        updatedSpecializationDTO.setAbbreviation(specialization.getAbbreviation());
        updatedSpecializationDTO.setName("Updated Test Specialization");

        when(specializationRepository.findById(specialization.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> specializationService.updateSpecialization(specialization.getId(), updatedSpecializationDTO));
    }
}
