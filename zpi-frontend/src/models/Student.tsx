import { Role } from "./Role";
import { StudentProgramCycle, StudentProgramCycleDTO } from "./StudentProgramCycle";

export type Student = {
    mail: string;
    name: string;
    surname: string;
    index: string;
    status: string;
    role: Role;
    studentProgramCycles: StudentProgramCycle[];
  }

export type StudentDTO = {
  mail: string;
  name: string;
  surname: string;
  index: string;
  status: string;
  studentProgramCycles: StudentProgramCycleDTO[];
}