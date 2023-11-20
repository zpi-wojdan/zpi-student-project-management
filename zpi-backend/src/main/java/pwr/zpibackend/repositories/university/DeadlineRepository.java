package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.Deadline;

import java.util.List;

public interface DeadlineRepository extends JpaRepository<Deadline, Long> {
    List<Deadline> findAllByOrderByDeadlineDateAsc();
    boolean existsByNamePL(String namePL);
    boolean existsByNameEN(String nameEN);
}
