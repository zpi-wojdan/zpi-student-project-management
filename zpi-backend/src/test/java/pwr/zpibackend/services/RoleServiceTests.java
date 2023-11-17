package pwr.zpibackend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pwr.zpibackend.dto.user.RoleDTO;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Role;
import pwr.zpibackend.repositories.user.RoleRepository;
import pwr.zpibackend.services.user.RoleService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RoleServiceTests {

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    private Role role;
    private RoleDTO roleDTO;

    @BeforeEach
    public void setUp() {
        role = new Role(1L, "admin");
        roleDTO = new RoleDTO("student");
    }

    @Test
    public void testGetAllRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(role));

        List<Role> result = roleService.getAllRoles();

        assertEquals(1, result.size());
        assertEquals(role, result.get(0));
    }

    @Test
    public void testGetRoleById() {
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

        Role result = roleService.getRole(role.getId());

        assertEquals(role, result);
    }

    @Test
    public void testGetRoleByIdNotFound() {
        when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> roleService.getRole(role.getId()));
    }

    @Test
    public void testAddRole() {
        Role newRole = new Role(roleDTO.getName());

        when(roleRepository.existsByName(roleDTO.getName())).thenReturn(false);
        when(roleRepository.save(newRole)).thenReturn(newRole);

        Role result = roleService.addRole(roleDTO);

        assertEquals(newRole, result);
    }

    @Test
    public void testAddRoleAlreadyExists() {
        when(roleRepository.existsByName(roleDTO.getName())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> roleService.addRole(roleDTO));
    }

    @Test
    public void testUpdateRole() {
        Role updatedRole = new Role(role.getId(), roleDTO.getName());

        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(roleRepository.existsByName(roleDTO.getName())).thenReturn(false);
        when(roleRepository.save(updatedRole)).thenReturn(updatedRole);

        Role result = roleService.updateRole(role.getId(), roleDTO);

        assertEquals(role, result);
    }

    @Test
    public void testUpdateRoleNotFound() {
        when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> roleService.updateRole(role.getId(), roleDTO));
    }

    @Test
    public void testUpdateRoleAlreadyExists() {
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(roleRepository.existsByName(roleDTO.getName())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> roleService.updateRole(role.getId(), roleDTO));
    }

    @Test
    public void testDeleteRole() {
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

        Role result = roleService.deleteRole(role.getId());

        assertEquals(role, result);
    }

    @Test
    public void testDeleteRoleNotFound() {
        when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> roleService.deleteRole(role.getId()));
    }

    @Test
    public void testDeleteRoleInUse() {
        role.setEmployees(List.of(new Employee()));
        role.setStudents(List.of());

        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

        assertThrows(IllegalArgumentException.class, () -> roleService.deleteRole(role.getId()));
    }

    @Test
    public void testGetRoleByName() {
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));

        Role result = roleService.getRoleByName(role.getName());

        assertEquals(role, result);
    }

    @Test
    public void testGetRoleByNameNotFound() {
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> roleService.getRoleByName(role.getName()));
    }
}
