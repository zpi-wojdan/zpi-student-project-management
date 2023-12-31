import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import { Student } from '../../models/user/Student';
import { Thesis } from '../../models/thesis/Thesis';
import api from '../../utils/api';
import { useTranslation } from "react-i18next";
import api_access from '../../utils/api_access';
import { Program } from '../../models/university/Program';

type ReservationProps = {
    admin: boolean;
}

function ReservationPage({ admin }: ReservationProps) {
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const { i18n, t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();
    const thesis = location.state?.thesis as Thesis;

    const [reservations, setReservations] = useState<string[]>(admin ? [""] : ["", ""]);
    const [errors, setErrors] = useState<boolean[]>([]);
    const [doubles, setDoubles] = useState<boolean[]>([]);
    const [wrongProgramCycle, setWrongProgramCycle] = useState<boolean[]>([]);
    const [students, setStudents] = useState<Student[]>([]);
    const [user, setUser] = useState<Student>();
    const [showAddButton, setShowAddButton] = useState<boolean>(true);

    useEffect(() => {
        if (!admin) {
            const userCookies = JSON.parse(Cookies.get("user") || "{}");
            setUser(userCookies);
            reservations[0] = userCookies.index || "";
            setReservations(reservations);
            students[0] = userCookies;
            setStudents(students);
        }
    }, []);

    const addReservationInput = () => {
        setReservations([...reservations, ""]);
        if (thesis?.numPeople && reservations.length >= (thesis?.numPeople - thesis?.occupied) - 1) {
            setShowAddButton(false);
        }
    };

    const removeReservationInput = (index: number) => {
        const updatedReservations = [...reservations];
        const updatedErrors = [...errors];
        const updatedStudents = [...students];
        const updatedDoubles = [...doubles];
        const updatedWrongProgramCycle = [...wrongProgramCycle];

        updatedReservations.splice(index, 1);
        updatedErrors.splice(index, 1);
        updatedStudents.splice(index, 1);
        updatedDoubles.splice(index, 1);
        updatedWrongProgramCycle.splice(index, 1);

        setReservations(updatedReservations);
        setErrors(updatedErrors);
        setStudents(updatedStudents);
        setDoubles(updatedDoubles);
        setWrongProgramCycle(updatedWrongProgramCycle);
        setShowAddButton(true);
    };

    const handleReservationChange = (index: number, value: string) => {
        const updatedReservations = [...reservations];
        updatedReservations[index] = value;
        setReservations(updatedReservations);
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
        const newWrongProgramCycle = [...wrongProgramCycle];
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
                if ((response.data as Student).studentProgramCycles.some((spc: any) => thesis.programs.some((p: Program) => p.name === spc.program.name)) &&
                    (response.data as Student).studentProgramCycles.some((spc: any) => thesis.studyCycle?.name === spc.cycle.name)) {
                    newStudents[index] = response.data as Student;
                    newWrongProgramCycle[index] = false;
                } else {
                    newStudents[index] = {} as Student;
                    newWrongProgramCycle[index] = true;
                }
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
        setWrongProgramCycle(newWrongProgramCycle);
    };

    const handleSubmit = async (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
        reservations.forEach((reservation, index) => handleReservationBlur(index));
        e.preventDefault();
        if (reservations.every((reservation, index) => isReservationValid(index, reservation))) {

            const requestBody = reservations.map((reservation) => {
                return {
                    thesisId: thesis.id,
                    student: students.find(student => student.index === reservation),
                    reservationDate: new Date(),
                    confirmedByLeader: true,
                    confirmedByStudent: admin,
                    confirmedBySupervisor: admin,
                };
            });

            const response = await api.post(api_access + "reservation/list", JSON.stringify(requestBody), {
                headers: {
                    'Content-Type': 'application/json'
                }

            })
                .then(response => {
                    if (response.status === 201) {
                        toast.success(t('reservation.reservationSuccessful'));
                        navigate("/public-theses/" + thesis.id)
                    }
                })
                .catch(error => {
                    if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                        setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                        handleSignOut(navigate);
                    }
                    else if (error.response && error.response.status === 409) {
                        toast.error(t('reservation.studentAlreadyReserved', {
                            index: error.response.data.message,
                        }));
                    }
                    else {
                        toast.error(t('reservation.reservationError'));
                    }
                });

        } else {
            for (const reservation of reservations) {
                handleReservationBlur(reservations.indexOf(reservation));
            }
        }
    };

    return (
        <div className="container page-margin">
            <div className="d-flex">
                <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
                    &larr; {t('general.management.goBack')}
                </button>
                <button type="submit" className="custom-button" onClick={handleSubmit}>
                    {t('general.management.reserve')}
                </button>
            </div>
            <h1 className='my-3'>{t('reservation.reservation')}:</h1>
            <h3>{t('general.university.thesis')}: {thesis?.namePL}</h3>
            <form>
                <table className="table table-borderless">
                    <thead>
                        <tr>
                            <th scope="col" style={{ width: '10%' }}></th>
                            <th scope="col" style={{ width: '30%' }}></th>
                            <th scope="col" style={{ width: '5%' }}></th>
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
                                        readOnly={!admin && index === 0}
                                    />
                                </td>
                                <td>
                                    {!admin && index > 1 && (
                                        <button
                                            type="button"
                                            className="btn btn-sm my-1"
                                            onClick={() => removeReservationInput(index)}
                                        >
                                            <i className="bi bi-trash"></i>
                                        </button>
                                    )}
                                    {admin && index > 0 && (
                                        <button
                                            type="button"
                                            className="btn btn-sm my-1"
                                            onClick={() => removeReservationInput(index)}
                                        >
                                            <i className="bi bi-trash"></i>
                                        </button>
                                    )}
                                </td>
                                <td>
                                    <p className={errors[index] || doubles[index] || wrongProgramCycle[index] ? "col-form-label text-danger" : "col-form-label mb-0"}>
                                        {students[index] && students[index].name !== undefined ?
                                            students[index].name + ' ' + students[index].surname
                                            : (errors[index] ?
                                                (doubles[index] ? t('reservation.indexUsedInAnotherRow') :
                                                    t('reservation.wrongIndex')
                                                ) : (wrongProgramCycle[index] ? t('reservation.wrongProgramCycle') : "")
                                            )}
                                    </p>
                                </td>
                            </tr>
                        ))}
                        {showAddButton && (
                            <tr>
                                <td></td>
                                <td>
                                    <button
                                        type="button"
                                        className="custom-button form-control"
                                        onClick={addReservationInput}
                                    >
                                        {t('reservation.addPerson')}
                                    </button>
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </form>
        </div>
    );
}

export default ReservationPage