import { Student } from "./Student";
import { Thesis } from "./Thesis";

export type Reservation = {
    thesis: Thesis;
    student: Student;
    reservationDate: Date;
    sentForApprovalDate: Date;
    id: number;
    confirmedByLeader: boolean;
    confirmedBySupervisor: boolean;
    confirmedByStudent: boolean;
    readyForApproval: boolean;
}