
import { Department } from "./Department";
import { Program } from "./Program";
import { StudyField } from "./StudyField";

export type Faculty = {
  id: number;
  abbreviation: string;
  name: string;
  departments: Department[];
}