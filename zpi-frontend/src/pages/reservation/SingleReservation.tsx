import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import { Employee } from '../../models/user/Employee';
import { Student } from '../../models/user/Student';
import { Thesis } from '../../models/thesis/Thesis';
import { useTranslation } from "react-i18next";
import api from "../../utils/api";
import { use } from 'i18next';
import api_access from '../../utils/api_access';

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
        const userCookies = JSON.parse(Cookies.get("user") || "{}");
        setUser(userCookies);
        setReservation(userCookies.index || "");
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

        const response = await api.post(api_access + "reservation", JSON.stringify(responseBody), {
            headers: {
                'Content-Type': 'application/json'
            }

        })
            .then(response => {
                if (response.status === 201) {
                    console.log(`Reservation ${reservation} created successfully`);
                    toast.success(t('reservation.reservationSuccessful'));
                    navigate("/public-theses/" + thesis.id)
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
                            <th scope="col" style={{ width: '60%' }}></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td style={{whiteSpace: "nowrap"}}>
                                <label htmlFor={`reservation`} className="col-sm-2 col-form-label">
                                    {t('general.people.student')}:</label>
                            </td>
                            <td>
                                <input
                                    id={`reservation`}
                                    type="text"
                                    className={`form-control my-1`}
                                    value={reservation}
                                    readOnly
                                />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </form>
        </div>

    );

}

export default SingleReservationPage