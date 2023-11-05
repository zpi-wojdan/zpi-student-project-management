package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.dto.DepartmentDTO;
import pwr.zpibackend.repositories.university.DepartmentRepository;
import pwr.zpibackend.repositories.university.FacultyRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentByCode(String code) throws NotFoundException {
        return departmentRepository.findById(code).orElseThrow(
                () -> new NotFoundException("Department with code " + code + " does not exist")
        );
    }

    public Department addDepartment(DepartmentDTO department) {
        System.out.println(department.getFacultyAbbreviation());
        Department newDepartment = new Department();
        newDepartment.setCode(department.getCode());
        newDepartment.setName(department.getName());
        newDepartment.setFaculty(facultyRepository.findById(department.getFacultyAbbreviation()).orElse(null));
        return departmentRepository.save(newDepartment);
    }

    public Department deleteDepartment(String code) throws NotFoundException {
        Department department = departmentRepository.findById(code)
                .orElseThrow(NotFoundException::new);
        departmentRepository.delete(department);
        return department;
    }

    public Department updateDepartment(String code, DepartmentDTO updatedDepartment) throws NotFoundException {
        Department existingDepartment = departmentRepository.findById(code)
                .orElseThrow(NotFoundException::new);
        existingDepartment.setName(updatedDepartment.getName());
        existingDepartment.setFaculty(facultyRepository.findById(updatedDepartment.getFacultyAbbreviation()).orElse(null));
        return departmentRepository.save(existingDepartment);
    }
}
