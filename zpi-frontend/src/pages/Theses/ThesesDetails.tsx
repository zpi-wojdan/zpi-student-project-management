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

const ThesesDetails: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();

  const { id } = useParams<{ id: string }>();
  const [thesis, setThesis] = useState<ThesisFront>();

  useEffect(() => {
    const response = api.get(`http://localhost:8080/thesis/${id}`)
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
        };
        setThesis(thesis);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });

  }, [id]);

  const [programs, setPrograms] = useState<Program[]>([]);
  useEffect(() => {
    api.get('http://localhost:8080/program')
      .then((response) => {
        setPrograms(response.data);
      })
      .catch((error) => {
        console.error(error);
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
  }; const [user, setUser] = useState<Student & Employee>();

  useEffect(() => {
    setUser(JSON.parse(Cookies.get("user") || "{}"));
  }, []);

  const handleReadyForApproval = async () => {
    if (thesis?.reservations) {
      for (const reservation of thesis.reservations) {
        try {
          reservation.readyForApproval = true;
          reservation.sentForApprovalDate = new Date();
          const response = await api.put('http://localhost:8080/reservation/' + reservation.id,
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
    let url = 'http://localhost:8080/report/pdf/thesis-declaration/' + thesis?.id;

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
      <div className='row d-flex justify-content-between'>
        <button type="button" className="col-sm-2 custom-button another-color m-3" onClick={() => navigate(-1)}>
          &larr; {t('general.management.goBack')}
        </button>

        {(thesis && thesis.reservations && thesis.reservations.length > 0 &&
          (user?.mail === thesis?.supervisor.mail ||
            thesis.reservations.some((res: Reservation) => res.student.mail === user?.mail)) &&
          thesis.reservations.every((res: Reservation) => res.confirmedBySupervisor)) ?
          (
            <button className="col-sm-2 custom-button m-3" onClick={downloadDeclaration}>
              {t('thesis.downloadDeclaration')}
            </button>
          ) : null}

        {(thesis && thesis?.occupied < thesis?.numPeople && (
          user?.role?.name === 'student' &&
          user?.studentProgramCycles.some((programCycle) => thesis?.programs.map(p => p.studyField).some(studyField => studyField.abbreviation === programCycle.program.studyField.abbreviation)) ||
          user?.roles?.some(role => role.name === 'supervisor') &&
          user?.mail === thesis?.supervisor.mail ) ||
          user?.roles?.some(role => role.name === 'admin')) ?
          (
            <button type="button" className="col-sm-2 custom-button m-3" onClick={() => {
              if (user?.role?.name === 'student') {
                if (thesis?.reservations.length === 0) {
                  navigate('/reservation', { state: { thesis: thesis } })
                } else {
                  navigate('/single-reservation', { state: { thesis: thesis } })
                }
              } else {
                if (user?.mail === thesis?.supervisor.mail) {
                navigate('/supervisor-reservation', { state: { thesis: thesis } })
                } else {
                  navigate('/admin-reservation', { state: { thesis: thesis } })
                }
              }
            }
            }>
              {user?.role?.name === 'student' ? (
                <span>{t('general.management.reserve')}</span>
              ) : (
                user?.mail === thesis?.supervisor.mail ?
                  (
                    <span>{t('thesis.enrollStudents')}</span>
                  ) : (
                    user?.roles?.some(role => role.name === 'admin') && <span>{t('thesis.enrollStudents')}</span>
                  )
              )}
            </button>
          ) : (
            <span></span>
          )
        }
      </div>
      <div>
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
          <p>{t('general.management.errorOfLoading')} {id}</p>
        )}
      </div>
    </div>
  );
};

export default ThesesDetails;
