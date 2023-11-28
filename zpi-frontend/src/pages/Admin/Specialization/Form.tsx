import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Specialization, SpecializationDTO } from '../../../models/university/Specialization';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { StudyField } from '../../../models/university/StudyField';
import { Faculty } from '../../../models/university/Faculty';
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import api_access from '../../../utils/api_access';

const SpecializationForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();
  const specialization = location.state?.specialization as Specialization;
  const [specializationId, setSpecializationId] = useState<number>();
  const [formData, setFormData] = useState<SpecializationDTO>({
    abbreviation: '',
    name: '',
    studyFieldAbbr: '',
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
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          abbreviation: specialization.abbreviation,
          name: specialization.name,
          studyFieldAbbr: specialization.studyField.abbreviation,
        };
      });
      setSelectedFacultyAbbr(specialization.studyField.faculty.abbreviation)
      setSpecializationId(specialization.id)
    }
  }, [specialization]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (specialization) {
        api.put(api_access + `specialization/${specializationId}`, formData)
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
        api.post(api_access + 'specialization', formData)
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
    const errorWrongFormat = t("general.management.wrongFormat");
    const regexPatternForAbbr = /^[A-Z]{3}$/;

    if (!selectedFacultyAbbr) {
      newErrors.faculty = errorRequireText;
      newErrorsKeys.faculty = 'general.management.fieldIsRequired';
      isValid = false;
    }

    if (!formData.studyFieldAbbr) {
      newErrors.studyField = errorRequireText;
      newErrorsKeys.studyField = 'general.management.fieldIsRequired';
      isValid = false;
    }

    if (!formData.abbreviation) {
      newErrors.abbreviation = errorRequireText;
      newErrorsKeys.abbreviation = 'general.management.fieldIsRequired';
      isValid = false;
    } else if (!regexPatternForAbbr.test(formData.abbreviation)) {
      newErrors.abbreviation = errorWrongFormat
      newErrorsKeys.abbreviation = "general.management.wrongFormat"
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

  useEffect(() => {
    api.get(api_access + 'faculty')
      .then((response) => {
        setAvailableFaculties(response.data);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  useEffect(() => {
    api.get(api_access + 'studyfield')
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
            {specialization ? t('specialization.save') : t('specialization.add')}
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
              setFormData({ ...formData, studyFieldAbbr: "" })
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
            value={formData.studyFieldAbbr}
            onChange={(e) => {
              setFormData({ ...formData, studyFieldAbbr: e.target.value });
            }}
            className="form-control"
            disabled={selectedFacultyAbbr === ""}
          >
            <option value={""}>{t('general.management.choose')}</option>
            {selectedFacultyAbbr !== "" &&
              availableFields
                .filter((fi) => fi.faculty.abbreviation === selectedFacultyAbbr)
                .map((field, fIndex) => (
                  <option key={fIndex} value={field.abbreviation}>
                    {field.name}
                  </option>
                ))}
          </select>

          {errors.studyField && <div className="text-danger">{errors.studyField}</div>}
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
            {t('specialization.goodAbbrFormat')}
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

export default SpecializationForm;