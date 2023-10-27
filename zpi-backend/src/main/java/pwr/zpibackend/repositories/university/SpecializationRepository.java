package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.Specialization;

public interface SpecializationRepository extends JpaRepository<Specialization, String> {
}
