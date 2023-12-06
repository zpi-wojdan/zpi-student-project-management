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

    @Operation(summary = "Get all roles", description = "Returns all roles from database")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @Operation(summary = "Get role by id", description = "Returns role with given id")
    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.getRole(roleId));
    }

    @Operation(summary = "Add role", description = "Adds role to database")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> addRole(@RequestBody RoleDTO role) {
        return new ResponseEntity<>(roleService.addRole(role), HttpStatus.CREATED);
    }

    @Operation(summary = "Update role", description = "Updates role with given id")
    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> updateRole(@PathVariable Long roleId, @RequestBody RoleDTO updatedRole) {
        return ResponseEntity.ok(roleService.updateRole(roleId, updatedRole));
    }

    @Operation(summary = "Delete role", description = "Deletes role with given id")
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> deleteRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.deleteRole(roleId));
    }

}
