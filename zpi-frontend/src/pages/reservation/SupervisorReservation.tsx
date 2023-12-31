import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import { Student } from '../../models/user/Student';
import { useTranslation } from "react-i18next";
import api from "../../utils/api";
import api_access from '../../utils/api_access';

type SupervisorReservationProps = {
    numPeople: number;
    studentIndexes: string[];
    setStudentIndexes: (indexes: string[]) => void;
}

function SupervisorReservationPage({ numPeople, studentIndexes, setStudentIndexes }: SupervisorReservationProps) {
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const { i18n, t } = useTranslation();
    const navigate = useNavigate();

    const calculateArraySize = (numPeople: number | undefined) => {
        if (numPeople){ 
            if (numPeople < 3) {
                return 3;
            } 
            else if (numPeople > 5) {
                return 5;
            } else {
                return numPeople;
            }
        }
        return 0;
    };

    const [reservations, setReservations] = useState<string[]>(Array(calculateArraySize(numPeople)).fill(""));
    const [errors, setErrors] = useState<boolean[]>([]);
    const [doubles, setDoubles] = useState<boolean[]>([]);
    const [students, setStudents] = useState<Student[]>([]);
    const [showList, setShowList] = useState<boolean>(false);

    useEffect(() => {
        const updatedReservations = [...reservations].slice(0, numPeople);
        studentIndexes?.slice(0, numPeople).forEach((v, i) => {
            updatedReservations[i] = v;
        });
        setReservations(updatedReservations);
        setStudentIndexes(updatedReservations);
    }, [numPeople]);

    const handleReservationChange = (index: number, value: string) => {
        const updatedReservations = [...reservations];
        updatedReservations[index] = value;
        setReservations(updatedReservations);
        setStudentIndexes(updatedReservations);
    };

    const isReservationValid = (index: number, reservation: string) => {
        if (!/^[0-9]{6}$/.test(reservation)) {
            return false;
        }

        const otherReservations = reservations.filter((_, i) => i !== index);
        if (otherReservations.includes(reservation)) {
            const updatedDoubles = [...doubles];
            updatedDoubles[index] = true;
            setDoubles(updatedDoubles);
            return false;
        }
        const updatedDoubles = [...doubles];
        updatedDoubles[index] = false;
        setDoubles(updatedDoubles);
        return true;
    };

    const handleReservationBlur = async (index: number) => {
        const reservation = reservations[index];
        const newErrors = [...errors];
        const newStudents = [...students];

        if (reservation === "") {
            newStudents[index] = {} as Student;
            setStudents(newStudents);
            newErrors[index] = false;
            setErrors(newErrors);
            return;
        }

        if (!isReservationValid(index, reservation)) {
            newStudents[index] = {} as Student;
            setStudents(newStudents);
            newErrors[index] = true;
            setErrors(newErrors);
            return;
        } else {
            newErrors[index] = false;
        }

        await api.get(api_access + `student/index/${reservation}`)
            .then(response => {
                newStudents[index] = response.data as Student;
            })
            .catch(error => {
                newStudents[index] = {} as Student;
                newErrors[index] = true;
                if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            })
        setStudents(newStudents);
        setErrors(newErrors);
        setStudentIndexes(reservations);
    };

    useEffect(() => {
        const emptyCount = reservations.filter((value) => value === "").length;
        if (!showList) {
          if (reservations.length === emptyCount) {
            setReservations([]);
          }
        } else {
            if (reservations.length === 0){
                setReservations(Array(calculateArraySize(numPeople)).fill(""));
            }
        }
      }, [showList]);      

    return (
        <div className="page-margin">
            <label className="bold" onClick={() => setShowList(!showList)}>
                {t('general.people.students')} {showList ? '▼' : '▶'}
            </label>
            {showList &&
                <table className="table table-borderless">
                    <thead>
                        <tr>
                            <th scope="col" style={{ width: '10%' }}></th>
                            <th scope="col" style={{ width: '30%' }}></th>
                            <th scope="col" style={{ width: '60%' }}></th>
                        </tr>
                    </thead>
                    <tbody>
                        {reservations.map((reservation, index) => (
                            <tr key={index}>
                                <td style={{ whiteSpace: "nowrap" }}>
                                    <label htmlFor={`reservation-${index}`} className="col-form-label">
                                        {t('general.people.student')} {index + 1}:
                                    </label>
                                </td>
                                <td>
                                    <input
                                        id={`reservation-${index}`}
                                        type="text"
                                        className={`form-control ${errors[index] ? "is-invalid" : ""}`}
                                        value={reservation}
                                        onChange={(e) => handleReservationChange(index, e.target.value)}
                                        onBlur={() => handleReservationBlur(index)}
                                        placeholder={t('general.people.index')}
                                    />
                                </td>
                                <td>
                                    <p className={errors[index] ? "col-form-label text-danger" : "col-form-label mb-0"}>
                                        {students[index] && students[index].name !== undefined ?
                                            students[index].name + ' ' + students[index].surname
                                            : (errors[index] ?
                                                (doubles[index] ? t('reservation.indexUsedInAnotherRow') :
                                                    t('reservation.wrongIndex')
                                                ) : ''
                                            )}
                                    </p>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            }
            <div className="text-info">
                {t('thesis.addStudentsMessage')}
            </div>
        </div>
    );

}

export default SupervisorReservationPage