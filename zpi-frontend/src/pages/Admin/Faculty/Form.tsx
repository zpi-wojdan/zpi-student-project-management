import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Axios from 'axios';
import { Faculty } from '../../../models/Faculty';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import {useTranslation} from "react-i18next";

const FacultyForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const location = useLocation();
  const faculty = location.state?.faculty as Faculty;
  const [oldAbbr, setOldAbbr] = useState<String>();
  const [formData, setFormData] = useState<Faculty>({
    abbreviation: '',
    name: '',
    studyFields: [],
    programs: [],
    departments: [],
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
    if (faculty) {
      setFormData(faculty);
      setOldAbbr(faculty.abbreviation);
    }
  }, [faculty]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (faculty) {
        Axios.put(`http://localhost:8080/faculty/${oldAbbr}`, formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/faculties")
          toast.success(t("faculty.updateSuccessful"));
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                const newErrors: Record<string, string> = {};
                newErrors.abbreviation = t("general.management.abbreviationExists")
                setErrors(newErrors);
                const newErrorsKeys: Record<string, string> = {};
                newErrorsKeys.abbreviation = "general.management.abbreviationExists"
                setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
            }
          });
      } else {
        Axios.post('http://localhost:8080/faculty', formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/faculties")
          toast.success(t("faculty.addSuccessful"));
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                const newErrors: Record<string, string> = {};
                newErrors.abbreviation = t("general.management.abbreviationExists")
                setErrors(newErrors);
                const newErrorsKeys: Record<string, string> = {};
                newErrorsKeys.abbreviation = "general.management.abbreviationExists"
                setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
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

    if (!formData.abbreviation) {
      newErrors.abbreviation = errorRequireText;
      newErrorsKeys.abbreviation = "general.management.fieldIsRequired"
      isValid = false;
    }

    if (!formData.name) {
      newErrors.name = errorRequireText;
        newErrorsKeys.name = "general.management.fieldIsRequired"
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
                {faculty ? t('general.management.save') : t('general.management.add')}
                </button>
            </div>
            <div className="mb-3">
                <label className="bold" htmlFor="abbreviation">
                    {t('general.university.abbreviation')}:
                </label>
                <input
                type="text"
                id="abbreviation"
                name="abbreviation"
                value={formData.abbreviation}
                onChange={(e) => setFormData({ ...formData, abbreviation: e.target.value })}
                className="form-control"
                disabled={faculty ? true : false}
                />
                {errors.abbreviation && <div className="text-danger">{errors.abbreviation}</div>}
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

export default FacultyForm;
