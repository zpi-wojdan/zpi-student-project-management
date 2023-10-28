package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.university.Specialization;
import pwr.zpibackend.repositories.university.SpecializationRepository;
import pwr.zpibackend.exceptions.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class SpecialisationService {

    private final SpecializationRepository specializationRepository;

    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    public Specialization getSpecializationByAbbreviation(String abbreviation) throws NotFoundException {
        return specializationRepository.findById(abbreviation)
                .orElseThrow(NotFoundException::new);
    }

    public Specialization saveSpecialization(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    public Specialization deleteSpecialization(String abbreviation) throws NotFoundException {
        Specialization specialization = specializationRepository.findById(abbreviation)
                .orElseThrow(NotFoundException::new);
        specializationRepository.delete(specialization);
        return specialization;
    }

    public Specialization updateSpecialization(String abbreviation, Specialization updatedSpecialization) throws NotFoundException {
        Specialization existingSpecialization = specializationRepository.findById(abbreviation)
                .orElseThrow(NotFoundException::new);
        existingSpecialization.setName(updatedSpecialization.getName());
        existingSpecialization.setStudyField(updatedSpecialization.getStudyField());
        return specializationRepository.save(existingSpecialization);
    }
}
