package pwr.zpibackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.Student;

import java.util.Optional;


@RepositoryRestResource
public interface StudentRepository extends JpaRepository<Student, Long>{
    Optional<Student> findByMail(String mail);
    Optional<Student> findByIndex(String index);

    boolean existsByMail(String mail);
}
