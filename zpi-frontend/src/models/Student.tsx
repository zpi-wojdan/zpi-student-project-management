import { Role } from "./Role";
import { StudentProgramCycle } from "./StudentProgramCycle";

export type Student = {
    mail: string;
    name: string;
    surname: string;
    index: string;
    status: string;
    role: Role;
    studentProgramCycles: StudentProgramCycle[];
  }