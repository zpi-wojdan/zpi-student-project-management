import React, { useState } from 'react'
import axios from 'axios'
import { Student, Thesis } from '../models/Models';
import { useNavigate } from 'react-router-dom';

type ReservationProps = {
    thesis?: Thesis;
    mail?: string;
}

function ReservationPage({ thesis, mail }: ReservationProps) {
    const navigate = useNavigate();

    const [reservations, setReservations] = useState<string[]>([""]);
    const [errors, setErrors] = useState<boolean[]>([]);
    const [students, setStudents] = useState<Student[]>([])

    const addReservationInput = () => {
        if (thesis?.num_people && reservations.length >= thesis?.num_people) {
            return;
        }
        setReservations([...reservations, ""]);
    };

    const removeReservationInput = (index: number) => {
        const updatedReservations = [...reservations];
        const updatedErrors = [...errors];
        const updatedStudents = [...students];

        updatedReservations.splice(index, 1);
        updatedErrors.splice(index, 1);
        updatedStudents.splice(index, 1);

        setReservations(updatedReservations);
        setErrors(updatedErrors);
        setStudents(updatedStudents);
    };

    const handleReservationChange = (index: number, value: string) => {
        const updatedReservations = [...reservations];
        updatedReservations[index] = value;
        setReservations(updatedReservations);
    };

    const isReservationValid = (reservation: string) => {
        return /^[0-9]{6}$/.test(reservation);
    };

    const handleReservationBlur = async (index: number) => {
        const reservation = reservations[index];
        const newErrors = [...errors];
        const newStudents = [...students];

        if (!isReservationValid(reservation)) {
            console.error(`Invalid reservation number: ${reservation}`);
            newErrors[index] = true;
            setErrors(newErrors);
            return;
        } else {
            newErrors[index] = false;
        }

        // await axios.get(`http://localhost:8080/student/${reservation}@student.pwr.edu.pl`)      // get student info and set students with the students data if exists else add error for that index        
        //     .then(response => {
        //         newStudents[index] = response.data;
        //         setStudents(newStudents);
        //     })
        //     .catch(error => {
        //         newErrors[index] = true;
        //     })
        setErrors(newErrors);
    };

    const handleSubmit = async () => {
        if (reservations.every(isReservationValid)) {
            // Iterate over each reservation and send a POST request to the API
            for (const reservation of reservations) {
                const response = await axios.post("http://localhost:8080/reservation", {
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        thesis,
                        mail,
                        reservation,
                    }),
                });
                // Handle API response for each reservation
                if (response.status === 401) {
                    console.log(`Reservation ${reservation} created successfully`);
                } else {
                    console.error(`Failed to submit reservation ${reservation} - it already exists`);
                }
            }
        } else {
            console.error("Invalid reservation numbers");
        }
    };

    return (
        <div className="container">
            <button type="button" className="btn btn-secondary m-2" onClick={() => navigate(-1)}>
                &larr; Powrót
            </button>
            <h1>Rezerwacja tematu:</h1>
            <h3>Temat: {thesis?.name_pl}</h3>
            <form>
                {reservations.map((reservation, index) => (
                    <div key={index} className="form-group row justify-content-center">
                        <label htmlFor={`reservation-${index}`} className="col-sm-2 col-form-label">Student {index + 1}:</label>
                        <div className="col-sm-6 d-flex">
                            <input
                                id={`reservation-${index}`}
                                type="text"
                                className={`form-control ${errors[index] ? "is-invalid" : ""}`}
                                value={reservation}
                                onChange={(e) => handleReservationChange(index, e.target.value)}
                                onBlur={() => handleReservationBlur(index)}
                                placeholder="Indeks"
                            />
                            {index > 0 ? (
                                <button type="button" className="btn btn-sm ml-2" onClick={() => removeReservationInput(index)}>
                                    <span>&times;</span>
                                </button>
                            ) : (
                                <span className="btn btn-sm ml-2" style={{ visibility: "hidden" }}>&times;</span>
                            )}
                        </div>
                        <div className="col-sm-4">
                            <p className="col-form-label">{students[index] && students[index].name + ' ' + students[index].surname}</p> {/* Imię i nazwisko wczytane po wpisaniu */}
                        </div>
                    </div>
                ))}
                <div className="row justify-content-center">
                    <div className="col-sm-6">
                        <div className="form-group row justify-content-center">
                            <button type="button" className="col-sm-3 btn btn-primary m-2" onClick={addReservationInput}>
                                Dodaj osobę
                            </button>
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

export default ReservationPage