import { Employee } from "./Employee";
import { Reservation } from "./Reservation";
import { Program } from "./Program";
import { Student } from "./Student";
import { StudyCycle } from "./StudyCycle";
import { Status, StatusDTO } from "./Status";

export type Thesis = {
    id: number;
    namePL: string;
    nameEN: string;
    descriptionPL: string;
    descriptionEN: string;
    num_people: number;
    supervisor: Employee;
    leader: Student | null;
    programs: Program[];
    studyCycle: StudyCycle | null;
    status: Status;
    occupied: number;
    reservations: Reservation[];
  }

export type ThesisFront = {
    id: number;
    namePL: string;
    nameEN: string;
    descriptionPL: string;
    descriptionEN: string;
    num_people: number;
    supervisor: Employee;
    leader: Student | null;
    programs: Program[];
    studyCycle: StudyCycle | null;
    status: StatusDTO;
    occupied: number;
    students: Student[];
    reservations: Reservation[];
}

export type ThesisDTO = {
  namePL: string;
  nameEN: string;
  descriptionPL: string;
  descriptionEN: string;
  num_people: string;
  supervisor: Employee | null;
  // programs: Program[] | null;
  studyField: string;
  specialization: string;
  studyCycle: string;
  status: string;
  occupied: number;
}