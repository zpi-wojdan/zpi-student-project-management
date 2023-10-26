import { Program } from "./Program";
import { StudyCycle } from "./StydyCycle";

export type Student = {
    mail: string;
    name: string;
    surname: string;
    index: string;
    program: string;
    teaching_cycle: string;
    status: string;
    role: string;
    programs: Program[];
    studyCycles: StudyCycle[];
  }