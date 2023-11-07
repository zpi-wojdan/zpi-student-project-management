import { Faculty } from "./Faculty";

export type Department = {
  id: number;
  code: string;
  name: string;
  faculty: Faculty;
}

export type DepartmentDTO = {
  code: string;
  name: string;
  facultyAbbreviation: string;
}