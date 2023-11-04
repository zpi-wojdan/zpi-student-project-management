import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { useLocation, useNavigate } from 'react-router-dom';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import { Employee } from '../../models/Employee';
import { Student } from '../../models/Student';
import { Thesis } from '../../models/Thesis';
import {useTranslation} from "react-i18next";

type SingleReservationProps = {
}

function SingleReservationPage({ }: SingleReservationProps) {
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const { i18n, t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();
    const thesis = location.state?.thesis as Thesis;

    const [reservation, setReservation] = useState<string>("");
    const [user, setUser] = useState<Student & Employee>();

    useEffect(() => {
        setUser(JSON.parse(Cookies.get("user") || "{}"));
        setReservation(user?.index || "");
    }, []);

    const handleSubmit = async (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
        e.preventDefault();

            const responseBody = {
                thesisId: thesis.id,
                student: user,
                reservationDate: new Date(),
                confirmedByStudent: true,
            };
            console.log(JSON.stringify(responseBody));

            const response = await axios.post("http://localhost:8080/reservation", JSON.stringify(responseBody), {
                headers: {
                    "Content-Type": "application/json",
                    'Authorization': `Bearer ${Cookies.get('google_token')}`
                },
            })
                .then(response => {
                    if (response.status === 201) {
                        console.log(`Reservation ${reservation} created successfully`);
                        toast.success(t('reservation.reservationSuccessful'));
                        navigate("/theses/" + thesis.id)
                    }
                })
                .catch(error => {
                    console.error(`Failed to submit reservation ${reservation}`);
                    console.error(error)
                    if (error.response.status === 401 || error.response.status === 403) {
                        setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                        handleSignOut(navigate);
                    }
                    toast.error(t('reservation.reservationError'));
                });
    };

    return (
        <div className="container">
            <button type="button" className="btn btn-secondary m-2" onClick={() => navigate(-1)}>
                &larr; {t('general.management.goBack')}
            </button>
            <h1>{t('reservation.reservation')}:</h1>
            <h3>{t('general.university.thesis')}: {thesis?.namePL}</h3>
            <form>

                <div className="form-group row justify-content-center">
                    <label htmlFor={`reservation`} className="col-sm-2 col-form-label">
                        {t('general.people.student')}:</label>
                    <div className="col-sm-4 d-flex">
                        <input
                            id={`reservation`}
                            type="text"
                            className={`form-control`}
                            value={reservation}
                            readOnly
                        />
                    </div>
                </div>

                <div className="row justify-content-center">
                    <div className="col-sm-6">
                        <div className="form-group row justify-content-center">
                            <button type="submit" className="col-sm-3 btn btn-success m-2" onClick={handleSubmit}>
                                {t('general.management.reserve')}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    );

}

export default SingleReservationPage