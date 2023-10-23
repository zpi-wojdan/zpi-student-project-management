import React from 'react';
import { Student, Thesis } from '../models/Models';

type StudentTableProps = {
    students: Student[];
    thesis: Thesis;
}

function StudentTable({ students }: StudentTableProps) {
    return (
      <table className="table">
        <thead>
          <tr>
            <th scope="col">#</th>
            <th scope="col">Imię i Nazwisko</th>
            <th scope="col">Indeks</th>
          </tr>
        </thead>
        <tbody>
          {students.map((student, index) => (
            <tr key={index}>
              <td>{index}</td>
              <td>{student.name + ' ' + student.surname}</td>
              <td>{student.index}</td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  };
  
  export default StudentTable;