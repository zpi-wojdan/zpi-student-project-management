import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import StudentTable from '../../components/StudentsTable';
import { ThesisFront, Thesis } from '../../models/thesis/Thesis';
import { Program } from '../../models/university/Program';
import Cookies from 'js-cookie';
import { Employee } from '../../models/user/Employee';
import { Student } from '../../models/user/Student';
import api from '../../utils/api';
import useAuth from "../../auth/useAuth";
import handleSignOut from "../../auth/Logout";
import { useTranslation } from "react-i18next";
import { Reservation } from "../../models/thesis/Reservation";
import { toast } from "react-toastify";
import api_access from '../../utils/api_access';
import LoadingSpinner from "../../components/LoadingSpinner";
import { Alert } from 'react-bootstrap';


const ThesisStudent: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();

  const [thesis, setThesis] = useState<ThesisFront>();
  const [loaded, setLoaded] = useState<boolean>(false);
  const [user, setUser] = useState<Student>();

  useEffect(() => {
    const userTmp = JSON.parse(Cookies.get("user") || "{}")
    setUser(userTmp)
    api.get(api_access + `thesis/student/${userTmp.id}`)
      .then((response) => {
        const thesisDb = response.data as Thesis;
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
          students: thesisDb.reservations.map((reservation) => reservation.student).sort((a, b) => a.index.localeCompare(b.index)),
          reservations: thesisDb.reservations.sort((a, b) => a.student.index.localeCompare(b.student.index)),
          comments: thesisDb.comments,
        };
        setThesis(thesis);
        setLoaded(true);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 404) {
          setLoaded(true);
        }
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });

  }, []);

  const [expandedPrograms, setExpandedPrograms] = useState<number[]>([]);

  const toggleProgramExpansion = (programId: number) => {
    if (expandedPrograms.includes(programId)) {
      setExpandedPrograms(expandedPrograms.filter(id => id !== programId));
    } else {
      setExpandedPrograms([...expandedPrograms, programId]);
    }
  };

  const handleReadyForApproval = async () => {
    if (thesis?.reservations) {
      for (const reservation of thesis.reservations) {
        try {
          reservation.readyForApproval = true;
          reservation.sentForApprovalDate = new Date();
          const response = await api.put(api_access + 'reservation/' + reservation.id,
            JSON.stringify(reservation)
          );

          if (response.status === 200) {
            toast.success(t('thesis.readyForApproval'));
            console.log('All users reservations sent for approval successfully');
          }
        } catch (error) {
          toast.error(t('thesis.readyForApprovalError'));
          console.error(`Failed to update reservations for reservation: ${reservation}`, error);
        }
      }
    }
  };

  const downloadDeclaration = () => {
    let url = api_access + 'report/pdf/thesis-declaration/' + thesis?.id;

    let toastId: any = null;
    toastId = toast.info(t('thesis.generating'), { autoClose: false });

    api.get(url, { responseType: 'blob' })
      .then((response) => {
        const file = new Blob([response.data], { type: 'application/pdf' });
        const fileURL = URL.createObjectURL(file);
        const link = document.createElement('a');
        link.href = fileURL;

        const contentDisposition = response.headers['content-disposition'];
        let filename = 'report.pdf';
        if (contentDisposition) {
          const filenameMatch = contentDisposition.match(/filename=(.+)/i);
          if (filenameMatch.length === 2)
            filename = filenameMatch[1];
        }
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();

        setTimeout(() => {
          toast.dismiss(toastId);
        }, 2000);
        toast.success(t('thesis.downloadSuccessful'));
      })

      .catch((error) => {
        console.error(error);
        setTimeout(() => {
          toast.dismiss(toastId);
        }, 2000);

        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        else if (error.response.status === 404) {
          toast.error(t('thesis.downloadNoDataError'));
        }
        else
          toast.error(t('thesis.downloadError'));
      });
  }

  return (
    <div className='page-margin'>
      {loaded ? (<React.Fragment>
        <div className='d-flex justify-content-between align-items-center'>
          <div>
            {thesis && thesis.leader?.id === user?.id && thesis.reservations.some((r) => !r.confirmedByLeader) &&
              <Alert variant="info">
                {t('studentThesis.leaderAccept')}
              </Alert>}
            {(thesis && thesis.status.name === "PendingApproval") ? (
              <Alert variant="info">
                {t('studentThesis.pendingApproval')}
              </Alert>
            ) : ((thesis && thesis.reservations.every(r => r.readyForApproval)) ? (
              (thesis.reservations.every(r => r.confirmedBySupervisor) ?
                <Alert variant="info">
                  {t('studentThesis.afterSupervisorAccept')}
                </Alert>
                :
                <Alert variant="info">
                  {t('studentThesis.beforeSupervisorAccept')}
                </Alert>)
            ) : (thesis && !thesis.reservations.find(
              (reservation) => reservation.student.id === user?.id)?.confirmedByLeader &&
              <Alert variant="info">
                {t('studentThesis.beforeLeaderAccept')}
              </Alert>
            )
            )}
          </div>
          <div className='d-flex justify-content-end align-items-center  mb-3'>
            {thesis && thesis.reservations && thesis.reservations.length > 0 && thesis.reservations.every((res: Reservation) => res.confirmedBySupervisor) ?
              (
                <button className="custom-button" onClick={downloadDeclaration}>
                  {t('thesis.downloadDeclaration')}
                </button>
              ) : null}
          </div>
        </div>
      </React.Fragment>) : null}
      <div>
        {!loaded ? (
          <LoadingSpinner height="50vh" />
        ) : (<React.Fragment>
          {thesis ? (
            <div>
              <p className="bold">{t('thesis.thesisName')}:</p>
              {i18n.language === 'pl' ? (
                <p>{thesis.namePL}</p>
              ) : (
                <p>{thesis.nameEN}</p>
              )}
              <p className="bold">{t('general.university.description')}:</p>
              {i18n.language === 'pl' ? (
                <p>{thesis.descriptionPL}</p>
              ) : (
                <p>{thesis.descriptionEN}</p>
              )}
              <p><span className="bold">{t('general.people.supervisor')}:</span> <span>{thesis.supervisor.title.name +
                " " + thesis.supervisor.name + " " + thesis.supervisor.surname}</span></p>
              <p><span className="bold">{t('general.university.studyCycle')}:</span> <span>{thesis.studyCycle ?
                thesis.studyCycle.name : 'N/A'}</span></p>
              <p className="bold">{t('general.university.studyPrograms')}:</p>
              <ul>
                {thesis.programs.map((program: Program) => (
                  <li key={program.id}>
                    {program.name}
                    <button className='custom-toggle-button' onClick={() => toggleProgramExpansion(program.id)}>
                      {expandedPrograms.includes(program.id) ? '▼' : '▶'}
                    </button>
                    {expandedPrograms.includes(program.id) && (
                      <ul>
                        <li>
                          <p><span className="bold">{t('general.university.faculty')} - </span> <span>{program.studyField.faculty.name}</span></p>
                        </li>
                        <li>
                          <p><span className="bold">{t('general.university.field')} - </span> <span>{program.studyField.name}</span></p>
                        </li>
                        <li>
                          <p><span className="bold">{t('general.university.specialization')} - </span> <span>{program.specialization ? program.specialization.name : t('general.management.nA')}</span></p>
                        </li>
                      </ul>
                    )}
                  </li>
                ))}
              </ul>
              <div>
                <p><span className="bold">{t('thesis.enrolled')}:</span> <span>
                  {thesis.occupied + "/" + thesis.numPeople}</span></p>
                {thesis.students.length > 0 ? (
                  <StudentTable students={thesis.students} thesis={thesis} />
                ) : (
                  <></>
                )}
                {thesis?.leader?.mail === user?.mail &&
                  thesis?.reservations?.every(res => res.confirmedByLeader && res.confirmedByStudent) &&
                  thesis?.reservations?.length >= 3 &&
                  thesis?.reservations.some(r => !r.readyForApproval) &&
                  (
                    <button
                      type="button"
                      className="col-sm-2 custom-button m-3"
                      onClick={handleReadyForApproval}
                    >
                      {t('thesis.readyForApproval')}
                    </button>
                  )}

              </div>
            </div>
          ) : (
            <div className='info-no-data'>
              <p>{t('studentThesis.noReservation')}</p>
            </div>
          )}
        </React.Fragment>)}
      </div>
    </div>
  );
};

export default ThesisStudent;
