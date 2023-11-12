package pwr.zpibackend.services.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.dto.university.DepartmentDTO;
import pwr.zpibackend.models.university.Faculty;
import pwr.zpibackend.repositories.university.DepartmentRepository;
import pwr.zpibackend.repositories.university.FacultyRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DepartmentServiceTest {

    @MockBean
    private DepartmentRepository departmentRepository;
    @MockBean
    private FacultyRepository facultyRepository;

    @Autowired
    private DepartmentService departmentService;

    private Department department;
    private DepartmentDTO departmentDTO;
    private final Long id = 1L;
    private final String code = "W4";
    private final String name = "Test Department";

    @BeforeEach
    public void setUp() {
        departmentDTO = new DepartmentDTO();
        departmentDTO.setCode("W4");
        departmentDTO.setName(name);
        departmentDTO.setFacultyAbbreviation("ABC");

        department = new Department();
        department.setId(id);
        department.setCode("W4");
        department.setName(name);
    }

    @Test
    public void testGetDepartmentByCodeSuccess() throws NotFoundException {
        when(departmentRepository.findByCode(code)).thenReturn(Optional.of(department));

        Department result = departmentService.getDepartmentByCode(code);

        assertEquals(department, result);
    }

    @Test
    public void testGetDepartmentByCodeNotFound() {
        when(departmentRepository.findByCode(code)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> departmentService.getDepartmentByCode(code));
    }

    @Test
    public void testAddDepartmentSuccess() throws AlreadyExistsException {
        when(facultyRepository.findById(any())).thenReturn(Optional.of(new Faculty()));
        when(departmentRepository.saveAndFlush(any())).thenReturn(department);

        Department result = departmentService.addDepartment(departmentDTO);

        assertEquals(department, result);
    }

    @Test
    public void testUpdateDepartmentSuccess() throws NotFoundException {
        DepartmentDTO newDepartmentDTO = new DepartmentDTO();
        newDepartmentDTO.setCode(code);
        newDepartmentDTO.setName("Updated " + name);
        newDepartmentDTO.setFacultyAbbreviation("ABC");

        Department updatedDepartment = new Department();
        updatedDepartment.setCode(code);
        updatedDepartment.setName(newDepartmentDTO.getName());

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        when(facultyRepository.findById(any())).thenReturn(Optional.of(new Faculty()));
        when(departmentRepository.saveAndFlush(any())).thenReturn(updatedDepartment);

        Department result = departmentService.updateDepartment(id, departmentDTO);

        assertEquals(updatedDepartment, result);
    }

    @Test
    public void testUpdateDepartmentNotFound() {
        DepartmentDTO newDepartmentDTO = new DepartmentDTO();
        newDepartmentDTO.setCode(code);
        newDepartmentDTO.setName("Updated " + name);
        newDepartmentDTO.setFacultyAbbreviation("ABC");

        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> departmentService.updateDepartment(id, departmentDTO));
    }

    @Test
    public void testDeleteDepartmentSuccess() throws NotFoundException {
        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        Department result = departmentService.deleteDepartment(id);

        assertEquals(department, result);
    }

    @Test
    public void testDeleteDepartmentNotFound() {
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> departmentService.deleteDepartment(id));
    }
}