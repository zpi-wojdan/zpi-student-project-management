import { Department } from "./Department";
import { Role, RoleDTO } from "./Role";

export type Employee = {
    mail: string;
    name: string;
    surname: string;
    roles: Role[];
    department: Department;
    title: string;
  }

  export type EmployeeDTO = {
  mail: string;
  name: string;
  surname: string;
  roles: RoleDTO[];
  departmentCode: string;
  title: string;
}