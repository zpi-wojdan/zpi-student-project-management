import { StudyField } from "./StudyField";

export type Specialization = {
  id: number;
  abbreviation: string;
  name: string;
  studyField: StudyField;
}

export type SpecializationDTO = {
  abbreviation: string;
  name: string;
  studyFieldAbbr: string;
}