import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Axios from 'axios';
import { StudyField, StudyFieldDTO } from '../../../models/StudyField';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import { Faculty } from '../../../models/Faculty';
import handleSignOut from "../../../auth/Logout";
import useAuth from '../../../auth/useAuth';
import {useTranslation} from "react-i18next";

const StudyFieldForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();
  const studyField = location.state?.studyField as StudyField;
  const [oldAbbr, setOldAbbr] = useState<String>();
  const [formData, setFormData] = useState<StudyFieldDTO>({
    abbreviation: '',
    name: '',
    facultyAbbr: '',
  });
  const [faculties, setFaculties] = useState<Faculty[]>([]);
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
    Axios.get('http://localhost:8080/faculty', {
      headers: {
        'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
    })
      .then((response) => {
        setFaculties(response.data);
        if (studyField) {
          formData.abbreviation = studyField.abbreviation;
          formData.name = studyField.name;
          formData.facultyAbbr = findFacultyAbbrByField(studyField.abbreviation);
          setOldAbbr(studyField.abbreviation);
        }
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  function findFacultyAbbrByField(fieldAbbr: string): string {
    for (const faculty of faculties) {
        for (const field of faculty.studyFields) {
            if (field.abbreviation === fieldAbbr) {
                return faculty.abbreviation;
            }
        }
    }
    return "";
  }

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      console.log(formData);
      if (studyField) {
        Axios.put(`http://localhost:8080/studyfield/${oldAbbr}`, formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/fields")
          toast.success(t("field.updateSuccessful"));
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
              toast.error(t("field.updateError"));
            }
          });
      } else {
        Axios.post('http://localhost:8080/studyfield', formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/fields")
          toast.success(t("field.addSuccessful"));
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
              toast.error(t("field.addError"));
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

    if (!formData.facultyAbbr) {
      newErrors.faculty = errorRequireText;
      newErrorsKeys.faculty = "general.management.fieldIsRequired"
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
                {studyField ? t('general.management.save') : t('general.management.add')}
                </button>
            </div>
            <div className="mb-3">
              <label className="bold" htmlFor="faculty">
                  {t('general.university.faculty')}:
              </label>
              <select
                id="faculty"
                name="faculty"
                value={formData.facultyAbbr}
                onChange={(e) => setFormData({ ...formData, facultyAbbr: e.target.value })}
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
                disabled={studyField ? true : false}
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

export default StudyFieldForm;
