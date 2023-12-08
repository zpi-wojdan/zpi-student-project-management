package pwr.zpibackend.services.university;

import pwr.zpibackend.dto.university.SpecializationDTO;
import pwr.zpibackend.models.university.Specialization;

import java.util.List;

public interface ISpecializationService {
    List<Specialization> getAllSpecializations();
    Specialization getSpecializationByAbbreviation(String abbreviation);
    Specialization saveSpecialization(SpecializationDTO specialization);
    Specialization deleteSpecialization(Long id);
    Specialization updateSpecialization(Long id, SpecializationDTO updatedSpecialization);
}
