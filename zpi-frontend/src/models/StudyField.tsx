import { Faculty } from "./Faculty";

export type StudyField = {
    abbreviation: string;
    name: string;
  }

export type StudyFieldDTO = {
  abbreviation: string;
  name: string;
  facultyAbbr: string;
}