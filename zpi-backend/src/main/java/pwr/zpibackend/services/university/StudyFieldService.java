package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.models.university.StudyField;
import pwr.zpibackend.repositories.university.StudyFieldRepository;
import pwr.zpibackend.exceptions.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class StudyFieldService {

    private StudyFieldRepository studyFieldRepository;

    public List<StudyField> getAllStudyFields() {
        return studyFieldRepository.findAll();
    }

    public StudyField getStudyFieldByAbbreviation(String abbreviation) throws NotFoundException {
        return studyFieldRepository.findById(abbreviation)
                .orElseThrow(NotFoundException::new);
    }

    public StudyField saveStudyField(StudyField studyField) throws AlreadyExistsException {
        if (studyFieldRepository.existsById(studyField.getAbbreviation())) {
            throw new AlreadyExistsException();
        }
        return studyFieldRepository.save(studyField);
    }

    public StudyField deleteStudyField(String abbreviation) throws NotFoundException {
        StudyField studyField = studyFieldRepository.findById(abbreviation)
                .orElseThrow(NotFoundException::new);
        studyFieldRepository.delete(studyField);
        return studyField;
    }

    public StudyField updateStudyField(String abbreviation, StudyField updatedStudyField) throws NotFoundException {
        StudyField existingStudyField = studyFieldRepository.findById(abbreviation)
                .orElseThrow(NotFoundException::new);
        existingStudyField.setName(updatedStudyField.getName());
        return studyFieldRepository.save(existingStudyField);
    }
}
