package pwr.zpibackend.services.impl.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.university.FacultyDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.repositories.university.FacultyRepository;
import pwr.zpibackend.services.university.IFacultyService;

import java.util.List;

@Service
@AllArgsConstructor
public class FacultyService implements IFacultyService {

    private final FacultyRepository facultyRepository;

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Faculty getFacultyByAbbreviation(String abbreviation) {
        return facultyRepository.findByAbbreviation(abbreviation)
                .orElseThrow(() -> new NotFoundException("Faculty with abbreviation " + abbreviation + " does not exist"));
    }

    public Faculty saveFaculty(FacultyDTO faculty) {
        if (facultyRepository.existsByAbbreviation(faculty.getAbbreviation())) {
            throw new AlreadyExistsException("Faculty with abbreviation " + faculty.getAbbreviation() + " already exists");
        }
        Faculty newFaculty = new Faculty();
        newFaculty.setAbbreviation(faculty.getAbbreviation());
        newFaculty.setName(faculty.getName());
        return facultyRepository.saveAndFlush(newFaculty);
    }

    public Faculty deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty with id " + id + " does not exist"));
        facultyRepository.delete(faculty);
        return faculty;
    }

    public Faculty updateFaculty(Long id, FacultyDTO faculty) {
        if (facultyRepository.existsByAbbreviation(faculty.getAbbreviation())) {
            if (!(facultyRepository.findByAbbreviation(faculty.getAbbreviation()).get().getId() == id)) {
                throw new AlreadyExistsException("Faculty with abbreviation " + faculty.getAbbreviation() + " already exists");
            }
        }
        Faculty existingFaculty = facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty with id " + id + " does not exist"));
        existingFaculty.setAbbreviation(faculty.getAbbreviation());
        existingFaculty.setName(faculty.getName());
        return facultyRepository.saveAndFlush(existingFaculty);
    }

    public List<Faculty> getAllFacultiesOrderedByAbbreviationAsc() {
        return facultyRepository.findAllByOrderByAbbreviationAsc();
    }
}
