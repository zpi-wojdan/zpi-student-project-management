import { Department } from "./Department";
import { Role } from "./Role";

export type Employee = {
    mail: string;
    name: string;
    surname: string;
    roles: Role[];
    department: Department;
    title: string;
  }