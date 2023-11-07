package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.StudentProgramCycle;
import pwr.zpibackend.models.university.StudentProgramCycleId;

import java.util.List;

public interface StudentProgramCycleRepository extends JpaRepository<StudentProgramCycle, StudentProgramCycleId> {
    List<StudentProgramCycle> findByStudentId(Long id);
}
