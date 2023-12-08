package pwr.zpibackend.services.university;

import pwr.zpibackend.dto.university.DepartmentDTO;
import pwr.zpibackend.models.university.Department;

import java.util.List;

public interface IDepartmentService {
    List<Department> getAllDepartments();
    Department getDepartmentByCode(String code);
    Department addDepartment(DepartmentDTO department);
    Department deleteDepartment(Long id);
    Department updateDepartment(Long id, DepartmentDTO updatedDepartment);

}
