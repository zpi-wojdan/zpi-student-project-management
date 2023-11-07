import { StudyField } from "./StudyField";

export type Specialization = {
  id: number;
  abbreviation: string;
  name: string;
  studyField: StudyField;
}