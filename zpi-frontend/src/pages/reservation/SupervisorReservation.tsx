import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import { Student } from '../../models/user/Student';
import { Thesis } from '../../models/thesis/Thesis';
import {useTranslation} from "react-i18next";
import api from "../../utils/api";

type SupervisorReservationProps = {
}

function SupervisorReservationPage({ }: SupervisorReservationProps) {
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const { i18n, t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();
    const thesis = location.state?.thesis as Thesis;

    const [reservations, setReservations] = useState<string[]>(Array(thesis?.numPeople || 0).fill(""));
    const [errors, setErrors] = useState<boolean[]>([]);
    const [doubles, setDoubles] = useState<boolean[]>([]);
    const [students, setStudents] = useState<Student[]>([]);
    const [user, setUser] = useState<Student>();

    useEffect(() => {
        setUser(JSON.parse(Cookies.get("user") || "{}"));
        reservations[0] = user?.index || "";
        setReservations(reservations);
    }, []);

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
        const newStudents = [...students];

        if (reservation === "") {
            newStudents[index] = {} as Student;
            setStudents(newStudents);
            newErrors[index] = false;
            setErrors(newErrors);
            return;
        }

        if (!isReservationValid(index, reservation)) {
            console.error(`Invalid reservation number: ${reservation}`);
            newStudents[index] = {} as Student;
            setStudents(newStudents);
            newErrors[index] = true;
            setErrors(newErrors);
            return;
        } else {
            newErrors[index] = false;
        }

        await api.get(`http://localhost:8080/student/index/${reservation}`)
            .then(response => {
                newStudents[index] = response.data as Student;
            })
            .catch(error => {
                newStudents[index] = {} as Student;
                newErrors[index] = true;
                if (error.response.status === 401 || error.response.status === 403) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            })
        setStudents(newStudents);
        setErrors(newErrors);
    };

    const handleSubmit = async (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
        e.preventDefault();
        if (reservations.every((reservation, index) => isReservationValid(index, reservation))) {
            let allReservationsSuccessful = true;

            for (const reservation of reservations) {
                const responseBody = {
                    thesisId: thesis.id,
                    student: students.find(student => student.index === reservation),
                    reservationDate: new Date(),
                    readyForApproval: true,
                    confirmedByLeader: true,
                    confirmedBySupervisor: true,
                    confirmedByStudent: true,
                };
                console.log(JSON.stringify(responseBody));

                const response = await api.post("http://localhost:8080/reservation", JSON.stringify(responseBody))
                    .then(response => {
                        if (response.status === 201) {
                            console.log(`Reservation ${reservation} created successfully`);
                        }
                    })
                    .catch(error => {
                        console.error(`Failed to submit reservation ${reservation}`);
                        console.error(error)
                        allReservationsSuccessful = false;
                        if (error.response.status === 401 || error.response.status === 403) {
                            setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                            handleSignOut(navigate);
                        }
                    });
            }

            if (allReservationsSuccessful) {
                toast.success(t('reservation.reservationSuccessful'));
                navigate("/theses/" + thesis.id)
            } else {
                toast.error(t('reservation.reservationError'));
            }
        } else {
            for (const reservation of reservations) {
                handleReservationBlur(reservations.indexOf(reservation));
            }
            console.error("Invalid reservation numbers");
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
                {reservations.map((reservation, index) => (
                    <div key={index} className="form-group row justify-content-center">
                        <label htmlFor={`reservation-${index}`} className="col-sm-2 col-form-label">
                            {t('general.people.student')} {index + 1}:</label>
                        <div className="col-sm-4 d-flex">
                            <input
                                id={`reservation-${index}`}
                                type="text"
                                className={`form-control ${errors[index] ? "is-invalid" : ""}`}
                                value={reservation}
                                onChange={(e) => handleReservationChange(index, e.target.value)}
                                onBlur={() => handleReservationBlur(index)}
                                placeholder={t('general.people.index')}
                            />
                        </div>
                        <div className="col-sm-6">
                            <p className={errors[index] ? "col-form-label text-danger" : "col-form-label"}>
                                {students[index] && students[index].name !== undefined ?
                                    students[index].name + ' ' + students[index].surname
                                    : (errors[index] ?
                                        (doubles[index] ? t('reservation.indexUsedInAnotherRow') :
                                            t('reservation.wrongIndex')
                                        ) : ''
                                    )}
                            </p>
                        </div>

                    </div>
                ))}
            </form>
        </div>
    );

}

export default SupervisorReservationPage