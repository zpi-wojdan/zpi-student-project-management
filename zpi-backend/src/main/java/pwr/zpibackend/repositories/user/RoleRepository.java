package pwr.zpibackend.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.user.Role;

import java.util.Optional;

@RepositoryRestResource
public interface RoleRepository extends JpaRepository<Role, Long> {
        boolean existsByName(String name);
        Optional<Role> findByName(String name);
}
