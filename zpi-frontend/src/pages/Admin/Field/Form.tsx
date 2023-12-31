import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { StudyField, StudyFieldDTO } from '../../../models/university/StudyField';
import { toast } from 'react-toastify';
import { Faculty } from '../../../models/university/Faculty';
import handleSignOut from "../../../auth/Logout";
import useAuth from '../../../auth/useAuth';
import api from '../../../utils/api';
import { useTranslation } from "react-i18next";
import api_access from '../../../utils/api_access';

const StudyFieldForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();
  const studyField = location.state?.studyField as StudyField;
  const [fieldId, setFieldId] = useState<number>();
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
    if (studyField) {
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          abbreviation: studyField.abbreviation,
          name: studyField.name,
          facultyAbbr: studyField.faculty.abbreviation
        };
      });
      setFieldId(studyField.id);
    }
  }, [studyField]);

  useEffect(() => {
    api.get(api_access + 'faculty')
      .then((response) => {
        setFaculties(response.data);
      })
      .catch((error) => {
        if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (studyField) {
        api.put(api_access + `studyfield/${fieldId}`, formData)
          .then(() => {
            navigate("/fields")
            toast.success(t("study_field.updateSuccessful"));
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
              if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("study_field.updateError"));
            }
          });
      } else {
        api.post(api_access + 'studyfield', formData)
          .then(() => {
            navigate("/fields")
            toast.success(t("study_field.addSuccessful"));
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
              if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
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
    const errorWrongFormat = t("general.management.wrongFormat");
    const regexPatternForAbbr = /^[A-Z]{3}$/;

    if (!formData.abbreviation) {
      newErrors.abbreviation = errorRequireText;
      newErrorsKeys.abbreviation = "general.management.fieldIsRequired"
      isValid = false;
    } else if (!regexPatternForAbbr.test(formData.abbreviation)) {
      newErrors.abbreviation = errorWrongFormat
      newErrorsKeys.abbreviation = "general.management.wrongFormat"
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
            {studyField ? t('study_field.save') : t('study_field.add')}
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
          />
          <div className="text-info">
            {t('study_field.goodAbbrFormat')}
          </div>
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
