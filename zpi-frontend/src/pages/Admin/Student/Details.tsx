import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Student } from '../../../models/user/Student';
import { Faculty } from '../../../models/university/Faculty';
import { StudentProgramCycle } from '../../../models/StudentProgramCycle';
import useAuth from "../../../auth/useAuth";
import handleSignOut from "../../../auth/Logout";
import { toast } from 'react-toastify';
import ChoiceConfirmation from '../../../components/ChoiceConfirmation';
import api from '../../../utils/api';
import { useTranslation } from "react-i18next";
import { handleDeletionError } from '../../../utils/handleDeleteError';
import LoadingSpinner from "../../../components/LoadingSpinner";
import api_access from '../../../utils/api_access';

const StudentDetails: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [student, setStudent] = useState<Student>()
  const [loaded, setLoaded] = useState<boolean>(false);

  useEffect(() => {
    api.get(api_access + `student/${id}`)
      .then((response) => {
        setStudent(response.data);
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
    api.delete(api_access + `student/${student?.id}`)
      .then(() => {
        toast.success(t('student.deleteSuccessful'));
        navigate("/students");
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        handleDeletionError(error, t, 'student');
      });
    setShowDeleteConfirmation(false);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-begin  align-items-center mb-3'>
        <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; {t('general.management.goBack')}
        </button>
        {loaded ? (<React.Fragment>
          <button type="button" className="custom-button" onClick={() => { navigate(`/students/edit/${student?.id}`, { state: { student } }) }}>
            {t('student.edit')}
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
                  questionText={t('student.deleteConfirmation')}
                />
              </td>
            </tr>
          )}
        </React.Fragment>
        ) : (<></>)}
      </div>
      <div>
        {!loaded ? (
            <LoadingSpinner height="50vh" />
        ) : (<React.Fragment>
          {student ? (
            <div>
              <p><span className="bold">{t('general.people.name')}:</span> <span>{student.name}</span></p>
              <p><span className="bold">{t('general.people.surname')}:</span> <span>{student.surname}</span></p>
              <p><span className="bold">{t('general.people.index')}:</span> <span>{student.index}</span></p>
              <p><span className="bold">{t('general.university.status')}:</span> <span>{student.status}</span></p>
              {student.studentProgramCycles.length > 0 && (
                <div>
                  <p className="bold">{t('general.university.studyPrograms')}:</p>
                  <ul>
                    {student.studentProgramCycles.map((studentProgramCycle: StudentProgramCycle) => (
                      <li key={studentProgramCycle.program.id}>
                        {studentProgramCycle.program.name}
                        <button className='custom-toggle-button' onClick={() => toggleProgramExpansion(studentProgramCycle.program.id)}>
                          {expandedPrograms.includes(studentProgramCycle.program.id) ? '▼' : '▶'}
                        </button>
                        {expandedPrograms.includes(studentProgramCycle.program.id) && (
                          <ul>
                            <li>
                              <p><span className="bold">{t('general.university.studyCycle')} - </span>
                                <span>{studentProgramCycle.cycle.name}</span></p>
                            </li>
                            <li>
                              <p><span className="bold">{t('general.university.faculty')} - </span>
                                <span>{studentProgramCycle.program.studyField ? studentProgramCycle.program.studyField.faculty.name : studentProgramCycle.program.specialization.studyField.faculty.name}</span></p>
                            </li>
                            <li>
                              <p><span className="bold">{t('general.university.field')} - </span>
                                <span>{studentProgramCycle.program.studyField ? studentProgramCycle.program.studyField.name : studentProgramCycle.program.specialization.studyField.name}</span></p>
                            </li>
                            <li>
                              <p><span className="bold">{t('general.university.specialization')} - </span>
                                <span>{studentProgramCycle.program.specialization ?
                                  studentProgramCycle.program.specialization.name : t('general.management.nA')}
                                </span>
                              </p>
                            </li>
                          </ul>
                        )}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
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

export default StudentDetails;
