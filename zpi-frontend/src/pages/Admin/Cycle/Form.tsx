import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { StudyCycle, StudyCycleDTO } from '../../../models/university/StudyCycle';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";

const StudyCycleForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();
  const studyCycle = location.state?.studyCycle as StudyCycle;
  const [cycleId, setCycleId] = useState<number>();
  const [formData, setFormData] = useState<StudyCycleDTO>({
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
    if (studyCycle) {
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          name: studyCycle.name,
        };
      });
      setCycleId(studyCycle.id);
    }
  }, [studyCycle]);

  useEffect(() => {
    if (studyCycle) {
      setFormData(studyCycle);
    }
  }, [studyCycle]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (studyCycle) {
        api.put(`http://localhost:8080/studycycle/${cycleId}`, formData)
          .then(() => {
            navigate("/cycles")
            toast.success(t("study_cycle.updateSuccessful"));
          })
          .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
            }
            toast.error(t("study_cycle.updateError"));
          });
      } else {
        api.post('http://localhost:8080/studycycle', formData)
          .then(() => {
            navigate("/cycles")
            toast.success(t("study_cycle.addSuccessful"));
          })
          .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
            }
            toast.error(t("study_cycle.addError"));
          });
      }
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    const newErrorsKeys: Record<string, string> = {};
    let isValid = true;

    const errorRequireText = t('general.management.fieldIsRequired');
    const regexPattern = /^\d{4}\/\d{2}-[A-Z]{1,3}$/;

    if (!formData.name) {
      newErrors.name = errorRequireText;
      newErrorsKeys.name = "general.management.fieldIsRequired"
      isValid = false;
    } else if (!regexPattern.test(formData.name)) {
      newErrors.name = t("general.management.wrongFormat")
      newErrorsKeys.name = "general.management.wrongFormat"
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
            {studyCycle ? t('study_cycle.save') : t('study_cycle.add')}
          </button>
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
          <div className="text-info">
            {t('study_cycle.goodFormat')}
          </div>
          {errors.name && <div className="text-danger">{errors.name}</div>}
        </div>
      </form>
    </div>
  );
};

export default StudyCycleForm;
