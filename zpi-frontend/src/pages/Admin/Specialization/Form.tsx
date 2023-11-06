import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Axios from 'axios';
import { Specialization } from '../../../models/Specialization';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { StudyField } from '../../../models/StudyField';
import { Faculty } from '../../../models/Faculty';
import {useTranslation} from "react-i18next";

const SpecializationForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();
  const specialization = location.state?.specialization as Specialization;
  const [formData, setFormData] = useState<Specialization>({
    abbreviation: '',
    name: '',
    studyField: {
      abbreviation: '',
      name: '',
    },
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
    if (specialization) {
      setFormData(specialization);
      //setSelectedFacultyAbbr(specialization.studyField.faculty.abbreviation)
      setSelectedFieldAbbr(specialization.studyField?.abbreviation)
    }
  }, [specialization]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      const requestData = {
        abbreviation: formData.abbreviation,
        name: formData.name,
        studyField: availableFields.find((studyField) => studyField.abbreviation === selectedFieldAbbr)
      };
      console.log("Request",requestData)
      if (specialization) {
        console.log(formData)
        Axios.put(`http://localhost:8080/specialization/${formData.abbreviation}`, requestData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/specializations")
          toast.success(t("specialization.updateSuccessful"));
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                const newErrors: Record<string, string> = {};
                newErrors.abbreviation = t('general.management.abbreviationExists')
                setErrors(newErrors);
                const newErrorsKeys: Record<string, string> = {};
                newErrorsKeys.abbreviation = 'general.management.abbreviationExists'
                setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("specialization.updateError"));
            }
          });
      } else {
        console.log(formData)
        Axios.post('http://localhost:8080/specialization', requestData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/specializations")
          toast.success(t("specialization.addSuccessful"));
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                const newErrors: Record<string, string> = {};
                newErrors.abbreviation = t('general.management.abbreviationExists')
                setErrors(newErrors);
                const newErrorsKeys: Record<string, string> = {};
                newErrorsKeys.abbreviation = 'general.management.abbreviationExists'
                setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("specialization.addError"));
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

    if (!selectedFacultyAbbr) {
      newErrors.faculty = errorRequireText;
      newErrorsKeys.faculty = 'general.management.fieldIsRequired';
      isValid = false;
    }

    if (!selectedFieldAbbr) {
        newErrors.studyField = errorRequireText;
        newErrorsKeys.studyField = 'general.management.fieldIsRequired';
        isValid = false;
    }

    if (!formData.abbreviation) {
      newErrors.abbreviation = errorRequireText;
      newErrorsKeys.abbreviation = 'general.management.fieldIsRequired';
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

  const [availableFaculties, setAvailableFaculties] = useState<Faculty[]>([]);
  const [availableFields, setAvailableFields] = useState<StudyField[]>([]);
  const [selectedFacultyAbbr, setSelectedFacultyAbbr] = useState<string>();
  const [selectedFieldAbbr, setSelectedFieldAbbr] = useState<string>();

  useEffect(() => {
    Axios.get('http://localhost:8080/faculty', {
      headers: {
        'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
    })
      .then((response) => {
        setAvailableFaculties(response.data);
      })
      .catch((error) => 
      {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  useEffect(() => {
    Axios.get('http://localhost:8080/studyfield', {
      headers: {
        'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
    })
      .then((response) => {
        setAvailableFields(response.data);
      })
      .catch((error) => {
        console.error(error)
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  return (
    <div className='page-margin'>
        <form onSubmit={handleSubmit} className="form">
            <div className='d-flex justify-content-begin  align-items-center mb-3'>
                <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
                &larr; {t('general.management.goBack')}
                </button>
                <button type="submit" className="custom-button">
                {specialization ? t('general.management.save') : t('general.management.add')}
                </button>
            </div>
            <div className="mb-3">
              <label className="bold" htmlFor="faculty">
                  {t('general.university.faculty')}:
              </label>
              <select
                id="faculty"
                name="faculty"
                value={selectedFacultyAbbr}
                onChange={(e) => {
                    setSelectedFacultyAbbr(e.target.value);
                    setSelectedFieldAbbr("");
                }}
                className="form-control"
                >
                <option value="">{t('general.management.choose')}</option>
                {availableFaculties.map((faculty) => (
                  <option key={faculty.abbreviation} value={faculty.abbreviation}>
                    {faculty.name}
                  </option>
                ))}
              </select>
              {errors.faculty && <div className="text-danger">{errors.faculty}</div>}
            </div>
            <div className="mb-3">
              <label className="bold" htmlFor="studyField">
                  {t('general.university.field')}:
              </label>
              <select
                id="studyField"
                name="studyField"
                value={selectedFieldAbbr}
                onChange={(e) => {
                    setSelectedFieldAbbr(e.target.value);
                }}
                className="form-control"
                disabled={selectedFacultyAbbr == ""}
                  >
                    <option value={""}>{t('general.management.choose')}</option>
                    {/* {selectedFacultyAbbr == "" &&
                      availableFields
                        .filter((fi) => fi.faculty.abbreviation === selectedFacultyAbbr))
                        .map((field, fIndex) => (
                          <option key={fIndex} value={field.abbreviation}>
                            {field.name}
                        tak bedzie jak kierunek będzie miał przypisany wydział i będzie mozna po tym filtrować*/} 
                    {availableFields.map((studyField) => (
                        <option key={studyField.abbreviation} value={studyField.abbreviation}>
                            {studyField.name}
                        </option>
                    ))}
                  </select>
              {errors.studyField && <div className="text-danger">{errors.studyField}</div>}
            </div>
            <div className="mb-3">
                <label className="bold" htmlFor="abbreviation">
                    {t('general.university.code')}:
                </label>
                <input
                type="text"
                id="abbreviation"
                name="abbreviation"
                value={formData.abbreviation}
                onChange={(e) => setFormData({ ...formData, abbreviation: e.target.value })}
                className="form-control"
                disabled={specialization ? true : false}
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

export default SpecializationForm;