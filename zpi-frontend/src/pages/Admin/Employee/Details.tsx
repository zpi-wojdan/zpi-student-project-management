import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import { Employee } from '../../../models/Employee';
import { Department } from '../../../models/Department';
import Cookies from "js-cookie";
import {useTranslation} from "react-i18next";

const EmployeeDetails: React.FC = () => {
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const location = useLocation();
  const employee = location.state?.employee as Employee;

  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-begin  align-items-center mb-3'>
        <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; {t('general.management.goBack')}
        </button>
        <button type="button" className="custom-button" onClick={() => {
            // go to employee edit
            }}>
            {t('general.management.edit')}
        </button>
      </div>
      <div>
        {employee ? (
        <div>
            <p><span className="bold">{t('general.title')}:</span> <span>{employee.title}</span></p>
            <p><span className="bold">{t('general.people.name')}:</span> <span>{employee.name}</span></p>
            <p><span className="bold">{t('general.people.surname')}:</span> <span>{employee.surname}</span></p>
            <p><span className="bold">{t('general.people.mail')}:</span> <span>{employee.mail}</span></p>
            <p><span className="bold">{t('general.university.department')}:</span> <span>{employee.department.name}</span></p>
            <p className="bold">{t('general.people.roles')}:</p>
                <ul>
                    {employee.roles.map((role) => (
                        <li key={role.id}>
                          {role.name === 'approver' ? t('general.people.approverLC') :
                            role.name === 'supervisor' ? t('general.people.supervisorLC') :
                            role.name === 'admin' ? t('general.people.adminLC') : role.name}
                        </li>
                    ))}
                </ul> 
        </div>
        ) : (
        <p>{t('general.management.errorOfLoading')}</p>
        )}
      </div>
    </div>
  );
};

export default EmployeeDetails;
