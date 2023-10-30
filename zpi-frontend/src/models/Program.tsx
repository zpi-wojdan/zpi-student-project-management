import { Specialization } from "./Specialization";
import { StudyField } from "./StudyField";
import { StudyCycle } from "./StydyCycle";

export type Program = {
    id: number;
    name: string;
    studyField: StudyField;
    specialization: Specialization;
    studyCycles: StudyCycle[];
  }