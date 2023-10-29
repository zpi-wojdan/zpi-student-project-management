package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Role;
import pwr.zpibackend.dto.RoleDTO;
import pwr.zpibackend.repositories.RoleRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRole(Long roleId) {
        return roleRepository.findById(roleId).orElseThrow(
                () -> new NoSuchElementException("Role with id " + roleId + " does not exist")
        );
    }

    public Role addRole(RoleDTO role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role with name " + role.getName() + " already exists");
        }
        Role newRole = new Role(role.getName());
        return roleRepository.save(newRole);
    }

    public Role updateRole(Long roleId, RoleDTO updatedRole) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role != null) {
            if (roleRepository.existsByName(updatedRole.getName()) && !role.getName().equals(updatedRole.getName())) {
                throw new IllegalArgumentException("Role with name " + updatedRole.getName() + " already exists");
            }
            role.setName(updatedRole.getName());
            return roleRepository.save(role);
        }
        throw new NoSuchElementException("Role with id " + roleId + " does not exist");
    }

    public Role deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role != null) {
            if (role.getEmployees().isEmpty() && role.getStudents().isEmpty()) {
                roleRepository.delete(role);
                return role;
            } else {
                throw new IllegalArgumentException("Role with id " + roleId + " is used by some users");
            }
        } else {
            throw new NoSuchElementException("Role with id " + roleId + " does not exist");
        }
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElseThrow(
                () -> new NoSuchElementException("Role with name " + name + " does not exist")
        );
    }

}
