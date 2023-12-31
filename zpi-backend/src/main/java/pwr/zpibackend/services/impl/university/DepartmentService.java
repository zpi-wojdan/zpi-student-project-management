package pwr.zpibackend.services.impl.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.dto.university.DepartmentDTO;
import pwr.zpibackend.repositories.university.DepartmentRepository;
import pwr.zpibackend.repositories.university.FacultyRepository;
import pwr.zpibackend.services.university.IDepartmentService;

import java.util.List;

@Service
@AllArgsConstructor
public class DepartmentService implements IDepartmentService {

    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentByCode(String code) {
        return departmentRepository.findByCode(code).orElseThrow(
                () -> new NotFoundException("Department with code " + code + " does not exist")
        );
    }

    public Department addDepartment(DepartmentDTO department) {
        if (departmentRepository.existsByCode(department.getCode())) {
            throw new AlreadyExistsException("Department with code " + department.getCode() + " already exists");
        }
        System.out.println(department.getFacultyAbbreviation());
        Department newDepartment = new Department();
        newDepartment.setCode(department.getCode());
        newDepartment.setName(department.getName());
        newDepartment.setFaculty(facultyRepository.findByAbbreviation(department.getFacultyAbbreviation()).orElse(null));
        return departmentRepository.saveAndFlush(newDepartment);
    }

    public Department deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department with id " + id + " does not exist"));
        departmentRepository.delete(department);
        return department;
    }

    public Department updateDepartment(Long id, DepartmentDTO updatedDepartment) {
        if (departmentRepository.existsByCode(updatedDepartment.getCode())) {
            if (!departmentRepository.findByCode(updatedDepartment.getCode()).get().getId().equals(id)) {
                throw new AlreadyExistsException("Department with code " + updatedDepartment.getCode() + " already exists");
            }
        }
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department with id " + id + " does not exist"));
        existingDepartment.setCode(updatedDepartment.getCode());
        existingDepartment.setName(updatedDepartment.getName());
        existingDepartment.setFaculty(facultyRepository.findByAbbreviation(updatedDepartment.getFacultyAbbreviation()).orElse(null));
        return departmentRepository.saveAndFlush(existingDepartment);
    }
}
