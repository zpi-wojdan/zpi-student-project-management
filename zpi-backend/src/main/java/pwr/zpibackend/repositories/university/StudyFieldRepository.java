package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.StudyField;

import java.util.Optional;

public interface StudyFieldRepository extends JpaRepository<StudyField, Long> {
    Optional<StudyField> findByAbbreviation(String abbreviation);
    boolean existsByAbbreviation(String abbreviation);
}
