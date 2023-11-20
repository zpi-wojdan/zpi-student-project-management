package pwr.zpibackend.services.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pwr.zpibackend.dto.university.FacultyDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.repositories.university.FacultyRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FacultyServiceTest {

    @MockBean
    private FacultyRepository facultyRepository;

    @Autowired
    private FacultyService facultyService;

    private Faculty faculty;
    private List<Faculty> faculties;
    private List<Faculty> orderedFaculties;

    @BeforeEach
    public void setup() {
        faculty = new Faculty();
        faculty.setAbbreviation("W04");
        faculty.setName("Wydział Elektroniki");

        Faculty faculty2 = new Faculty();
        faculty2.setAbbreviation("W02");
        faculty2.setName("Wydział Chemiczny");

        faculties = List.of(faculty, faculty2);
        orderedFaculties = List.of(faculty2, faculty);
    }

    @Test
    public void testGetAllFaculties() {
        when(facultyRepository.findAll()).thenReturn(faculties);

        List<Faculty> result = facultyService.getAllFaculties();

        assertEquals(2, result.size());
        assertEquals(faculties, result);
    }

    @Test
    public void testGetFacultyByAbbreviationSuccess() throws NotFoundException {
        when(facultyRepository.findByAbbreviation(faculty.getAbbreviation())).thenReturn(Optional.of(faculty));

        Faculty result = facultyService.getFacultyByAbbreviation(faculty.getAbbreviation());

        assertEquals(faculty, result);
    }

    @Test
    public void testGetFacultyByAbbreviationNotFound() {
        when(facultyRepository.findByAbbreviation(faculty.getAbbreviation())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> facultyService.getFacultyByAbbreviation(faculty.getAbbreviation()));
    }

    @Test
    public void testSaveFacultySuccess() throws AlreadyExistsException {
        FacultyDTO newFaculty = new FacultyDTO();
        newFaculty.setAbbreviation(faculty.getAbbreviation());
        newFaculty.setName("Wydział Elektroniki");

        when(facultyRepository.saveAndFlush(any())).thenReturn(faculty);

        Faculty result = facultyService.saveFaculty(newFaculty);

        assertEquals(faculty, result);
    }

    @Test
    public void testDeleteFacultySuccess() throws NotFoundException {
        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));

        Faculty result = facultyService.deleteFaculty(faculty.getId());

        assertEquals(faculty, result);
    }

    @Test
    public void testDeleteFacultyNotFound() {
        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> facultyService.deleteFaculty(faculty.getId()));
    }

    @Test
    public void testUpdateFacultySuccess() throws NotFoundException {
        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setAbbreviation(faculty.getAbbreviation());
        updatedFaculty.setName("Updated Test Faculty");

        FacultyDTO updatedFacultyDTO = new FacultyDTO();
        updatedFaculty.setAbbreviation(faculty.getAbbreviation());
        updatedFaculty.setName("Updated Test Faculty");

        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));
        when(facultyRepository.saveAndFlush(any())).thenReturn(updatedFaculty);

        Faculty result = facultyService.updateFaculty(faculty.getId(), updatedFacultyDTO);

        assertEquals(updatedFaculty, result);
    }

    @Test
    public void testUpdateFacultyNotFound() {
        FacultyDTO updatedFaculty = new FacultyDTO();
        updatedFaculty.setAbbreviation(faculty.getAbbreviation());
        updatedFaculty.setName("Updated Test Faculty");

        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> facultyService.updateFaculty(faculty.getId(), updatedFaculty));
    }

    @Test
    public void testGetAllFacultiesOrderedByAbbreviationAsc() {
        when(facultyRepository.findAllByOrderByAbbreviationAsc()).thenReturn(orderedFaculties);

        List<Faculty> result = facultyService.getAllFacultiesOrderedByAbbreviationAsc();

        assertEquals(2, result.size());
        assertEquals(orderedFaculties, result);
    }
}
