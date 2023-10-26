import { Department } from "./Department";

export type Employee = {
    mail: string;
    name: string;
    surname: string;
    role: string;
    department: Department;
    title: string;
  }