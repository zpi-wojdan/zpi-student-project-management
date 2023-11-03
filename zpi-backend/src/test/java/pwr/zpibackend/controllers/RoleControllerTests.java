package pwr.zpibackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.dto.RoleDTO;
import pwr.zpibackend.models.Role;
import pwr.zpibackend.services.EmployeeService;
import pwr.zpibackend.services.RoleService;
import pwr.zpibackend.services.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RoleControllerTests {

    private static final String BASE_URL = "/role";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private RoleService roleService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private RoleController roleController;

    private List<Role> roles;
    private Role role;
    private RoleDTO roleDTO;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("admin");

        roleDTO = new RoleDTO();
        roleDTO.setName("student");

        roles = new ArrayList<>();
        roles.add(role);
    }

    @Test
    public void testGetAllRoles() throws Exception {
        Mockito.when(roleService.getAllRoles()).thenReturn(roles);

        String returnedJson = objectMapper.writeValueAsString(roles);

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(roleService).getAllRoles();
    }

    @Test
    public void testGetRoleById() throws Exception {
        Long roleId = 1L;
        Mockito.when(roleService.getRole(roleId)).thenReturn(role);

        String returnedJson = objectMapper.writeValueAsString(role);

        mockMvc.perform(get(BASE_URL + "/{roleId}", roleId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(roleService).getRole(roleId);
    }

    @Test
    public void testGetRoleByIdNotFound() throws Exception {
        Long roleId = 1L;
        Mockito.when(roleService.getRole(roleId)).thenThrow(new NoSuchElementException());

        mockMvc.perform(get(BASE_URL + "/{roleId}", roleId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(roleService).getRole(roleId);
    }

    @Test
    public void testAddRole() throws Exception {
        String requestBody = objectMapper.writeValueAsString(roleDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(roleService).addRole(roleDTO);
    }

    @Test
    public void testAddRoleAlreadyExists() throws Exception {
        Mockito.when(roleService.addRole(roleDTO)).thenThrow(new IllegalArgumentException());

        String requestBody = objectMapper.writeValueAsString(roleDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(roleService).addRole(roleDTO);
    }

    @Test
    public void testUpdateRole() throws Exception {
        Long roleId = 1L;
        roleDTO.setName("updated");
        role.setName("updated");

        Mockito.when(roleService.updateRole(roleId, roleDTO)).thenReturn(role);

        String requestBody = objectMapper.writeValueAsString(roleDTO);
        String responseBody = objectMapper.writeValueAsString(role);

        mockMvc.perform(put(BASE_URL + "/{roleId}", roleId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(roleService).updateRole(roleId, roleDTO);
    }

    @Test
    public void testUpdateRoleNotFound() throws Exception {
        Long roleId = 1L;

        Mockito.when(roleService.updateRole(roleId, roleDTO)).thenThrow(new NoSuchElementException());

        String requestBody = objectMapper.writeValueAsString(roleDTO);

        mockMvc.perform(put(BASE_URL + "/{roleId}", roleId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(roleService).updateRole(roleId, roleDTO);
    }

    @Test
    public void testUpdateRoleAlreadyExists() throws Exception {
        Long roleId = 1L;
        roleDTO.setName("admin");

        Mockito.when(roleService.updateRole(roleId, roleDTO)).thenThrow(new IllegalArgumentException());

        String requestBody = objectMapper.writeValueAsString(roleDTO);

        mockMvc.perform(put(BASE_URL + "/{roleId}", roleId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(roleService).updateRole(roleId, roleDTO);
    }

    @Test
    public void testDeleteRole() throws Exception {
        Long roleId = 1L;

        Mockito.when(roleService.deleteRole(roleId)).thenReturn(role);

        String returnedJson = objectMapper.writeValueAsString(role);

        mockMvc.perform(delete(BASE_URL + "/{roleId}", roleId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(returnedJson));

        verify(roleService).deleteRole(roleId);
    }

    @Test
    public void testDeleteRoleNotFound() throws Exception {
        Long roleId = 2L;

        Mockito.when(roleService.deleteRole(roleId)).thenThrow(new NoSuchElementException());

        mockMvc.perform(delete(BASE_URL + "/{roleId}", roleId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(roleService).deleteRole(roleId);
    }

}