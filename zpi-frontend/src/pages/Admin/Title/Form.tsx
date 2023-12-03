import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import {Deadline, DeadlineDTO} from "../../../models/Deadline";
import api_access from '../../../utils/api_access';

const TitleForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();
  const deadline = location.state?.deadline as Deadline;
  const [deadlineId, setDeadlineId] = useState<number>();
  const [formData, setFormData] = useState<DeadlineDTO>({
    namePL: "",
    nameEN: "",
    deadlineDate: new Date().toISOString().slice(0, 10),
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
    if (deadline) {
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          namePL: deadline.namePL,
          nameEN: deadline.nameEN,
          deadlineDate: deadline.deadlineDate,
        };
      });
      setDeadlineId(deadline.id);
    }
  }, [deadline]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (deadline) {
        api.put(api_access + `deadline/${deadlineId}`, formData)
          .then(() => {
            navigate("/deadlines")
            toast.success(t("deadline.updateSuccessful"));
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              toast.error(t("deadline.activityExists"));
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("deadline.updateError"));
            }
          });
      } else {
        api.post(api_access + 'deadline', formData)
          .then(() => {
            navigate("/deadlines")
            toast.success(t("deadline.addSuccessful"));
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              toast.error(t("deadline.activityExists"));
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("deadline.addError"));
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
    const errorRequireTextKey = 'general.management.fieldIsRequired';

    if (!formData.namePL) {
      newErrors.namePL = errorRequireText;
      newErrorsKeys.namePL = errorRequireTextKey;
      isValid = false;
    }

    if (!formData.nameEN) {
      newErrors.nameEN = errorRequireText;
      newErrorsKeys.nameEN = errorRequireTextKey;
      isValid = false;
    }

    if (!formData.deadlineDate) {
      newErrors.deadlineDate = errorRequireText;
      newErrorsKeys.deadlineDate = errorRequireTextKey;
      isValid = false;
    }
    else if(new Date(formData.deadlineDate).setHours(0, 0, 0, 0) < new Date().setHours(0, 0, 0, 0)) {
        newErrors.deadlineDate = t('deadline.dateInPast');
        newErrorsKeys.deadlineDate = 'deadline.dateInPast';
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
            {deadline ? t('deadline.save') : t('deadline.add')}
          </button>
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="namePL">
            {t('deadline.activity')} (PL):
          </label>
          <input
            type="text"
            id="namePL"
            name="namePL"
            value={formData.namePL}
            onChange={(e) => setFormData({ ...formData, namePL: e.target.value })}
            className="form-control"
          />
          {errors.namePL && <div className="text-danger">{errors.namePL}</div>}
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="nameEN">
            {t('deadline.activity')} (EN):
          </label>
          <input
              type="text"
              id="nameEN"
              name="nameEN"
              value={formData.nameEN}
              onChange={(e) => setFormData({ ...formData, nameEN: e.target.value })}
              className="form-control"
          />
          {errors.nameEN && <div className="text-danger">{errors.nameEN}</div>}
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="deadlineDate">
            {t('deadline.deadline')}:
          </label>
          <input
              type="date"
              id="deadlineDate"
              name="deadlineDate"
              value={formData.deadlineDate}
              onChange={(e) => setFormData({ ...formData, deadlineDate: e.target.value })}
              className="form-control"
          />
          {errors.deadlineDate && <div className="text-danger">{errors.deadlineDate}</div>}
        </div>
      </form>
    </div>
  );
};

export default TitleForm;
