import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Thesis } from '../../../models/thesis/Thesis';
import { Program } from '../../../models/university/Program';
import api from '../../../utils/api';
import useAuth from "../../../auth/useAuth";
import handleSignOut from "../../../auth/Logout";
import { useTranslation } from "react-i18next";
import ChoiceConfirmation from '../../../components/ChoiceConfirmation';
import { toast } from 'react-toastify';
import { Comment } from '../../../models/thesis/Comment';

const ThesisDetails: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();

  const { id } = useParams<{ id: string }>();
  const [thesis, setThesis] = useState<Thesis>();
  const [loaded, setLoaded] = useState<boolean>(false);

  useEffect(() => {
    const response = api.get(`http://localhost:8080/thesis/${id}`)
      .then((response) => {
        setThesis(response.data);
        setLoaded(true);
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
  };

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

  const handleDeleteClick = () => {
    setShowDeleteConfirmation(true);
  };

  const handleConfirmDelete = () => {
    api.delete(`http://localhost:8080/thesis/${id}`)
      .then(() => {
        toast.success(t('thesis.deleteSuccessful'));
        navigate("/theses");
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        toast.error(t('thesis.deleteError'));

      });
    setShowDeleteConfirmation(false);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  const formatCreationTime = (creationTime: string) => {
    const now = new Date();
    const creationDate = new Date(creationTime);

    const elapsedMilliseconds = now.getTime() - creationDate.getTime();
    const elapsedSeconds = Math.floor(elapsedMilliseconds / 1000);
    const elapsedMinutes = Math.floor(elapsedSeconds / 60);
    const elapsedHours = Math.floor(elapsedMinutes / 60);
    const elapsedDays = Math.floor(elapsedHours / 24);
    const elapsedMonths = Math.floor(elapsedDays / 28); // miesiąc = +- 28 dni - zaokrąglam
    const elapsedYears = Math.floor(elapsedDays / 365); // rok = +- 365 dni - zaokrąglam

    const rtf = new Intl.RelativeTimeFormat(i18n.language === 'pl' ? 'pl' : 'en', { numeric: 'auto' });

    if (elapsedYears > 0) {
      return rtf.format(-elapsedYears, 'year');
    } else if (elapsedMonths > 0) {
      return rtf.format(-elapsedMonths, 'month');
    } else if (elapsedDays > 0) {
      return rtf.format(-elapsedDays, 'day');
    } else if (elapsedHours > 0) {
      return rtf.format(-elapsedHours, 'hour');
    } else if (elapsedMinutes > 0) {
      return rtf.format(-elapsedMinutes, 'minute');
    } else {
      return rtf.format(-elapsedSeconds, 'second');
    }
  };

  const statusLabels: { [key: string]: string } = {
    "Draft": t('status.draft'),
    "Pending approval": t('status.pending'),
    "Rejected": t('status.rejected'),
    "Approved": t('status.approved'),
    "Assigned": t('status.assigned'),
    "Closed": t('status.closed')
  }


  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-begin  align-items-center mb-3'>
        <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; {t('general.management.goBack')}
        </button>
        {loaded ? (<React.Fragment>
          <button type="button" className="custom-button" onClick={() => { navigate(`/theses/edit/${id}`, { state: { thesis } }) }}>
            {t('thesis.edit')}
          </button>
          <button type="button" className="custom-button" onClick={() => handleDeleteClick()}>
            <i className="bi bi-trash"></i>
          </button>
          {showDeleteConfirmation && (
            <tr>
              <td colSpan={5}>
                <ChoiceConfirmation
                  isOpen={showDeleteConfirmation}
                  onClose={handleCancelDelete}
                  onConfirm={handleConfirmDelete}
                  onCancel={handleCancelDelete}
                  questionText={t('thesis.deleteConfirmation')}
                />
              </td>
            </tr>
          )}
        </React.Fragment>
        ) : (<></>)}
      </div>
      <div>
        {!loaded ? (
          <div className='info-no-data'>
            <p>{t('general.management.load')}</p>
          </div>
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
                          <p><span className="bold">{t('general.university.specialization')} - </span> <span>{program.specialization ? program.specialization.name : t('general.management.lack')}</span></p>
                        </li>
                      </ul>
                    )}
                  </li>
                ))}
              </ul>
              <p>
                <span className="bold">{t('general.university.status')}: </span>
                <span>
                  {statusLabels[thesis.status.name] || thesis.status.name}
                </span>
              </p>

              <div className='comment-section'>
                <>
                  <hr className="my-4" />
                  {thesis.comments.length !== 0 ? (
                    <table className="custom-table mt-4">
                      <thead>
                        <tr>
                          <th style={{ width: '65%' }}>{t('comment.content')}</th>
                          <th style={{ width: '20%' }}>{t('comment.author')}</th>
                          <th style={{ width: '10%', textAlign: 'center' }}><i className="bi bi-stopwatch"></i></th>
                        </tr>
                      </thead>
                      <tbody>
                        {thesis.comments
                          .sort((a, b) => new Date(b.creationTime).getTime() - new Date(a.creationTime).getTime())
                          .map((c: Comment) => (
                            <tr key={`${c.id}`}>
                              <td
                                style={{
                                  wordBreak: 'break-word', overflowY: 'auto',
                                  display: '-webkit-box', WebkitLineClamp: 10, WebkitBoxOrient: 'vertical',
                                }}>
                                {c.content}
                              </td>
                              <td>{c.author.mail}</td>
                              <td className='centered'>{formatCreationTime(c.creationTime)}</td>
                            </tr>
                          ))}
                      </tbody>
                    </table>
                  ) : (
                    <div className='info-no-data'>
                      <p>{t('comment.empty')}</p>
                    </div>
                  )}
                </>
              </div>

            </div>
          ) : (
            <div className='info-no-data'>
              <p>{t('general.management.errorOfLoading')}</p>
            </div>
          )}
        </React.Fragment>)}
      </div>
    </div>
  );
};

export default ThesisDetails;
