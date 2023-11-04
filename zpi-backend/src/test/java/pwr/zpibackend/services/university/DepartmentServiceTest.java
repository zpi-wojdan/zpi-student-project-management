package pwr.zpibackend.services.university;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.dto.DepartmentDTO;
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
    private final String code = "123";
    private final String name = "Test Department";

    @BeforeEach
    public void setUp() {
        departmentDTO = new DepartmentDTO();
        departmentDTO.setCode(code);
        departmentDTO.setName(name);
        departmentDTO.setFacultyAbbreviation("ABC");

        department = new Department();
        department.setCode(code);
        department.setName(name);
    }

    @Test
    public void testGetDepartmentByCodeSuccess() throws NotFoundException {
        when(departmentRepository.findById(code)).thenReturn(Optional.of(department));

        Department result = departmentService.getDepartmentByCode(code);

        assertEquals(department, result);
    }

    @Test
    public void testGetDepartmentByCodeNotFound() {
        when(departmentRepository.findById(code)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> departmentService.getDepartmentByCode(code));
    }

//    @Test
//    public void testAddDepartmentSuccess() {
//        when(facultyRepository.findById(any())).thenReturn(Optional.of(new Faculty()));
//        when(departmentRepository.save(any())).thenReturn(department);
//
//        Department result = departmentService.addDepartment(departmentDTO);
//
//        assertEquals(department, result);
//    }

    @Test
    public void testUpdateDepartmentSuccess() throws NotFoundException {
        DepartmentDTO newDepartmentDTO = new DepartmentDTO();
        newDepartmentDTO.setCode(code);
        newDepartmentDTO.setName("Updated " + name);
        newDepartmentDTO.setFacultyAbbreviation("ABC");

        Department updatedDepartment = new Department();
        updatedDepartment.setCode(code);
        updatedDepartment.setName(newDepartmentDTO.getName());

        when(departmentRepository.findById(code)).thenReturn(Optional.of(department));
        when(facultyRepository.findById(any())).thenReturn(Optional.of(new Faculty()));
        when(departmentRepository.save(any())).thenReturn(updatedDepartment);

        Department result = departmentService.updateDepartment(code, departmentDTO);

        assertEquals(updatedDepartment, result);
    }

    @Test
    public void testUpdateDepartmentNotFound() {
        DepartmentDTO newDepartmentDTO = new DepartmentDTO();
        newDepartmentDTO.setCode(code);
        newDepartmentDTO.setName("Updated " + name);
        newDepartmentDTO.setFacultyAbbreviation("ABC");

        when(departmentRepository.findById(code)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> departmentService.updateDepartment(code, departmentDTO));
    }

    @Test
    public void testDeleteDepartmentSuccess() throws NotFoundException {
        when(departmentRepository.findById(code)).thenReturn(Optional.of(department));

        Department result = departmentService.deleteDepartment(code);

        assertEquals(department, result);
    }

    @Test
    public void testDeleteDepartmentNotFound() {
        when(departmentRepository.findById(code)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> departmentService.deleteDepartment(code));
    }
}