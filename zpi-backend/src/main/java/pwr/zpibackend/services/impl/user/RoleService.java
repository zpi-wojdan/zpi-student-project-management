package pwr.zpibackend.services.impl.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.CannotDeleteException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.user.Role;
import pwr.zpibackend.dto.user.RoleDTO;
import pwr.zpibackend.repositories.user.RoleRepository;
import pwr.zpibackend.services.user.IRoleService;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRole(Long roleId) {
        return roleRepository.findById(roleId).orElseThrow(
                () -> new NotFoundException("Role with id " + roleId + " does not exist")
        );
    }

    public Role addRole(RoleDTO role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new AlreadyExistsException("Role with name " + role.getName() + " already exists");
        }
        Role newRole = new Role(role.getName());
        return roleRepository.save(newRole);
    }

    public Role updateRole(Long roleId, RoleDTO updatedRole) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role != null) {
            if (roleRepository.existsByName(updatedRole.getName()) && !role.getName().equals(updatedRole.getName())) {
                throw new AlreadyExistsException("Role with name " + updatedRole.getName() + " already exists");
            }
            role.setName(updatedRole.getName());
            return roleRepository.save(role);
        }
        throw new NotFoundException("Role with id " + roleId + " does not exist");
    }

    public Role deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role != null) {
            if ((role.getEmployees() == null || role.getEmployees().isEmpty()) &&
                    (role.getStudents() == null || role.getStudents().isEmpty())) {
                roleRepository.delete(role);
                return role;
            } else {
                throw new CannotDeleteException("Role with id " + roleId + " is used by some users");
            }
        } else {
            throw new NotFoundException("Role with id " + roleId + " does not exist");
        }
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("Role with name " + name + " does not exist")
        );
    }

}
