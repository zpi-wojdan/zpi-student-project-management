import { Specialization } from "./Specialization";
import { StudyField } from "./StudyField";
import { StudyCycle } from "./StudyCycle";
import { Faculty } from "./Faculty";

export type Program = {
  id: number;
  name: string;
  studyField: StudyField;
  specialization: Specialization;
  studyCycles: StudyCycle[];
  faculty: Faculty;
}

export type ProgramDTO = {
  name: string;
  studyFieldAbbr: string;
  specializationAbbr: string;
  studyCyclesId: number[];
  facultyId: number;
}