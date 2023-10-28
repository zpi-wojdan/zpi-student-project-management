package pwr.zpibackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.Role;

@RepositoryRestResource
public interface RoleRepository extends JpaRepository<Role, Long> {
        boolean existsByName(String name);
}
