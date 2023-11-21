import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Thesis } from '../../../models/thesis/Thesis';
import { Program } from '../../../models/university/Program';
import api from '../../../utils/api';
import useAuth from "../../../auth/useAuth";
import handleSignOut from "../../../auth/Logout";
import { useTranslation } from "react-i18next";
import DeleteConfirmation from '../../../components/DeleteConfirmation';
import { toast } from 'react-toastify';

const ThesisDetails: React.FC = () => {
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

  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-begin  align-items-center mb-3'>
        <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; {t('general.management.goBack')}
        </button>
        <button type="button" className="custom-button" onClick={() => { navigate(`/theses/edit/${id}`, { state: { thesis } }) }}>
          {t('thesis.edit')}
        </button>
        <button type="button" className="custom-button" onClick={() => handleDeleteClick()}>
          <i className="bi bi-trash"></i>
        </button>
        {showDeleteConfirmation && (
          <tr>
            <td colSpan={5}>
              <DeleteConfirmation
                isOpen={showDeleteConfirmation}
                onClose={handleCancelDelete}
                onConfirm={handleConfirmDelete}
                onCancel={handleCancelDelete}
                questionText={t('thesis.deleteConfirmation')}
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

export default ThesisDetails;
