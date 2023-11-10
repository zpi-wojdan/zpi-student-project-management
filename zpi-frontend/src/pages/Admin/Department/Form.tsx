import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Department, DepartmentDTO } from '../../../models/Department';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { Faculty } from '../../../models/Faculty';
import {useTranslation} from "react-i18next";
import api from "../../../utils/api";

const DepartmentForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();
  const department = location.state?.department as Department;
  const [formData, setFormData] = useState<DepartmentDTO>({
    code: '',
    name: '',
    facultyAbbreviation: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [errorsKeys, setErrorsKeys] = useState<Record<string, string>>({});

  useEffect(() => {
    const newErrors: Record<string, string> = {};
    Object.keys(errorsKeys).forEach((key) => {
        newErrors[key] = t(errorsKeys[key]);
    });
    setErrors(newErrors);
  }, [i18n.language]);

  useEffect(() => {
    if (department) {
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          code: department.code,
          name: department.name,
          facultyAbbr: department.faculty?.abbreviation,
        };
      });
    }
  }, [department]);

  const [faculties, setFaculties] = useState<Faculty[]>([]);

  useEffect(() => {
    api.get('http://localhost:8080/faculty')
      .then((response) => {
        setFaculties(response.data);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (department) {
        api.put(`http://localhost:8080/departments/${formData.code}`, formData)
        .then(() => {
          navigate("/departments")
          toast.success(t("department.updateSuccessful"));
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                const newErrors: Record<string, string> = {};
                newErrors.code = t("general.management.abbreviationExists")
                setErrors(newErrors);
                const newErrorsKeys: Record<string, string> = {};
                newErrorsKeys.code = "general.management.abbreviationExists"
                setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("department.updateError"));
            }
          });
      } else {
        api.post('http://localhost:8080/departments', formData)
        .then(() => {
          navigate("/departments")
          toast.success(t("department.addSuccessful"));
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                const newErrors: Record<string, string> = {};
                newErrors.code = t("general.management.abbreviationExists")
                setErrors(newErrors);
                const newErrorsKeys: Record<string, string> = {};
                newErrorsKeys.code = "general.management.abbreviationExists"
                setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("department.addError"));
            }
          });
      }
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    const newErrorsKeys: Record<string, string> = {};
    let isValid = true;

    const errorRequireText = t('general.management.fieldIsRequired');

    if (!formData.facultyAbbreviation) {
      newErrors.faculty = errorRequireText;
      newErrorsKeys.faculty = 'general.management.fieldIsRequired';
      isValid = false;
    }
    
    if (!formData.code) {
      newErrors.code = errorRequireText;
      newErrorsKeys.code = 'general.management.fieldIsRequired';
      isValid = false;
    }

    if (!formData.name) {
      newErrors.name = errorRequireText;
      newErrorsKeys.name = 'general.management.fieldIsRequired';
      isValid = false;
    }

    setErrors(newErrors);
    setErrorsKeys(newErrorsKeys);
    return isValid;
  };

  return (
    <div className='page-margin'>
        <form onSubmit={handleSubmit} className="form">
            <div className='d-flex justify-content-begin  align-items-center mb-3'>
                <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
                &larr; {t('general.management.goBack')}
                </button>
                <button type="submit" className="custom-button">
                {department ? t('general.management.save') : t('general.management.add')}
                </button>
            </div>
            <div className="mb-3">
              <label className="bold" htmlFor="faculty">
                  {t('general.university.faculty')}:
              </label>
              <select
                id="faculty"
                name="faculty"
                value={formData.facultyAbbreviation}
                onChange={(e) => setFormData({ ...formData, facultyAbbreviation: e.target.value })}
                className="form-control"
              >
                <option value="">{t('general.management.choose')}</option>
                {faculties.map((faculty) => (
                  <option key={faculty.abbreviation} value={faculty.abbreviation}>
                    {faculty.name}
                  </option>
                ))}
              </select>
              {errors.faculty && <div className="text-danger">{errors.faculty}</div>}
            </div>
            <div className="mb-3">
                <label className="bold" htmlFor="code">
                    {t('general.university.code')}:
                </label>
                <input
                type="text"
                id="code"
                name="code"
                value={formData.code}
                onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                className="form-control"
                disabled={department ? true : false}
                />
                {errors.code && <div className="text-danger">{errors.code}</div>}
            </div>
            <div className="mb-3">
                <label className="bold" htmlFor="name">
                    {t('general.university.name')}:
                </label>
                <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="form-control"
                />
                {errors.name && <div className="text-danger">{errors.name}</div>}
            </div>
        </form>
    </div>
  );
};

export default DepartmentForm;
