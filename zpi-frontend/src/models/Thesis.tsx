import { Employee } from "./Employee";
import { Reservation } from "./Reservation";
import { Program } from "./Program";
import { Student } from "./Student";
import { StudyCycle } from "./StudyCycle";

export type Thesis = {
    id: number;
    namePL: string;
    nameEN: string;
    description: string;
    num_people: number;
    supervisor: Employee;
    leader: Student | null;
    programs: Program[];
    studyCycle: StudyCycle | null;
    status: string;
    occupied: number;
    reservations: Reservation[];
  }

export type ThesisFront = {
    id: number;
    namePL: string;
    nameEN: string;
    description: string;
    num_people: number;
    supervisor: Employee;
    leader: Student | null;
    programs: Program[];
    studyCycle: StudyCycle | null;
    status: string;
    occupied: number;
    students: Student[];
    reservations: Reservation[];
}