import React, { useState } from 'react'
import axios from 'axios'

type ReservationProps = {
    thesis_id?: string;
    mail?: string;
}

type Student = {
    indeks: string,
    mail: string,
    name: string,
    surname: string
}

function ReservationPage({ thesis_id, mail }: ReservationProps) {
    const [reservations, setReservations] = useState<string[]>([""]);
    const [errors, setErrors] = useState<boolean[]>([]);
    const [students, setStudents] = useState<Student[]>([])

    const addReservationInput = () => {
        setReservations([...reservations, ""]);
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
        // await axios.get('')      # get student info and set students with the students data if exists else add error for that index        
        const newErrors = [...errors];
        if (!isReservationValid(reservation)) {
            console.error(`Invalid reservation number: ${reservation}`);
            newErrors[index] = true;
        } else {
            newErrors[index] = false;
        }
        setErrors(newErrors);
    };

    const handleSubmit = async () => {
        if (reservations.every(isReservationValid)) {
            // Iterate over each reservation and send a POST request to the API
            for (const reservation of reservations) {
                const response = await fetch("/api/reservation", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        thesis_id,
                        mail,
                        reservation,
                    }),
                });
                // Handle API response for each reservation
                if (response.ok) {
                    console.log(`Reservation ${reservation} submitted successfully`);
                } else {
                    console.error(`Failed to submit reservation ${reservation}`);
                }
            }
        } else {
            console.error("Invalid reservation numbers");
        }
    };

    return (
        <div className="container w-80">
            <h1 className="text-center">Rezerwacja tematu:</h1>
            {/* tu moźna wrzucić później dane tematu jak juz endpointy beda gotowe to zmienie */}
            <form>
                {reservations.map((reservation, index) => (
                    <div key={index} className="form-group row justify-content-center">
                        <label htmlFor={`reservation-${index}`} className="col-sm-2 col-form-label">Student {index + 1}:</label>
                        <div className="col-sm-4">
                            <input
                                id={`reservation-${index}`}
                                type="text"
                                className={`form-control ${errors[index] ? "is-invalid" : ""}`}
                                value={reservation}
                                onChange={(e) => handleReservationChange(index, e.target.value)}
                                onBlur={() => handleReservationBlur(index)}
                                placeholder="6-digit number"
                            />
                            <div className="invalid-feedback">
                                Podany indeks jest niepoprawny!
                            </div>
                        </div>
                        <div className="col-sm-2">
                            <p className="col-form-label">{students[index] && students[index].name + ' ' + students[index].surname}</p> {/* Imię i nazwisko wczytane po wpisaniu */}
                        </div>
                    </div>
                ))}
                <div className="row justify-content-center">
                    <div className="col-sm-4">
                        <div className="form-group row justify-content-center">
                            <button type="button" className="btn btn-primary m-2" onClick={addReservationInput}>
                                Dodaj osobę
                            </button>
                        </div>
                        <div className="form-group row justify-content-center">
                            <button type="submit" className="btn btn-success mx-2" onClick={handleSubmit}>
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