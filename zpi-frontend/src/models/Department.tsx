import { Faculty } from "./Faculty";

export type Department = {
    code: string;
    name: string;
    faculty: Faculty;
  }

  export type DepartmentDTO = {
    code: string;
    name: string;
    facultyAbbreviation: string;
  }