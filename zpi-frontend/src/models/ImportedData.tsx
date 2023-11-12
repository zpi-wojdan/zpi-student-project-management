import { Student } from "./Student";
import { Employee } from "./Employee";

export type InvalidStudentData = {
    database_repetitions: Student[] | null;
    invalid_indices: Student[] | null;
    invalid_names: Student[] | null;
    invalid_surnames: Student[] | null;
    invalid_statuses: Student[] | null;
    invalid_programs: Student[] | null;
    invalid_cycles: Student[] | null;
    invalid_data: Student[] | null;
};

export type ImportedEmployee = {
    mail: string;
    name: string;
    surname: string;
    roles: string;
    department: string;
    faculty: string;
    title: string;
    phone_number: string;
    position: string;
  }

export type InvalidEmployeeData = {
    database_repetitions: ImportedEmployee[] | null;
    invalid_indices: ImportedEmployee[] | null;
    invalid_academic_titles: ImportedEmployee[] | null;
    invalid_surnames: ImportedEmployee[] | null;
    invalid_names: ImportedEmployee[] | null;
    invalid_units: ImportedEmployee[] | null;
    invalid_subunits: ImportedEmployee[] | null;
    invalid_positions: ImportedEmployee[] | null;
    invalid_phone_numbers: ImportedEmployee[] | null;
    invalid_emails: ImportedEmployee[] | null;
    invalid_data: ImportedEmployee[] | null;
};