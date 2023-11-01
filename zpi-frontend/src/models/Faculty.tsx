
import { Department } from "./Department";
import { Program } from "./Program";
import { StudyField } from "./StudyField";

export type Faculty = {
    abbreviation: string;
    name: string;
    studyFields: StudyField[];
    programs: Program[];
    departments: Department[];
  }