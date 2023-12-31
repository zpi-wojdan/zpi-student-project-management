package pwr.zpibackend.services.impl.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pwr.zpibackend.dto.university.StudyFieldDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.repositories.university.FacultyRepository;
import pwr.zpibackend.repositories.university.StudyFieldRepository;
import pwr.zpibackend.services.impl.university.StudyFieldService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StudyFieldServiceTests {

    @MockBean
    private StudyFieldRepository studyFieldRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @Autowired
    private StudyFieldService studyFieldService;

    private StudyField studyField;
    private StudyFieldDTO studyFieldDTO;
    private List<StudyField> studyFields;
    private List<StudyField> orderedStudyFields;

    @BeforeEach
    public void setup() {
        studyField = new StudyField();
        studyField.setId(1L);
        studyField.setAbbreviation("TEST");
        studyField.setName("Test Study Field");

        StudyField studyField2 = new StudyField();
        studyField2.setId(2L);
        studyField2.setAbbreviation("Another");
        studyField2.setName("Another Study Field");

        studyFields = List.of(studyField, studyField2);
        orderedStudyFields = List.of(studyField2, studyField);

        studyFieldDTO = new StudyFieldDTO();
        studyFieldDTO.setAbbreviation("TEST");
        studyFieldDTO.setName("Test Study Field");
    }

    @Test
    public void testGetAllStudyFields() {
        when(studyFieldRepository.findAll()).thenReturn(studyFields);

        List<StudyField> result = studyFieldService.getAllStudyFields();

        assertEquals(2, result.size());
        assertEquals(studyFields, result);
    }

    @Test
    public void testGetStudyFieldByAbbreviationSuccess() throws NotFoundException {
        when(studyFieldRepository.findByAbbreviation(studyField.getAbbreviation())).thenReturn(Optional.of(studyField));


        StudyField result = studyFieldService.getStudyFieldByAbbreviation(studyField.getAbbreviation());

        assertEquals(studyField, result);
    }

    @Test
    public void testGetStudyFieldByAbbreviationNotFound() {
        when(studyFieldRepository.findById(studyField.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studyFieldService.getStudyFieldByAbbreviation(studyField.getAbbreviation()));
    }

    @Test
    public void testSaveStudyFieldSuccess() throws AlreadyExistsException, NotFoundException {
        when(studyFieldRepository.save(any())).thenReturn(studyField);
        when(facultyRepository.findByAbbreviation(any())).thenReturn(Optional.of(new Faculty()));

        StudyField result = studyFieldService.saveStudyField(studyFieldDTO);

        assertEquals(studyField, result);
    }

    @Test
    public void testDeleteStudyFieldSuccess() throws NotFoundException {
        when(studyFieldRepository.findById(studyField.getId())).thenReturn(Optional.of(studyField));

        StudyField result = studyFieldService.deleteStudyField(studyField.getId());

        assertEquals(studyField, result);
    }

    @Test
    public void testDeleteStudyFieldNotFound() {
        when(studyFieldRepository.findById(studyField.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studyFieldService.deleteStudyField(studyField.getId()));
    }

    @Test
    public void testUpdateStudyFieldSuccess() throws NotFoundException {
        StudyField updatedStudyField = new StudyField();
        updatedStudyField.setAbbreviation(studyField.getAbbreviation());
        updatedStudyField.setName("Updated Test Study Field");

        StudyFieldDTO updatedStudyFieldDTO = new StudyFieldDTO();
        updatedStudyFieldDTO.setAbbreviation(studyField.getAbbreviation());
        updatedStudyFieldDTO.setName("Updated Test Study Field");

        when(studyFieldRepository.findById(studyField.getId())).thenReturn(Optional.of(studyField));
        when(studyFieldRepository.save(any())).thenReturn(updatedStudyField);
        when(facultyRepository.findByAbbreviation(any())).thenReturn(Optional.of(new Faculty()));

        StudyField result = studyFieldService.updateStudyField(studyField.getId(), updatedStudyFieldDTO);

        assertEquals(updatedStudyField, result);
    }

    @Test
    public void testUpdateStudyFieldNotFound() {
        StudyFieldDTO updatedStudyFieldDTO = new StudyFieldDTO();
        updatedStudyFieldDTO.setAbbreviation(studyField.getAbbreviation());
        updatedStudyFieldDTO.setName("Updated Test Study Field");

        when(studyFieldRepository.findById(studyField.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studyFieldService.updateStudyField(studyField.getId(), updatedStudyFieldDTO));
    }

    @Test
    public void testGetAllStudyFieldsOrderedByAbbreviationAsc() {
        when(studyFieldRepository.findAllByOrderByAbbreviationAsc()).thenReturn(orderedStudyFields);

        List<StudyField> result = studyFieldService.getAllStudyFieldsOrderedByAbbreviationAsc();

        assertEquals(2, result.size());
        assertEquals(orderedStudyFields, result);
    }
}
