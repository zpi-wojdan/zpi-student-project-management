import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ThesisFront, Thesis } from '../../models/thesis/Thesis';
import api from '../../utils/api';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import { useTranslation } from "react-i18next";
import Cookies from 'js-cookie';
import LoadingSpinner from '../../components/LoadingSpinner';

const SupervisorMy: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();
  const [theses, setTheses] = useState<ThesisFront[]>([]);
  const [drafts, setDrafts] = useState<ThesisFront[]>([]);
  const [loadedTheses, setLoadedTheses] = useState<boolean>(false);
  const [loadedDrafts, setLoadedDrafts] = useState<boolean>(false);

  useEffect(() => {
    const user = JSON.parse(Cookies.get("user") || "{}")
    const statNames = ['Pending approval', 'Rejected', 'Approved', 'Assigned'];
    api.get(`http://localhost:8080/thesis/employee/${user.id}/statuses?statName=${statNames.join(',')}`)
      .then((response) => {
        const thesis_response = response.data.map((thesisDb: Thesis) => {
          const thesis: ThesisFront = {
            id: thesisDb.id,
            namePL: thesisDb.namePL,
            nameEN: thesisDb.nameEN,
            descriptionPL: thesisDb.descriptionPL,
            descriptionEN: thesisDb.descriptionEN,
            programs: thesisDb.programs,
            studyCycle: thesisDb.studyCycle,
            numPeople: thesisDb.numPeople,
            occupied: thesisDb.occupied,
            supervisor: thesisDb.supervisor,
            status: thesisDb.status,
            leader: thesisDb.leader,
            students: thesisDb.reservations.map((reservation) => reservation.student),
            reservations: thesisDb.reservations,
            comments: thesisDb.comments,
          };
          return thesis;
        });
        setTheses(thesis_response);
        setLoadedTheses(true);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });

    api.get(`http://localhost:8080/thesis/${user.id}/Draft`)
      .then((response) => {
        console.log(response);
        const thesis_response = response.data.map((thesisDb: Thesis) => {
          const thesis: ThesisFront = {
            id: thesisDb.id,
            namePL: thesisDb.namePL,
            nameEN: thesisDb.nameEN,
            descriptionPL: thesisDb.descriptionPL,
            descriptionEN: thesisDb.descriptionEN,
            programs: thesisDb.programs,
            studyCycle: thesisDb.studyCycle,
            numPeople: thesisDb.numPeople,
            occupied: thesisDb.occupied,
            supervisor: thesisDb.supervisor,
            status: thesisDb.status,
            leader: thesisDb.leader,
            students: thesisDb.reservations.map((reservation) => reservation.student),
            reservations: thesisDb.reservations,
            comments: thesisDb.comments,
          };
          return thesis;
        });
        setDrafts(thesis_response);
        setLoadedDrafts(true);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  const statusLabels: { [key:string]:string } = {
    "Draft": t('status.draft'),
    "Pending approval": t('status.pending'),
    "Rejected": t('status.rejected'),
    "Approved": t('status.approved'),
    "Assigned": t('status.assigned'),
    "Closed": t('status.closed')
  }

  return (
    <div className='page-margin'>
      <div >
        <button className="custom-button" onClick={() => { navigate('/my/add') }}>
          {t('thesis.add')}
        </button>
      </div>
      {!loadedTheses ? (
        <LoadingSpinner height="50vh" />
      ) : (<React.Fragment>
        {theses.length === 0 ? (
          <div className='info-no-data'>
            <p>{t('general.management.noData')}</p>
          </div>
        ) : (
          <table className="custom-table">
            <thead>
              <tr>
                <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                <th style={{ width: '62%' }}>{t('general.university.thesis')}</th>
                <th style={{ width: '15%', textAlign: 'center' }}>{t('general.university.status')}</th>
                <th style={{ width: '10%', textAlign: 'center' }}>{t('thesis.occupiedSeats')}</th>
                <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.details')}</th>
              </tr>
            </thead>
            <tbody>
              {theses.map((thesis, index) => (
                <tr key={thesis.id}>
                  <td className="centered">{index + 1}</td>
                  <td>
                    {i18n.language === 'pl' ? (
                      thesis.namePL
                    ) : (
                      thesis.nameEN
                    )}
                  </td>
                  <td className="centered">{statusLabels[thesis.status.name] || thesis.status.name}</td>
                  <td className="centered">{thesis.occupied + "/" + thesis.numPeople}</td>
                  <td>
                    <button
                      className="custom-button coverall"
                      onClick={() => { navigate(`/my/${thesis.id}`, { state: { thesis } }) }}
                    >
                      <i className="bi bi-arrow-right"></i>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </React.Fragment>)}
      <hr className="my-4" />
      {!loadedDrafts ? (
        <LoadingSpinner height="50vh" />
      ) : (<React.Fragment>
        {drafts.length === 0 ? (
          <div className='info-no-data'>
            <p>{t('general.management.noData')}</p>
          </div>
        ) : (
          <><h3 className="mb-4 text-center">
            {t('thesis.drafts')}
          </h3>
            <table className="custom-table">
              <thead>
                <tr>
                  <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                  <th>{t('general.university.thesis')}</th>
                  <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.details')}</th>
                </tr>
              </thead>
              <tbody>
                {drafts.map((thesis, index) => (
                  <tr key={thesis.id}>
                    <td className="centered">{index + 1}</td>
                    <td>
                      {i18n.language === 'pl' ? (
                        thesis.namePL
                      ) : (
                        thesis.nameEN
                      )}
                    </td>
                    <td>
                      <button
                        className="custom-button coverall"
                        onClick={() => { navigate(`/my/${thesis.id}`, { state: { thesis } }); }}
                      >
                        <i className="bi bi-arrow-right"></i>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table></>
        )}
      </React.Fragment>)}
    </div>
  );

}

export default SupervisorMy;
