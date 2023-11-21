import { Program } from "./university/Program";
import { Student } from "./user/Student";
import { StudyCycle } from "./university/StudyCycle";

export type StudentProgramCycleId = {
  studentId: number;
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