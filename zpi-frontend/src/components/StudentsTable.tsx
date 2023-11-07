import React, { useEffect, useState } from 'react';
import Cookies from "js-cookie";
import { Employee } from '../models/Employee';
import { Student } from '../models/Student';
import { ThesisFront } from '../models/Thesis';
import {useTranslation} from "react-i18next";

type StudentTableProps = {
  students: Student[];
  thesis: ThesisFront;
  role: string;
}

function StudentTable({ students, thesis, role }: StudentTableProps) {
  const [user, setUser] = useState<Student & Employee>();
  const [showButtons, setShowButtons] = useState<boolean[]>(students.map((s) => false));
  const { i18n, t } = useTranslation();

  const handleClick = () => {
    console.log("clicked")
  }

  useEffect(() => {
    setUser(JSON.parse(Cookies.get("user") || "{}"));
    whichButtonsToShow();
  }, []);

  const whichButtonsToShow = () => {
    if (role === "student") {
      if (user?.mail === thesis.leader?.mail) {
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
          <th scope="col">{t('general.people.fullName')}</th>
          <th scope="col">{t('general.people.index')}</th>
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