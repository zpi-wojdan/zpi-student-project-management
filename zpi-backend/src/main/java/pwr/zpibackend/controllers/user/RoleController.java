package pwr.zpibackend.controllers.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.models.user.Role;
import pwr.zpibackend.dto.user.RoleDTO;
import pwr.zpibackend.services.user.IRoleService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/role")
public class RoleController {

    private final IRoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles", description = "Returns all roles from database. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Get role by id", description = "Returns role with given id. <br>" +
            "Requires authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.getRole(roleId));
    }

    @PostMapping
    @Operation(summary = "Add role", description = "Adds role to database. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> addRole(@RequestBody RoleDTO role) {
        return new ResponseEntity<>(roleService.addRole(role), HttpStatus.CREATED);
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "Update role", description = "Updates role with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> updateRole(@PathVariable Long roleId, @RequestBody RoleDTO updatedRole) {
        return ResponseEntity.ok(roleService.updateRole(roleId, updatedRole));
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "Delete role", description = "Deletes role with given id. <br>Requires ADMIN role.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> deleteRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.deleteRole(roleId));
    }

}
