package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.StudyCycle;

import java.util.Optional;

public interface StudyCycleRepository extends JpaRepository<StudyCycle, Long> {
    Optional<StudyCycle> findByName(String name);

    boolean existsByName(String name);
}
