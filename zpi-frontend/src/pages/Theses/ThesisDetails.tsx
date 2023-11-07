import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import StudentTable from '../../components/StudentsTable';
import { ThesisFront, Thesis } from '../../models/Thesis';
import { Program } from '../../models/Program';
import { Faculty } from '../../models/Faculty';

import Cookies from 'js-cookie';
import { spawn } from 'child_process';
import { Employee } from '../../models/Employee';
import { Student } from '../../models/Student';
import api from '../../utils/api';
import useAuth from "../../auth/useAuth";
import handleSignOut from "../../auth/Logout";
import {useTranslation} from "react-i18next";

const ThesisDetails: React.FC = () => {
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
          num_people: thesisDb.num_people,
          occupied: thesisDb.occupied,
          supervisor: thesisDb.supervisor,
          status: thesisDb.status,
          leader: thesisDb.leader,
          students: thesisDb.reservations.map((reservation) => reservation.student),
          reservations: thesisDb.reservations,
        };
        setThesis(thesis);
        console.log(thesis);
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
        console.log(programs);
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
    console.log(user);
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
            console.log('All users reservations sent for approval successfully');
          }
        } catch (error) {
          console.error(`Failed to update reservations for reservation: ${reservation}`, error);
        }
      }
    }
  };

  return (
    <>
      <div className='row d-flex justify-content-between'>
        <button type="button" className="col-sm-2 btn btn-secondary m-3" onClick={() => navigate(-1)}>
          &larr; {t('general.management.goBack')}
        </button>
        {(user?.role?.name === 'student' || user?.roles?.some(role => role.name === 'supervisor') &&
          user?.mail === thesis?.supervisor.mail) ?
          (
            <button type="button" className="col-sm-2 btn btn-primary m-3" onClick={() => {
              if (user?.role?.name === 'student') {
                if (thesis?.reservations.length === 0) {
                  navigate('/reservation', { state: { thesis: thesis } })
                } else {
                  navigate('/single-reservation', { state: { thesis: thesis } })
                }
              } else {
                navigate('/supervisor-reservation', { state: { thesis: thesis } })
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
                    <></>
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
            <p><span className="bold">{t('general.people.supervisor')}:</span> <span>{thesis.supervisor.title +
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
                        <p><span className="bold">{t('general.university.specialization')} - </span> <span>{program.specialization ? program.specialization.name : t('general.management.lack')}</span></p>
                      </li>
                    </ul>
                  )}
                </li>
              ))}
            </ul>
            <div>
              <p><span className="bold">{t('thesis.enrolled')}:</span> <span>
                  {thesis.occupied + "/" + thesis.num_people}</span></p>
              {thesis.students.length > 0 ? (
                <StudentTable students={thesis.students} thesis={thesis} role={"student"} />
              ) : (
                <></>
              )}
              {thesis?.leader?.mail === user?.mail && thesis?.reservations?.every(res => res.confirmedByLeader && res.confirmedByStudent) && (
                <button
                  type="button"
                  className="col-sm-2 btn btn-primary m-3"
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
    </>
  );
};

export default ThesisDetails;
