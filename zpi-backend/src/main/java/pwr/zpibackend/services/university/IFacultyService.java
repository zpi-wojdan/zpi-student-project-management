package pwr.zpibackend.services.university;

import pwr.zpibackend.dto.university.FacultyDTO;
import pwr.zpibackend.models.university.Faculty;

import java.util.List;

public interface IFacultyService {
    List<Faculty> getAllFaculties();
    Faculty getFacultyByAbbreviation(String abbreviation);
    Faculty saveFaculty(FacultyDTO faculty);
    Faculty deleteFaculty(Long id);
    Faculty updateFaculty(Long id, FacultyDTO faculty);
    List<Faculty> getAllFacultiesOrderedByAbbreviationAsc();
}
