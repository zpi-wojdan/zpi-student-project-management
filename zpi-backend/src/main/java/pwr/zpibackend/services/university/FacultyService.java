package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.repositories.university.FacultyRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Faculty getFacultyByAbbreviation(String abbreviation) throws NotFoundException {
        return facultyRepository.findById(abbreviation)
                .orElseThrow(NotFoundException::new);
    }

    public Faculty saveFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }


    public Faculty deleteFaculty(String abbreviation) throws NotFoundException {
        Faculty faculty = facultyRepository.findById(abbreviation)
                .orElseThrow(NotFoundException::new);
        facultyRepository.delete(faculty);
        return faculty;
    }

    public Faculty updateFaculty(String abbreviation, Faculty faculty) throws NotFoundException {
        Faculty existingFaculty = facultyRepository.findById(abbreviation)
                .orElseThrow(NotFoundException::new);
        existingFaculty.setName(faculty.getName());
        existingFaculty.setStudyFields(faculty.getStudyFields());
        existingFaculty.setPrograms(faculty.getPrograms());
        existingFaculty.setDepartments(faculty.getDepartments());
        return facultyRepository.save(existingFaculty);
    }
}
