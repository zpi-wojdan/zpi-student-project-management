export type Student = {
    mail: string;
    name: string;
    surname: string;
    index: string;
    program: string;
    teachingCycle: string;
    stage: string;
    admissionDate: Date;
    role: string;
    status: string;
}

export type Thesis = {
    id: number;
    namePL: string;
    nameEN: string;
    description: string;
    num_people: number;
    status: string;
    faculty: string;
    field: string;
    eduCycle: string;
    supervisor: Employee;
    leader: string;
    occupied: number;
    students: Student[];
    reservations: Reservation[];
}

export type ThesisDB = {
    id: number;
    namePL: string;
    nameEN: string;
    description: string;
    num_people: number;
    status: string;
    faculty: string;
    field: string;
    eduCycle: string;
    supervisor: Employee;
    leader: string;
    occupied: number;
    reservations: Reservation[];
}

export type Reservation = {
    thesis: Thesis;
    student: Student;
    reservationDate: Date;
    id: number;
    isConfirmedByLeader: boolean;
    isConfirmedBySupervisor: boolean;
    isConfirmedByStudent: boolean;
    isReadyForApproval: boolean;
}

export type Employee = {
    mail: string;
    name: string;
    surname: string;
    departament_symbol: string;
    title: string;
    role: string;
}
