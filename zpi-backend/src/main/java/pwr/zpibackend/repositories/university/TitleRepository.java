package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.Title;

import java.util.Optional;

public interface TitleRepository extends JpaRepository<Title, Long> {
    Optional<Title> findByName(String name);

    boolean existsByName(String name);
}
