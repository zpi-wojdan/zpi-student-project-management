import { Specialization } from "./Specialization";
import { StudyField } from "./StudyField";
import { StudyCycle } from "./StudyCycle";

export type Program = {
  id: number;
  name: string;
  studyField: StudyField;
  specialization: Specialization;
  studyCycles: StudyCycle[];
}

export type ProgramDTO = {
  name: string;
  studyFieldAbbr: string;
  specializationAbbr: string;
  studyCyclesId: number[];
}