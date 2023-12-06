import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Employee } from '../../../models/user/Employee';
import { toast } from 'react-toastify';
import ChoiceConfirmation from '../../../components/ChoiceConfirmation';
import { useTranslation } from "react-i18next";
import api from '../../../utils/api';
import handleSignOut from '../../../auth/Logout';
import useAuth from "../../../auth/useAuth";
import api_access from '../../../utils/api_access';
import { handleDeletionError } from '../../../utils/handleDeleteError';
import LoadingSpinner from "../../../components/LoadingSpinner";

const EmployeeDetails: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const roleLabels: { [key: string]: string } = {
    supervisor: t('general.people.supervisorLC'),
    approver: t('general.people.approverLC'),
    admin: t('general.people.adminLC'),
  };

  const { id } = useParams<{ id: string }>();
  const [employee, setEmployee] = useState<Employee>()
  const [loaded, setLoaded] = useState<boolean>(false);

  useEffect(() => {
    api.get(api_access + `employee/${id}`)
      .then((response) => {
        setEmployee(response.data);
        setLoaded(true);
      })
      .catch((error) => {
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });

  }, [id]);

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

  const handleDeleteClick = () => {
    setShowDeleteConfirmation(true);
  };

  const handleConfirmDelete = () => {
    api.delete(api_access + `employee/${employee?.id}`)
      .then(() => {
        toast.success(t("employee.deleteSuccessful"));
        navigate("/employees");
      })
      .catch((error) => {
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        handleDeletionError(error, t, 'employee');
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
          <button type="button" className="custom-button" onClick={() => { navigate(`/employees/edit/${employee?.id}`, { state: { employee } }) }}>
            {t('employee.edit')}
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
                  questionText={t('employee.deleteConfirmation')}
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
          {employee ? (
            <div>
              <p><span className="bold">{t('general.title')}:</span> <span>{employee.title.name}</span></p>
              <p><span className="bold">{t('general.people.name')}:</span> <span>{employee.name}</span></p>
              <p><span className="bold">{t('general.people.surname')}:</span> <span>{employee.surname}</span></p>
              <p><span className="bold">{t('general.people.mail')}:</span> <span>{employee.mail}</span></p>
              <p><span className="bold">{t('general.university.department')}:</span> <span>{employee.department.name}</span></p>
              <p><span className="bold">{t('general.thesesLimit')}:</span> <span>{employee.numTheses}</span></p>
              <p className="bold">{t('general.people.roles')}:</p>
              <ul>
                {employee.roles.map((role) => (
                  <li key={role.id}>
                    {roleLabels[role.name] || role.name}
                  </li>
                ))}
              </ul>
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

export default EmployeeDetails;
