package pwr.zpibackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.Status;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {
    boolean existsByName(String name);
    Optional<Status> findByName(String name);
}
