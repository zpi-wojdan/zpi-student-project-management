package pwr.zpibackend.services.user;

import pwr.zpibackend.dto.user.RoleDTO;
import pwr.zpibackend.models.user.Role;

import java.util.List;

public interface IRoleService {
    List<Role> getAllRoles();
    Role getRole(Long roleId);
    Role addRole(RoleDTO role);
    Role updateRole(Long roleId, RoleDTO role);
    Role deleteRole(Long roleId);
    Role getRoleByName(String name);
}
