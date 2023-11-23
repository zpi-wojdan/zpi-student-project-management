import React, { useEffect, useState } from 'react';
import Cookies from "js-cookie";
import { Employee } from '../models/user/Employee';
import { Student } from '../models/user/Student';
import { ThesisFront } from '../models/thesis/Thesis';
import { useTranslation } from "react-i18next";
import api from '../utils/api';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import handleSignOut from "../auth/Logout";
import useAuth from "../auth/useAuth";
import { Reservation } from '../models/thesis/Reservation';
import DeleteConfirmation from './DeleteConfirmation';

type StudentTableProps = {
  students: Student[];
  thesis: ThesisFront;
}

function StudentTable({ students, thesis }: StudentTableProps) {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();

  const [showButtons, setShowButtons] = useState<boolean[]>(students.map((s) => false));
  const [showButtonDelete, setShowButtonDelete] = useState<boolean[]>(students.map((s) => false));
  const [showButtonsSupervisor, setShowButtonsSupervisor] = useState<boolean>(false);

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState<boolean>(false);
  const [reservationToDelete, setReservationToDelete] = useState<Reservation>(thesis.reservations[0] as Reservation);

  const handleDeleteClick = (reservation: Reservation) => {
    setShowDeleteConfirmation(true);
    setReservationToDelete(reservation);
  };

  const handleAcceptReservation = (reservation: Reservation) => {
    reservation.confirmedByLeader = true;
    reservation.confirmedByStudent = true;
    api.put(`http://localhost:8080/reservation/${reservation.id}`, reservation)
      .then((response) => {
        if (response.status === 200) {
          toast.success(t('reservation.deleteSuccessful'));
          window.location.reload();
        }
      })
      .catch((error) => {
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        toast.error(t('department.deleteError'));
      });
  }

  function handleCancelDelete(): void {
    setShowDeleteConfirmation(false);
  }

  function handleConfirmDelete(): void {
    api.delete(`http://localhost:8080/reservation/${reservationToDelete.id}`)
      .then((response) => {
        if (response.status === 200) {
          toast.success(t('reservation.reservationDeleted'));
          window.location.reload();
        }
      })
      .catch((error) => {
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }

  useEffect(() => {
    const userCookies = JSON.parse(Cookies.get("user") || "{}");
    whichButtonsToShow(userCookies);
  }, []);

  const whichButtonsToShow = (user: Student & Employee) => {
    const indexLeader = students.findIndex((stu) => stu.id === thesis.leader?.id)
    if (user) {
      if (user.role && user.role.name === "student") {
        if (user.mail === thesis.leader?.mail) {
          let newShowButtons = showButtons.map((s, i) => !thesis.reservations[i].confirmedByLeader);
          newShowButtons[indexLeader] = false;
          setShowButtons(newShowButtons);
        } else {
          let newShowButtons = showButtons.map((s, i) => user?.index === students[i].index && !thesis.reservations[i].confirmedByStudent);
          setShowButtons(newShowButtons);
        }

        const newShowButtonDelete = [...showButtonDelete];
        const thisStudent = students.find((stu) => stu.mail === user.mail);
        if (thisStudent && thesis.reservations.every((res) => !res.readyForApproval)) {
          newShowButtonDelete[students.findIndex((stu) => stu === thisStudent)] = true;
        }
        setShowButtonDelete(newShowButtonDelete)

      } else if (user.roles && user.roles.some(role => role.name === "supervisor") && user.mail === thesis.supervisor.mail) {
        const newShowButtonsSupervisor = thesis.reservations.every((res) => res.readyForApproval);
        setShowButtonsSupervisor(newShowButtonsSupervisor);
      } else {
        setShowButtons(showButtons.map((s) => false));
      }
    }
  }

  return (
    <table className="custom-table">
      <thead>
        <tr>
          <th style={{ width: '5%', textAlign: 'center' }} scope="col">#</th>
          <th style={{ width: '40%' }} scope="col">{t('general.people.fullName')}</th>
          <th style={{ width: '30%' }} scope="col">{t('general.people.index')}</th>
          {showButtons.some((s) => s) ? (
            <th style={{ width: '5%', textAlign: 'center' }} scope="col">{t('general.management.accept')}</th>
          ) : (
            <></>
          )}
          {showButtonDelete.some((s) => s) ? (
            <th style={{ width: '5%', textAlign: 'center' }} scope="col">{t('general.management.annul')}</th>
          ) : (
            <></>
          )}

        </tr>
      </thead>
      <tbody>
        {students.map((student, index) => (
          <>
            <tr key={index}>
              <td className='centered'>{index + 1}</td>
              <td>{student.name + ' ' + student.surname}</td>
              <td>{student.index}</td>
              {showButtons.some((s) => s) ? (
                showButtons[index] ? (
                  <td className="centered">
                    <button type="button" className="coverall custom-button" onClick={() => handleAcceptReservation(thesis.reservations.find((res) => res.student === student) as Reservation)}>
                      <i className='bi bi-check-lg'></i>
                    </button>
                  </td>
                ) : (
                  <td><span></span></td>
                )
              ) : (
                <></>
              )}
              {showButtonDelete.some((s) => s) ? (
                showButtonDelete[index] ? (
                  <td className='centered'>
                    <button type="button" className="custom-button coverall another-color" onClick={() => handleDeleteClick(thesis.reservations.find((res) => res.student === student) as Reservation)}>
                      <i className="bi bi-trash"></i>
                    </button>
                  </td>
                ) : (
                  <td><span></span></td>
                )
              ) : (
                <></>
              )}

            </tr>
            {
              reservationToDelete.id === thesis.reservations.find((res) => res.student === student)?.id && showDeleteConfirmation && (
                <tr>
                  <td colSpan={5}>
                    <DeleteConfirmation
                      isOpen={showDeleteConfirmation}
                      onClose={handleCancelDelete}
                      onConfirm={handleConfirmDelete}
                      onCancel={handleCancelDelete}
                      questionText={t('reservation.deleteConfirmation')}
                    />
                  </td>
                </tr>
              )
            }
          </>
        ))}
      </tbody>
    </table>
  );
};

export default StudentTable;