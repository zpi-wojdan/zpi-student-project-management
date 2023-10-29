import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { Student, Thesis } from '../../models/Models';
import { useLocation, useNavigate } from 'react-router-dom';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';

type SupervisorReservationProps = {
}

function SupervisorReservationPage({ }: SupervisorReservationProps) {
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const thesis = location.state?.thesis as Thesis;

    const [reservations, setReservations] = useState<string[]>(Array(thesis?.num_people || 0).fill(""));
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

        await axios.get(`http://localhost:8080/student/${reservation}@student.pwr.edu.pl`)
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
                    confirmedBySupervisor: true,
                    confirmedByStudent: true,
                };
                console.log(JSON.stringify(responseBody));

                const response = await axios.post("http://localhost:8080/reservation", JSON.stringify(responseBody), {
                    headers: {
                        "Content-Type": "application/json",
                    },
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
                toast.success("Rezerwacja zakończona pomyślnie!");
                navigate("/theses/" + thesis.id)
            } else {
                toast.error("Rezerwacja nie powiodła się!");
            }
        } else {
            for (const reservation of reservations) {
                handleReservationBlur(reservations.indexOf(reservation));
            }
            console.error("Invalid reservation numbers");
        }
    };

    return (
        <div className="container">
            <button type="button" className="btn btn-secondary m-2" onClick={() => navigate(-1)}>
                &larr; Powrót
            </button>
            <h1>Rezerwacja tematu:</h1>
            <h3>Temat: {thesis?.namePL}</h3>
            <form>
                {reservations.map((reservation, index) => (
                    <div key={index} className="form-group row justify-content-center">
                        <label htmlFor={`reservation-${index}`} className="col-sm-2 col-form-label">Student {index + 1}:</label>
                        <div className="col-sm-4 d-flex">
                            <input
                                id={`reservation-${index}`}
                                type="text"
                                className={`form-control ${errors[index] ? "is-invalid" : ""}`}
                                value={reservation}
                                onChange={(e) => handleReservationChange(index, e.target.value)}
                                onBlur={() => handleReservationBlur(index)}
                                placeholder="Indeks"
                            />
                        </div>
                        <div className="col-sm-6">
                            <p className={errors[index] ? "col-form-label text-danger" : "col-form-label"}>
                                {students[index] && students[index].name !== undefined ?
                                    students[index].name + ' ' + students[index].surname
                                    : (errors[index] ?
                                        (doubles[index] ? 'Indeks juz wpisany w innym wierszu!' : 'Indeks jest niepoprawny lub nie istnieje w systemie!'
                                        ) : ''
                                    )}
                            </p>
                        </div>

                    </div>
                ))}
                <div className="row justify-content-center">
                    <div className="col-sm-6">
                        <div className="form-group row justify-content-center">
                            <button type="submit" className="col-sm-3 btn btn-success m-2" onClick={handleSubmit}>
                                {/* zrobić inactive gdy l. indeksow mniejsza niz 2 */}
                                Zarezerwuj
                            </button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    );

}

export default SupervisorReservationPage