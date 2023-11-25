import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Thesis } from '../../models/thesis/Thesis';
import { Program } from '../../models/university/Program';
import api from '../../utils/api';
import useAuth from "../../auth/useAuth";
import handleSignOut from "../../auth/Logout";
import { useTranslation } from "react-i18next";
import ChoiceConfirmation from '../../components/ChoiceConfirmation';
import { toast } from 'react-toastify';

const ApproveDetails: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();

  const { id } = useParams<{ id: string }>();
  const [thesis, setThesis] = useState<Thesis>();

  useEffect(() => {
    const response = api.get(`http://localhost:8080/thesis/${id}`)
      .then((response) => {
        setThesis(response.data);
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

  const [showRejectConfirmation, setShowRejectConfirmation] = useState(false);
  const [showAcceptConfirmation, setShowAcceptConfirmation] = useState(false);

  const handleConfirmClick = () => {
    setShowAcceptConfirmation(true);
  };

  const handleConfirmAccept = () => {
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
    setShowRejectConfirmation(false);
  };

  const handleCancelAccept = () => {
    setShowAcceptConfirmation(false);
  };

  const handleRejectClick = () => {
    setShowRejectConfirmation(true);
  };

  const handleConfirmReject = () => {
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
    setShowRejectConfirmation(false);
  };

  const handleCancelReject = () => {
    setShowRejectConfirmation(false);
  };

  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-begin  align-items-center mb-3'>
        <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; {t('general.management.goBack')}
        </button>
        
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
                onClose={handleCancelAccept}
                onConfirm={handleConfirmAccept}
                onCancel={handleCancelAccept}
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
        {showRejectConfirmation && (
          <tr>
            <td colSpan={5}>
              <ChoiceConfirmation
                isOpen={showRejectConfirmation}
                onClose={handleCancelReject}
                onConfirm={handleConfirmReject}
                onCancel={handleCancelReject}
                questionText={t('thesis.rejectConfirmation')}
              />
            </td>
          </tr>
        )}

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
                        <p><span className="bold">{t('general.university.specialization')} - </span> <span>{program.specialization ? program.specialization.name : t('general.management.lack')}</span></p>
                      </li>
                    </ul>
                  )}
                </li>
              ))}
            </ul>
            <p><span className="bold">{t('general.university.status')}:</span> <span>{thesis.status.name}</span></p>
          </div>
        ) : (
          <p>{t('general.management.errorOfLoading')} {id}</p>
        )}
      </div>
    </div>
  );
};

export default ApproveDetails;
