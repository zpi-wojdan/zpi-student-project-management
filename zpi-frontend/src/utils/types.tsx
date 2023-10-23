export type SupervisorData = {
    mail?: string;
    name?: string;
    surname?: string;
    role?: string;
    department_symbol?: string;
    title?: string;
}
  
export type AddUpdateThesisProps = {
    role?: string;
    mail?: string;
}

export enum StatusEnum {
    OPEN = 'Open',
    CLOSED = 'Closed',
    TO_BE_REVIEWED = 'To be reviewed',
    FULL = 'Full'
}