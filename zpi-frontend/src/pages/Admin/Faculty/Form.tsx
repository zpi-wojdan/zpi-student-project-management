import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Faculty, FacultyDTO } from '../../../models/Faculty';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";

const FacultyForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const location = useLocation();
  const faculty = location.state?.faculty as Faculty;
  const [facultyId, setFacultyId] = useState<number>();
  const [formData, setFormData] = useState<FacultyDTO>({
    abbreviation: '',
    name: '',
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
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          abbreviation: faculty.abbreviation,
          name: faculty.name,
        };
      });
      setFacultyId(faculty.id);
    }
  }, [faculty]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (faculty) {
        api.put(`http://localhost:8080/faculty/${facultyId}`, formData)
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
              navigate("/faculties")
              toast.error(t("faculty.updateSuccessful"));
            }
          });
      } else {
        api.post('http://localhost:8080/faculty', formData)
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
              navigate("/faculties")
              toast.error(t("faculty.updateSuccessful"));
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
    const regexPatternForAbbr = /^W\d{2}[A-Z]?$/;

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
          />
          <div className="text-info">
            {t('faculty.goodAbbrFormat')}
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

export default FacultyForm;
