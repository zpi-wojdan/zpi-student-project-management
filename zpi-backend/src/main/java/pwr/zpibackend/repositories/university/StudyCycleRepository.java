package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.StudyCycle;

public interface StudyCycleRepository extends JpaRepository<StudyCycle, Long> {
}
