package pwr.zpibackend.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.user.Employee;

import java.util.List;
import java.util.Optional;


@RepositoryRestResource
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
    List<Employee> findAllByMailStartingWith(String prefix);
    Optional<Employee> findByMail(String mail);

    boolean existsByMail(String mail);
}

