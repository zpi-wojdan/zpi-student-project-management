import React, { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Thesis, ThesisDTO } from '../../models/thesis/Thesis';
import { Program } from '../../models/university/Program';
import api from '../../utils/api';
import useAuth from "../../auth/useAuth";
import handleSignOut from "../../auth/Logout";
import { useTranslation } from "react-i18next";
import ChoiceConfirmation from '../../components/ChoiceConfirmation';
import { toast } from 'react-toastify';
import { CommentDTO, Comment } from '../../models/thesis/Comment';
import { Status } from '../../models/thesis/Status';
import { Employee } from '../../models/user/Employee';
import Cookies from 'js-cookie';
import LoadingSpinner from "../../components/LoadingSpinner";
import api_access from "../../utils/api_access";

const ApproveDetails: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();

  const [key, setKey] = useState(0);
  const [commentsKey, setCommentsKey] = useState(1);

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [errorKeys, setErrorsKeys] = useState<Record<string, string>>({});

  const [commentForm, setCommentForm] = useState<CommentDTO>({
    content: '',
    thesisId: -1,
    authorId: -1
  });
  const commentContentRef = useRef<HTMLTextAreaElement | null>(null);

  const [statuses, setStatuses] = useState<Status[]>([]);
  const [statusName, setStatusName] = useState<string | undefined>(undefined);

  const [thesisForm, setThesisForm] = useState<ThesisDTO>({
    namePL: '',
    nameEN: '',
    descriptionPL: '',
    descriptionEN: '',
    numPeople: 4,
    supervisorId: -1,
    programIds: [-1],
    studyCycleId: -1,
    statusId: -1,
    studentIndexes: []
  });
  const [loaded, setLoaded] = useState<boolean>(false);

  const { id } = useParams<{ id: string }>();
  const [thesis, setThesis] = useState<Thesis>();

  const [user, setUser] = useState<Employee>();
  const [commentSectionRights, setCommentSectionRights] = useState(false);

  const [confirmClicked, setConfirmClicked] = useState(false);
  const [rejectClicked, setRejectClicked] = useState(false);

  const [rejected, setRejected] = useState(false);

  useEffect(() => {
    const response = api.get(api_access +`thesis/${id}`)
      .then((response) => {
        const thesisDb = response.data as Thesis;
        const t: Thesis = {
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
          reservations: thesisDb.reservations.sort((a, b) => a.student.index.localeCompare(b.student.index)),
          comments: thesisDb.comments,
        };
        setThesis(t);
        setStatusName(t.status.name);
        setLoaded(true);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });

  }, [id, key, commentsKey]);

  useEffect(() => {
    if (user === null) {
      const u = JSON.parse(Cookies.get("user") || "{}")
      setUser(u);
    }
  }, [id])

  useEffect(() => {
    if (thesis) {
      setThesisForm((prev) => {
        return {
          ...prev,
          namePL: thesis.namePL,
          nameEN: thesis.nameEN,
          descriptionPL: thesis.descriptionPL,
          descriptionEN: thesis.descriptionEN,
          numPeople: thesis.numPeople,
          supervisorId: thesis.supervisor.id,
          programIds: thesis.programs.map((p) => p.id),
          studyCycleId: thesis.studyCycle?.id ?? null,
          statusId: thesis.status.id,
        };
      });
    }
  }, [thesis]);

  useEffect(() => {
    if (thesis && user) {
      setCommentForm((prev) => {
        return {
          ...prev,
          thesisId: thesis.id,
          authorId: user.id
        };
      });
    }
  }, [thesis, user]);

  useEffect(() => {
    const newErrors: Record<string, string> = {};
    Object.keys(errorKeys).forEach((key) => {
      newErrors[key] = t(errorKeys[key]);
    });
    setErrors(newErrors);
  }, [i18n.language]);

  useEffect(() => {
    api.get(api_access +'status/Rejected')
      .then((response) => {
        setStatuses(statuses => [...statuses, response.data]);
      })
      .catch((error) => {
        console.error('Rejected error:', error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });

    api.get(api_access +'status/Approved')
      .then((response) => {
        setStatuses(statuses => [...statuses, response.data]);
      })
      .catch((error) => {
        console.error('Approved error:', error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, [id]);

  const [programs, setPrograms] = useState<Program[]>([]);
  useEffect(() => {
    api.get(api_access +'program')
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
  }, [id]);

  useEffect(() => {
    const byRoles = gotCommentSectionRightsByRoles();
    const bySupervisor = gotCommentSectionRightsBySupervisor();
    setCommentSectionRights(byRoles || bySupervisor);
  }, [user, thesisForm])

  const [expandedPrograms, setExpandedPrograms] = useState<number[]>([]);

  const toggleProgramExpansion = (programId: number) => {
    if (expandedPrograms.includes(programId)) {
      setExpandedPrograms(expandedPrograms.filter(id => id !== programId));
    } else {
      setExpandedPrograms([...expandedPrograms, programId]);
    }
  };

  const [showRejectConfirmation, setShowRejectConfirmation] = useState(false);
  const [showAcceptConfirmation, setShowAcceptConfirmation] = useState(false);

  const handleTextAreaChange = (
    event: React.ChangeEvent<HTMLTextAreaElement>,
    textareaRef: React.RefObject<HTMLTextAreaElement>
  ) => {
    setCommentForm({
      ...commentForm,
      content: event.target.value
    });
    if (textareaRef.current) {
      const textarea = textareaRef.current;
      textarea.style.height = 'auto';
      textarea.style.height = `${textarea.scrollHeight}px`;
    }
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

  const getApprovedStatusId = () => {
    return statuses.find(s => s.name === 'Approved')?.id ?? -1;
  }
  const getRejectedStatusId = () => {
    return statuses.find(s => s.name === 'Rejected')?.id ?? -1;
  }
  const gotCommentSectionRightsByRoles = () => {
    let u: Employee | undefined;
    if (user === null || user === undefined) {
      u = JSON.parse(Cookies.get("user") || "{}")
      setUser(u);
    }
    else {
      u = user;
    }
    return u?.roles.some(role => (role.name === 'admin' || role.name === 'approver')) ?? false
  }
  const gotCommentSectionRightsBySupervisor = () => {
    let u: Employee | undefined;
    if (user === null || user === undefined) {
      u = JSON.parse(Cookies.get("user") || "{}")
      setUser(u);
    }
    else {
      u = user;
    }
    return u?.id === thesisForm.supervisorId;
  }

  const handleConfirmClick = () => {
    setShowAcceptConfirmation(true);
    setConfirmClicked(true);
    setRejectClicked(false);
    setThesisForm({
      ...thesisForm,
      statusId: getApprovedStatusId()
    })
  };

  //  TODO: Approved vs Assigned
  const handleConfirmAccept = () => {
    const [isValid, thesisDTO] = validateThesis();
    if (isValid) {
      api.put(api_access +`thesis/${id}`, thesisDTO)
        .then(() => {
          toast.success(t("thesis.acceptSuccesful"));
          if (thesisDTO) {
            setStatusName(statuses.find(s => s.id = thesisDTO?.statusId)?.name)
          }
          navigate('/manage');
        })
        .catch((error) => {
          console.error(error);
          if (error.response.status === 401 || error.response.status === 403) {
            setAuth({ ...auth, reasonOfLogout: 'token_expired' });
            handleSignOut(navigate);
          }
          toast.error(t("thesis.acceptError"));
        });
    }
    else {
      toast.error(t("thesis.acceptError"));
    }
    setShowAcceptConfirmation(false);
  };

  const handleConfirmCancel = () => {
    setShowAcceptConfirmation(false);
  };

  const handleRejectClick = () => {
    setShowRejectConfirmation(true);
    setConfirmClicked(false);
    setRejectClicked(true);
    setThesisForm({
      ...thesisForm,
      statusId: getRejectedStatusId()
    })
  };

  const handleRejectConfirm = () => {
    const [thesisValid, thesisDTO] = validateThesis();
    if (thesisValid) {

      const [commentValid, commentDTO] = validateComment();
      if (commentValid) {
        api.post(api_access +`thesis/comment`, commentDTO)
          .then(() => {
            toast.success(t("comment.addSuccessful"));

            api.put(api_access +`thesis/${id}`, thesisDTO)
              .then(() => {
                setRejected(true);
                const name =  statuses.find(s => s.name === 'Rejected')?.name ?? "";
                if (name !== ""){
                  setStatusName(name);
                }
                toast.success(t("thesis.rejectionSuccessful"));
                if (thesisDTO) {
                  setStatusName(statuses.find(s => s.id = thesisDTO?.statusId)?.name)
                }
                setShowRejectConfirmation(false);
                setKey(k => k+1);
                setCommentsKey(k => k + 1);
              })
              .catch((error) => {
                console.error(error);
                if (error.response.status === 401 || error.response.status === 403) {
                  setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                  handleSignOut(navigate);
                }
                toast.error(t("thesis.rejectionError"));
              });

          })
          .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
            }
            toast.error(t("comment.addError"));
          });
      }
      else {
        toast.error(t("thesis.rejectionError"));
      }
    }
    else {
      toast.error(t("thesis.rejectionError"));
    }
  };

  const handleRejectCancel = () => {
    setShowRejectConfirmation(false);
  };

  const validateComment = (): [boolean, CommentDTO | null] => {
    const newErrors: Record<string, string> = {};
    const newErrorsKeys: Record<string, string> = {};
    const errorRequireText = t('general.management.fieldIsRequired');
    let isValid = true;

    let authorIndex: number = commentForm.authorId;
    let thesisIndex: number = commentForm.thesisId;

    if (!commentForm.content || commentForm.content.length === 0) {
      newErrors.comment = errorRequireText
      newErrorsKeys.comment = "general.management.fieldIsRequired";
      isValid = false;
    }

    if (!authorIndex || authorIndex === -1) {
      if (!user) {
        const cookieUser = JSON.parse(Cookies.get("user") || "{}");
        if (!cookieUser) {
          isValid = false;
        }
        else {
          authorIndex = cookieUser.id;
        }
      }
      else {
        authorIndex = user.id;
      }
    }

    if (!thesisIndex || thesisIndex === -1) {
      if (!thesis) {
        isValid = false;
      }
      else {
        thesisIndex = thesis.id;
      }
    }

    setErrors(newErrors);
    setErrorsKeys(newErrorsKeys);

    if (!isValid) {
      return [isValid, null];
    }
    const dto: CommentDTO = {
      content: commentForm.content,
      authorId: authorIndex,
      thesisId: thesisIndex
    }
    return [isValid, dto];
  }

  const validateThesis = (): [boolean, ThesisDTO | null] => {
    const isValid = !thesis ||
      !(thesis.status &&
        (thesis.status.name === 'Rejected' || thesis.status.name === 'Approved'));

    let id: number;
    if (confirmClicked && !rejectClicked) {
      id = getApprovedStatusId();
    }
    else if (!confirmClicked && rejectClicked) {
      id = getRejectedStatusId();
    }
    else {
      id = -1;
    }

    if (isValid && thesis && id !== -1) {
      const dto: ThesisDTO = {
        namePL: thesis.namePL,
        nameEN: thesis.nameEN,
        descriptionPL: thesis.descriptionPL,
        descriptionEN: thesis.descriptionEN,
        numPeople: thesis.numPeople,
        supervisorId: thesis.supervisor.id,
        programIds: thesis.programs.map(p => p.id),
        studyCycleId: thesis.studyCycle?.id ?? null,
        statusId: id,
        studentIndexes: thesis.reservations.map(r => r.student.index)
      }
      return [isValid, dto];
    }
    else {
      return [isValid, null];
    }
  }

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
        {(loaded && !rejected) ? (<React.Fragment>
          {!showRejectConfirmation && (
            <>
              <button
                type="button"
                className="custom-button"
                onClick={() => handleConfirmClick()}
                disabled={showRejectConfirmation}
              >
                {t('general.management.accept')}
              </button>

              {showAcceptConfirmation && (
                <tr>
                  <td colSpan={5}>
                    <ChoiceConfirmation
                      isOpen={showAcceptConfirmation}
                      onClose={handleConfirmCancel}
                      onConfirm={handleConfirmAccept}
                      onCancel={handleConfirmCancel}
                      questionText={t('thesis.acceptConfirmation')}
                    />
                  </td>
                </tr>
              )}

              <button
                type="button"
                className="custom-button"
                onClick={() => handleRejectClick()}
                disabled={showAcceptConfirmation}
              >
                {t('general.management.reject')}
              </button>
            </>
          )}

          {showRejectConfirmation && (
            <tr>
              <td colSpan={5}>
                <div className='d-flex justify-content-center  align-items-center'>
                  <div style={{ display: "flex", alignItems: "center" }}>
                    <button className="custom-button" onClick={handleRejectConfirm}>{t('general.management.send')}</button>
                    <button className="custom-button another-color" onClick={handleRejectCancel}>{t('general.management.cancel')}</button>
                  </div>
                </div>
              </td>
            </tr>
          )}
        </React.Fragment>
        ) : (<></>)}
      </div>
      {showRejectConfirmation && (
        <form>
          <div className="mb-3">
            <label className="bold" htmlFor="comment">
              {t('thesis.sendRejection')}
            </label>
            <textarea
              className="form-control resizable-input"
              id="comment"
              name="comment"
              value={commentForm.content}
              onChange={(event) => handleTextAreaChange(event, commentContentRef)}
              maxLength={1000}
              ref={commentContentRef}
            />
            {errors.comment && <div className="text-danger">{errors.comment}</div>}
          </div>

        </form>
      )}
      <div className='mt-3'>
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
              <p key={key}><span className="bold">{t('general.university.status')}: </span>
                <span>
                  {(statusName !== undefined && statusLabels[statusName]) || thesis.status.name}
                </span>
              </p>

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

export default ApproveDetails;
