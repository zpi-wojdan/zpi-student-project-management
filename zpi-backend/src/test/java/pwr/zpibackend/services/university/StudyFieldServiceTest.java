package pwr.zpibackend.services.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.repositories.university.StudyFieldRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StudyFieldServiceTest {

    @MockBean
    private StudyFieldRepository studyFieldRepository;

    @Autowired
    private StudyFieldService studyFieldService;

    private StudyField studyField;

    @BeforeEach
    public void setup() {
        studyField = new StudyField();
        studyField.setAbbreviation("TEST");
        studyField.setName("Test Study Field");
    }

    @Test
    public void testGetAllStudyFields() {
        when(studyFieldRepository.findAll()).thenReturn(List.of(studyField));

        List<StudyField> result = studyFieldService.getAllStudyFields();

        assertEquals(1, result.size());
        assertEquals(studyField, result.get(0));
    }

    @Test
    public void testGetStudyFieldByAbbreviationSuccess() throws NotFoundException {
        when(studyFieldRepository.findById(studyField.getAbbreviation())).thenReturn(Optional.of(studyField));

        StudyField result = studyFieldService.getStudyFieldByAbbreviation(studyField.getAbbreviation());

        assertEquals(studyField, result);
    }

    @Test
    public void testGetStudyFieldByAbbreviationNotFound() {
        when(studyFieldRepository.findById(studyField.getAbbreviation())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studyFieldService.getStudyFieldByAbbreviation(studyField.getAbbreviation()));
    }

    @Test
    public void testSaveStudyFieldSuccess() {
        when(studyFieldRepository.save(any())).thenReturn(studyField);

        StudyField result = studyFieldService.saveStudyField(studyField);

        assertEquals(studyField, result);
    }

    @Test
    public void testDeleteStudyFieldSuccess() throws NotFoundException {
        when(studyFieldRepository.findById(studyField.getAbbreviation())).thenReturn(Optional.of(studyField));

        StudyField result = studyFieldService.deleteStudyField(studyField.getAbbreviation());

        assertEquals(studyField, result);
    }

    @Test
    public void testDeleteStudyFieldNotFound() {
        when(studyFieldRepository.findById(studyField.getAbbreviation())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studyFieldService.deleteStudyField(studyField.getAbbreviation()));
    }

    @Test
    public void testUpdateStudyFieldSuccess() throws NotFoundException {
        StudyField updatedStudyField = new StudyField();
        updatedStudyField.setAbbreviation(studyField.getAbbreviation());
        updatedStudyField.setName("Updated Test Study Field");

        when(studyFieldRepository.findById(studyField.getAbbreviation())).thenReturn(Optional.of(studyField));
        when(studyFieldRepository.save(any())).thenReturn(updatedStudyField);

        StudyField result = studyFieldService.updateStudyField(studyField.getAbbreviation(), updatedStudyField);

        assertEquals(updatedStudyField, result);
    }

    @Test
    public void testUpdateStudyFieldNotFound() {
        StudyField updatedStudyField = new StudyField();
        updatedStudyField.setAbbreviation(studyField.getAbbreviation());
        updatedStudyField.setName("Updated Test Study Field");

        when(studyFieldRepository.findById(studyField.getAbbreviation())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studyFieldService.updateStudyField(studyField.getAbbreviation(), updatedStudyField));
    }
}
