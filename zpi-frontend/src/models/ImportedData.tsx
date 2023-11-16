export type ImportedStudent = {
    index: string;
    mail: string;
    name: string;
    programsCycles: Array<string>;
    role: string;
    status: string;
    surname: string;
    source_file_name: string;
}

export type InvalidStudentData = {
    database_repetitions: ImportedStudent[] | null;
    invalid_indices: ImportedStudent[] | null;
    invalid_names: ImportedStudent[] | null;
    invalid_surnames: ImportedStudent[] | null;
    invalid_statuses: ImportedStudent[] | null;
    invalid_programs: ImportedStudent[] | null;
    invalid_cycles: ImportedStudent[] | null;
    invalid_data: ImportedStudent[] | null;
};

export type ImportedEmployee = {
    email: string;
    name: string;
    surname: string;
    roles: string;
    department: string;
    faculty: string;
    title: string;
    phone_number: string;
    position: string;
    source_file_name: string;
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