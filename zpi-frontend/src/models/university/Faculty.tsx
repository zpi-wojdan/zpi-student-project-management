
import { Department } from "./Department";
import { Program } from "./Program";
import { StudyField } from "./StudyField";

export type Faculty = {
  id: number;
  abbreviation: string;
  name: string;
}

export type FacultyDTO = {
  abbreviation: string;
  name: string;
}