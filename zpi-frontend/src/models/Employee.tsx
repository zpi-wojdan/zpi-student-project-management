import { Department } from "./Department";
import { Role, RoleDTO } from "./Role";
import { Title, TitleDTO } from "./Title";

export type Employee = {
  id: number;
  mail: string;
  name: string;
  surname: string;
  roles: Role[];
  department: Department;
  title: Title;
}

export type EmployeeDTO = {
  mail: string;
  name: string;
  surname: string;
  roles: RoleDTO[];
  departmentCode: string;
  title: TitleDTO;
}