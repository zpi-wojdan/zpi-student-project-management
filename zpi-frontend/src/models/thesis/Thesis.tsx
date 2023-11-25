import { Employee } from "../user/Employee";
import { Reservation } from "./Reservation";
import { Program } from "../university/Program";
import { Student } from "../user/Student";
import { StudyCycle } from "../university/StudyCycle";
import { Status, StatusDTO } from "./Status";
import { Comment } from './Comment'

export type Thesis = {
    id: number;
    namePL: string;
    nameEN: string;
    descriptionPL: string;
    descriptionEN: string;
    numPeople: number;
    supervisor: Employee;
    leader: Student | null;
    programs: Program[];
    studyCycle: StudyCycle | null;
    status: Status;
    occupied: number;
    reservations: Reservation[];
    comments: Comment[];
}

export type ThesisFront = {
  id: number;
  namePL: string;
  nameEN: string;
  descriptionPL: string;
  descriptionEN: string;
  numPeople: number;
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
  numPeople: number;
  supervisorId: number; 
  programIds: number[];
  studyCycleId: number | null;
  statusId: number;
}
