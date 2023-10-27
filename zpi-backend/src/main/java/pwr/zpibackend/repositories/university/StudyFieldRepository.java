package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.StudyField;

public interface StudyFieldRepository extends JpaRepository<StudyField, String> {
}
