package pwr.zpibackend.services.impl.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.university.SpecializationDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.university.Specialization;
import pwr.zpibackend.repositories.university.SpecializationRepository;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.repositories.university.StudyFieldRepository;
import pwr.zpibackend.services.university.ISpecializationService;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SpecializationService implements ISpecializationService {

    private final SpecializationRepository specializationRepository;
    private final StudyFieldRepository studyFieldRepository;

    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    public Specialization getSpecializationByAbbreviation(String abbreviation) {
        return specializationRepository.findByAbbreviation(abbreviation)
                .orElseThrow(() -> new NotFoundException("Specialization with abbreviation " + abbreviation + " does not exist"));
    }

    public Specialization saveSpecialization(SpecializationDTO specialization) {
        if (specializationRepository.existsByAbbreviation(specialization.getAbbreviation())) {
            throw new AlreadyExistsException("Specialization with abbreviation " + specialization.getAbbreviation() + " already exists");
        }
        Specialization newSpecialization = new Specialization();
        newSpecialization.setAbbreviation(specialization.getAbbreviation());
        newSpecialization.setName(specialization.getName());
        newSpecialization.setStudyField(studyFieldRepository.findByAbbreviation(specialization.getStudyFieldAbbr())
                .orElseThrow(() -> new NotFoundException("Study field with abbreviation " + specialization.getStudyFieldAbbr() + " does not exist")));
        return specializationRepository.save(newSpecialization);
    }

    public Specialization deleteSpecialization(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Specialization with id " + id + " does not exist"));
        specialization.setStudyField(null);
        specializationRepository.delete(specialization);
        return specialization;
    }

    public Specialization updateSpecialization(Long id, SpecializationDTO updatedSpecialization) {
        if (specializationRepository.existsByAbbreviation(updatedSpecialization.getAbbreviation())) {
            if (!(Objects.equals(
                    specializationRepository.findByAbbreviation(updatedSpecialization.getAbbreviation()).get().getId(),
                    id))) {
                throw new AlreadyExistsException("Specialization with abbreviation " + updatedSpecialization.getAbbreviation() + " already exists");
            }
        }
        Specialization existingSpecialization = specializationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Specialization with id " + id + " does not exist"));
        existingSpecialization.setAbbreviation(updatedSpecialization.getAbbreviation());
        existingSpecialization.setName(updatedSpecialization.getName());
        existingSpecialization.setStudyField(studyFieldRepository.findByAbbreviation(updatedSpecialization.getStudyFieldAbbr())
                .orElseThrow(() -> new NotFoundException("Study field with abbreviation " + updatedSpecialization.getStudyFieldAbbr() + " does not exist")));
        return specializationRepository.save(existingSpecialization);
    }
}
