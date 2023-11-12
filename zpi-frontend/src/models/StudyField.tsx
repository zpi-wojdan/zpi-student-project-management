import { Faculty } from "./Faculty";

export type StudyField = {
  id: number;
  abbreviation: string;
  name: string;
  faculty: Faculty;
}

export type StudyFieldDTO = {
  abbreviation: string;
  name: string;
  facultyAbbr: string;
}