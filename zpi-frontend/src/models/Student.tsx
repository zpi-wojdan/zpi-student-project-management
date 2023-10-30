import { StudentProgramCycle } from "./StudentProgramCycle";

export type Student = {
    mail: string;
    name: string;
    surname: string;
    index: string;
    status: string;
    role: string;
    studentProgramCycles: StudentProgramCycle[];
  }