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
    thesis_id: number;
    name_pl: string;
    name_ang: string;
    description: string;
    num_people: number;
    status: string;
    faculty: string;
    field: string;
    eduCycle: string;
    supervisor: string;
    leader: string;
}

