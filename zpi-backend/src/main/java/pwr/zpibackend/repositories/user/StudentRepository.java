package pwr.zpibackend.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.user.Student;

import java.util.List;
import java.util.Optional;


@RepositoryRestResource
public interface StudentRepository extends JpaRepository<Student, Long>{
    Optional<Student> findByMail(String mail);
    Optional<Student> findByIndex(String index);

    boolean existsByMail(String mail);

    boolean existsByIndex(String index);

    List<Student> findAllByOrderByIndexAsc();
}
