import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import api_access from '../../../utils/api_access';
import {Title, TitleDTO} from "../../../models/user/Title";

const TitleForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();
  const title = location.state?.title as Title;
  const [titleId, setTitleId] = useState<number>();
  const [formData, setFormData] = useState<TitleDTO>({
    name: '',
    numTheses: 2,
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
    if (title) {
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          name: title.name,
          numTheses: title.numTheses,
        };
      });
      setTitleId(title.id);
    }
  }, [title]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (title) {
        api.put(api_access + `title/${titleId}`, formData)
          .then(() => {
            navigate("/titles")
            toast.success(t("title.updateSuccessful"));
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              toast.error(t("title.titleExists"));
            } else {
              ;
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("title.updateError"));
            }
          });
      } else {
        api.post(api_access + 'title', formData)
          .then(() => {
            navigate("/titles")
            toast.success(t("title.addSuccessful"));
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              toast.error(t("title.titleExists"));
            } else {
              ;
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("title.addError"));
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

    if (!formData.name) {
      newErrors.name = errorRequireText;
      newErrorsKeys.name = errorRequireTextKey;
      isValid = false;
    }

    if (formData.numTheses < 0 || formData.numTheses > 10){
      newErrors.numTheses = t('general.thesesLimitError');
      newErrorsKeys.numTheses = 'general.thesesLimitError';
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
            {title ? t('title.save') : t('title.add')}
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
          {errors.name && <div className="text-danger">{errors.name}</div>}
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="numTheses">
            {t('general.thesesLimit')}:
          </label>
          <input
              type="number"
              className="form-control"
              id="numTheses"
              name="numTheses"
              value={formData.numTheses}
              onChange={(e) => setFormData({ ...formData, numTheses: parseInt(e.target.value) })}
              min={0}
              max={10}
          />
          {errors.numTheses && <div className="text-danger">{errors.numTheses}</div>}
        </div>
      </form>
    </div>
  );
};

export default TitleForm;
