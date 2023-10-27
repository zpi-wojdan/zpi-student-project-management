package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, String> {
}
