import { StudyField } from "./StudyField";

export type Specialization = {
  abbreviation: string;
  name: string;
  studyField: StudyField | undefined;
}