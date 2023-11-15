package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.university.FacultyDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
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

    public Faculty getFacultyByAbbreviation(String abbreviation) {
        return facultyRepository.findByAbbreviation(abbreviation)
                .orElseThrow(NotFoundException::new);
    }

    public Faculty saveFaculty(FacultyDTO faculty) {
        if (facultyRepository.existsByAbbreviation(faculty.getAbbreviation())) {
            throw new AlreadyExistsException();
        }
        Faculty newFaculty = new Faculty();
        newFaculty.setAbbreviation(faculty.getAbbreviation());
        newFaculty.setName(faculty.getName());
        return facultyRepository.saveAndFlush(newFaculty);
    }

    public Faculty deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        facultyRepository.delete(faculty);
        return faculty;
    }

    public Faculty updateFaculty(Long id, FacultyDTO faculty) {
        if (facultyRepository.existsByAbbreviation(faculty.getAbbreviation())) {
            if (!(facultyRepository.findByAbbreviation(faculty.getAbbreviation()).get().getId() == id)) {
                throw new AlreadyExistsException();
            }
        }
        Faculty existingFaculty = facultyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        existingFaculty.setAbbreviation(faculty.getAbbreviation());
        existingFaculty.setName(faculty.getName());
        return facultyRepository.saveAndFlush(existingFaculty);
    }
}
