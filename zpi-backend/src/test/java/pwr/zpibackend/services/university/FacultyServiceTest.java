package pwr.zpibackend.services.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

    @BeforeEach
    public void setup() {
        faculty = new Faculty();
        faculty.setAbbreviation("W4");
        faculty.setName("Wydział Elektroniki");
    }

    @Test
    public void testGetAllFaculties() {
        when(facultyRepository.findAll()).thenReturn(List.of(faculty));

        List<Faculty> result = facultyService.getAllFaculties();

        assertEquals(1, result.size());
        assertEquals(faculty, result.get(0));
    }

    @Test
    public void testGetFacultyByAbbreviationSuccess() throws NotFoundException {
        when(facultyRepository.findById(faculty.getAbbreviation())).thenReturn(Optional.of(faculty));

        Faculty result = facultyService.getFacultyByAbbreviation(faculty.getAbbreviation());

        assertEquals(faculty, result);
    }

    @Test
    public void testGetFacultyByAbbreviationNotFound() {
        when(facultyRepository.findById(faculty.getAbbreviation())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> facultyService.getFacultyByAbbreviation(faculty.getAbbreviation()));
    }

//    @Test
//    public void testSaveFacultySuccess() {
//        when(facultyRepository.save(any())).thenReturn(faculty);
//
//        Faculty result = facultyService.saveFaculty(faculty);
//
//        assertEquals(faculty, result);
//    }

    @Test
    public void testDeleteFacultySuccess() throws NotFoundException {
        when(facultyRepository.findById(faculty.getAbbreviation())).thenReturn(Optional.of(faculty));

        Faculty result = facultyService.deleteFaculty(faculty.getAbbreviation());

        assertEquals(faculty, result);
    }

    @Test
    public void testDeleteFacultyNotFound() {
        when(facultyRepository.findById(faculty.getAbbreviation())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> facultyService.deleteFaculty(faculty.getAbbreviation()));
    }

    @Test
    public void testUpdateFacultySuccess() throws NotFoundException {
        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setAbbreviation(faculty.getAbbreviation());
        updatedFaculty.setName("Updated Test Faculty");

        when(facultyRepository.findById(faculty.getAbbreviation())).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any())).thenReturn(updatedFaculty);

        Faculty result = facultyService.updateFaculty(faculty.getAbbreviation(), updatedFaculty);

        assertEquals(updatedFaculty, result);
    }

    @Test
    public void testUpdateFacultyNotFound() {
        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setAbbreviation(faculty.getAbbreviation());
        updatedFaculty.setName("Updated Test Faculty");

        when(facultyRepository.findById(faculty.getAbbreviation())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> facultyService.updateFaculty(faculty.getAbbreviation(), updatedFaculty));
    }
}
