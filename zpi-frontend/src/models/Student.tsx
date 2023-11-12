import { Role } from "./Role";
import { StudentProgramCycle, StudentProgramCycleDTO } from "./StudentProgramCycle";

export type Student = {
  id: number;
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
  programsCycles: StudentProgramCycleDTO[];
}