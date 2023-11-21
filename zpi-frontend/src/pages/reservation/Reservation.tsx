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

type ReservationProps = {
}

function ReservationPage({ }: ReservationProps) {
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const { i18n, t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();
    const thesis = location.state?.thesis as Thesis;

    const [reservations, setReservations] = useState<string[]>(["", ""]);
    const [errors, setErrors] = useState<boolean[]>([]);
    const [doubles, setDoubles] = useState<boolean[]>([]);
    const [students, setStudents] = useState<Student[]>([]);
    const [user, setUser] = useState<Student>();

    useEffect(() => {
        const userCookies = JSON.parse(Cookies.get("user") || "{}");
        setUser(userCookies);
        reservations[0] = userCookies.index || "";
        setReservations(reservations);
    }, []);

    const addReservationInput = () => {
        if (thesis?.numPeople && reservations.length >= thesis?.numPeople) {
            return;
        }
        setReservations([...reservations, ""]);
    };

    const removeReservationInput = (index: number) => {
        const updatedReservations = [...reservations];
        const updatedErrors = [...errors];
        const updatedStudents = [...students];
        const updatedDoubles = [...doubles];

        updatedReservations.splice(index, 1);
        updatedErrors.splice(index, 1);
        updatedStudents.splice(index, 1);
        updatedDoubles.splice(index, 1);

        setReservations(updatedReservations);
        setErrors(updatedErrors);
        setStudents(updatedStudents);
        setDoubles(updatedDoubles);
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
                    confirmedByLeader: true,
                };
                console.log(JSON.stringify(responseBody));

                const response = await api.post("http://localhost:8080/reservation", JSON.stringify(responseBody), {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                
                })
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
                            {index > 1 ? (
                                <button type="button" className="btn btn-sm ml-2" onClick={() => removeReservationInput(index)}>
                                    <span>&times;</span>
                                </button>
                            ) : (
                                <span className="btn btn-sm ml-2" style={{ visibility: "hidden" }}>&times;</span>
                            )}
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

                <div className="row justify-content-center">
                    <div className="col-sm-1"></div>
                    <div className="col-sm-6">
                        <div className="form-group row justify-content-center">
                            <button type="button" className="col-sm-3 m-2 custom-button another-color" onClick={addReservationInput}>
                                {t('reservation.addPerson')}
                            </button>
                        </div>
                    </div>
                    <div className="col-sm-5"></div>
                </div>
            </form>
        </div>
    );

}

export default ReservationPage