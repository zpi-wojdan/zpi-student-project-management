package pwr.zpibackend.services.university;

import pwr.zpibackend.dto.university.StudyFieldDTO;
import pwr.zpibackend.models.university.StudyField;

import java.util.List;

public interface IStudyFieldService {
    List<StudyField> getAllStudyFields();
    StudyField getStudyFieldByAbbreviation(String abbreviation);
    StudyField saveStudyField(StudyFieldDTO studyField);
    StudyField deleteStudyField(Long id);
    StudyField updateStudyField(Long id, StudyFieldDTO updatedStudyField);
    List<StudyField> getAllStudyFieldsOrderedByAbbreviationAsc();
}
