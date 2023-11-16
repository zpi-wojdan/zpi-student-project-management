package pwr.zpibackend.repositories.thesis;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.thesis.Status;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {
    boolean existsByName(String name);
    Optional<Status> findByName(String name);
}
