package pwr.zpibackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.Employee;

import java.util.List;


@RepositoryRestResource
public interface EmployeeRepository extends JpaRepository<Employee, String>{
    List<Employee> findAllByMailStartingWith(String prefix);
}

