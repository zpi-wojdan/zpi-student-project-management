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
import { Comment } from '../../models/thesis/Comment';
import LoadingSpinner from "../../components/LoadingSpinner";
import { handleDeletionError } from '../../utils/handleDeleteError';
import ChoiceConfirmation from '../../components/ChoiceConfirmation';

type ThesisDetailsProps = {
  addStudents: boolean;
  goBackPath: string;
}

const ThesesDetails = ({ addStudents, goBackPath }: ThesisDetailsProps) => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();

  const { id } = useParams<{ id: string }>();
  const [thesis, setThesis] = useState<ThesisFront>();
  const [loaded, setLoaded] = useState<boolean>(false);

  useEffect(() => {
    const response = api.get(api_access + `thesis/${id}`)
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
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });

  }, [id]);

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
    api.delete(api_access + `thesis/${id}`)
      .then(() => {
        toast.success(t('thesis.deleteSuccessful'));
        navigate("/theses");
      })
      .catch((error) => {
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        handleDeletionError(error, t, 'thesis');

      });
    setShowDeleteConfirmation(false);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  const [user, setUser] = useState<Student & Employee>();
  const [commentSectionRights, setCommentSectionRights] = useState(false);

  useEffect(() => {
    setUser(JSON.parse(Cookies.get("user") || "{}"));
  }, []);

  useEffect(() => {
    const byRoles = gotCommentSectionRightsByRoles();
    const bySupervisor = gotCommentSectionRightsBySupervisor();
    const isDraft = thesis?.status.name == "Draft";
    setCommentSectionRights(!isDraft && (byRoles || bySupervisor));
  }, [user, thesis])

  const handleReadyForApproval = async () => {
    if (thesis?.reservations) {
      for (const reservation of thesis.reservations) {
        reservation.readyForApproval = true;
        reservation.sentForApprovalDate = new Date();
      }
      api.put(api_access + 'reservation/list_theses/' + thesis.id,
        JSON.stringify(thesis.reservations), {
        headers: {
          'Content-Type': 'application/json'
        }
      })
        .then((response) => {

          if (response.status === 200) {
            toast.success(t('thesis.sentForApproval'));
          }
        })
        .catch((error) => {
          toast.error(t('thesis.sentForApprovalError'));
        });
    }
  }


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
        ;
        setTimeout(() => {
          toast.dismiss(toastId);
        }, 2000);

        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        else if (error.response && error.response.status === 404) {
          toast.error(t('thesis.downloadNoDataError'));
        }
        else
          toast.error(t('thesis.downloadError'));
      });
  }

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

  const gotCommentSectionRightsByRoles = () => {
    let u: (Student & Employee) | undefined;
    if (user === null || user === undefined) {
      u = JSON.parse(Cookies.get("user") || "{}")
      setUser(u);
    }
    else {
      u = user;
    }
    return u?.roles?.some(role => (role.name === 'admin' || role.name === 'approver')) ?? false
  }
  const gotCommentSectionRightsBySupervisor = () => {
    let u: (Student & Employee) | undefined;
    if (user === null || user === undefined) {
      u = JSON.parse(Cookies.get("user") || "{}")
      setUser(u);
    }
    else {
      u = user;
    }
    const isSupervisor = u?.id === thesis?.supervisor.id;
    return u?.id === thesis?.supervisor.id;
  }


  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-between align-items-center mb-3'>
        <div className='d-flex justify-content-begin align-items-center'>
          <button type="button" className="custom-button another-color" onClick={() => navigate(goBackPath)}>
            &larr; {t('general.management.goBack')}
          </button>
          {(thesis && addStudents && (thesis.status.name === 'Approved' && thesis?.occupied < thesis?.numPeople && (
            user?.role?.name === 'student' &&
            user?.studentProgramCycles.some((programCycle) => thesis?.programs.map(p => p.studyField).some(studyField => studyField.abbreviation === programCycle.program.studyField.abbreviation))) ||
            user?.roles?.some(role => role.name === 'admin') && thesis?.status.name !== 'Closed')) ?
            (
              <button type="button" className="custom-button" onClick={() => {
                if (user?.role?.name === 'student') {
                  if (thesis?.reservations.length === 0) {
                    navigate('/reservation', { state: { thesis: thesis } })
                  } else {
                    navigate('/single-reservation', { state: { thesis: thesis } })
                  }
                } else {
                  navigate('/admin-reservation', { state: { thesis: thesis } })
                }
              }
              }>
                {user?.role?.name === 'student' ? (
                  <span>{t('general.management.reserve')}</span>
                ) : (
                  user?.roles?.some(role => role.name === 'admin') && <span>{t('thesis.enrollStudents')}</span>
                )}
              </button>
            ) : (
              <span></span>
            )
          }
          {loaded ? (<React.Fragment>
            {(thesis && thesis.reservations && thesis.reservations.length > 0 &&
              (user?.mail === thesis?.supervisor.mail ||
                thesis.reservations.some((res: Reservation) => res.student.mail === user?.mail)) &&
              thesis.reservations.every((res: Reservation) => res.confirmedBySupervisor && res.confirmedByStudent)) ?
              (
                <button className="custom-button" onClick={downloadDeclaration}>
                  {t('thesis.downloadDeclaration')}
                </button>
              ) : null}
          </React.Fragment>
          ) : (<></>)}

          {(loaded && (thesis?.status.name == "Draft" || thesis?.status.name == "Rejected")) ? (<React.Fragment>
            <button type="button" className="custom-button" onClick={() => { navigate(`/my/edit/${id}`, { state: { thesis } }) }}>
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
      </div>
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
              {i18n.language === 'pl' || !thesis.descriptionEN ? (
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


              <div className='comment-section'>
                {commentSectionRights && (
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
                )}
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

export default ThesesDetails;
