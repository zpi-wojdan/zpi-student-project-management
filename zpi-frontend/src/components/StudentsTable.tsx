import React, { useEffect, useState } from 'react';
import { Employee, Student, Thesis } from '../models/Models';
import Cookies from "js-cookie";

type StudentTableProps = {
  students: Student[];
  thesis: Thesis;
  role: string;
}

function StudentTable({ students, thesis, role }: StudentTableProps) {
  const [user, setUser] = useState<Student & Employee>();
  const [showButtons, setShowButtons] = useState<boolean[]>(students.map((s) => false));

  const handleClick = () => {
    console.log("clicked")
  }

  useEffect(() => {
    setUser(JSON.parse(Cookies.get("user") || "{}"));
    whichButtonsToShow();
  }, []);

  const whichButtonsToShow = () => {
    if (role === "student") {
      if (user?.mail === thesis.leader) {
        let newShowButtons = showButtons.map((s, i) => !thesis.reservations[i].confirmedByLeader);
        newShowButtons[0] = false;
        setShowButtons(newShowButtons);
      } else {
        let newShowButtons = showButtons.map((s, i) => user?.index === students[i].index && !thesis.reservations[i].confirmedByStudent);
        setShowButtons(newShowButtons);
      }
    } else if (role === "supervisor" && user?.mail === thesis.supervisor.mail) {
      let newShowButtons = showButtons.map((s, i) => !thesis.reservations[i].confirmedBySupervisor);
      setShowButtons(newShowButtons);
    } else {
      setShowButtons(showButtons.map((s) => false));
    }
  }


  return (
    <table className="table">
      <thead>
        <tr>
          <th scope="col">#</th>
          <th scope="col">Imię i Nazwisko</th>
          <th scope="col">Indeks</th>
          <th scope="col"></th>
        </tr>
      </thead>
      <tbody>
        {students.map((student, index) => (
          <tr key={index}>
            <td>{index + 1}</td>
            <td>{student.name + ' ' + student.surname}</td>
            <td>{student.index}</td>
            <td>
              {showButtons[index] ? (
                <div className="row">
                  <button type="button" className="btn btn-success btn-sm col-sm-6" onClick={handleClick}>
                    V
                  </button>
                  <button type="button" className="btn btn-danger btn-sm col-sm-6">
                    X
                  </button>
                </div>
              ) : (
                <></>
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default StudentTable;