package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import pwr.zpibackend.dto.university.StudyFieldDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.repositories.university.FacultyRepository;
import pwr.zpibackend.repositories.university.StudyFieldRepository;
import pwr.zpibackend.exceptions.NotFoundException;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class StudyFieldService {

    private StudyFieldRepository studyFieldRepository;
    private FacultyRepository facultyRepository;

    public List<StudyField> getAllStudyFields() {
        return studyFieldRepository.findAll();
    }

    public StudyField getStudyFieldByAbbreviation(String abbreviation) {
        return studyFieldRepository.findByAbbreviation(abbreviation)
                .orElseThrow(NotFoundException::new);
    }

    public StudyField saveStudyField(StudyFieldDTO studyField) {
        if (studyFieldRepository.existsByAbbreviation(studyField.getAbbreviation())) {
            throw new AlreadyExistsException();
        }
        StudyField newStudyField = new StudyField();
        newStudyField.setAbbreviation(studyField.getAbbreviation());
        newStudyField.setName(studyField.getName());
        newStudyField.setFaculty(facultyRepository.findByAbbreviation(studyField.getFacultyAbbr()).orElseThrow(NotFoundException::new));
        return studyFieldRepository.save(newStudyField);
    }

    public StudyField deleteStudyField(Long id) {
        StudyField studyField = studyFieldRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        studyField.setFaculty(null);
        studyFieldRepository.delete(studyField);
        return studyField;
    }

    public StudyField updateStudyField(Long id, StudyFieldDTO updatedStudyField) {
        if (studyFieldRepository.existsByAbbreviation(updatedStudyField.getAbbreviation())) {
            if (!(Objects.equals(studyFieldRepository.findByAbbreviation(updatedStudyField.getAbbreviation()).get().getId(), id))) {
                throw new AlreadyExistsException("studyField with abbreviation " + updatedStudyField.getAbbreviation() + " already exists");
            }
        }
        StudyField existingStudyField = studyFieldRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        existingStudyField.setAbbreviation(updatedStudyField.getAbbreviation());
        existingStudyField.setName(updatedStudyField.getName());
        existingStudyField.setFaculty(facultyRepository.findByAbbreviation(updatedStudyField.getFacultyAbbr()).orElseThrow(NotFoundException::new));
        return studyFieldRepository.save(existingStudyField);
    }

    public List<StudyField> getAllStudyFieldsOrderedByAbbreviationAsc() {
        return studyFieldRepository.findAllByOrderByAbbreviationAsc();
    }
}
