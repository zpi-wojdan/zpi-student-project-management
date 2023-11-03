import { Program } from "./Program";
import { Student } from "./Student";
import { StudyCycle } from "./StudyCycle";

export type StudentProgramCycleId = {
    studentMail: string;
    programId: number;
    cycleId: number;
  }
  
export type StudentProgramCycle = {
    id: StudentProgramCycleId;
    student: Student;
    program: Program;
    cycle: StudyCycle;
}

export type StudentProgramCycleDTO = {
  programId: number;
  cycleId: number;
}