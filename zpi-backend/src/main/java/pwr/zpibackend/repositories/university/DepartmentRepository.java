package pwr.zpibackend.repositories.university;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.university.Department;

public interface DepartmentRepository extends JpaRepository<Department, String> {
}
