package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {
}
