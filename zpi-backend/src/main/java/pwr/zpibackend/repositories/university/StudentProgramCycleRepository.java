package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.StudentProgramCycle;
import pwr.zpibackend.models.university.StudentProgramCycleId;

public interface StudentProgramCycleRepository extends JpaRepository<StudentProgramCycle, StudentProgramCycleId> {
}
