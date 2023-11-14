import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Program, ProgramDTO } from '../../../models/Program';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { StudyField } from '../../../models/StudyField';
import { Faculty } from '../../../models/Faculty';
import { StudyCycle } from '../../../models/StudyCycle';
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import { Specialization } from '../../../models/Specialization';

const ProgramForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();
  const program = location.state?.program as Program;
  const [programId, setProgramId] = useState<number>();
  const [formData, setFormData] = useState<ProgramDTO>({
    name: '',
    studyFieldAbbr: '',
    specializationAbbr: '',
    studyCycleIds: [],
    facultyId: -1,
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
    if (program) {
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          name: program.name,
          specializationAbbr: program.specialization?.abbreviation,
          studyFieldAbbr: program.specialization? program.specialization.studyField.abbreviation : program.studyField.abbreviation,
          studyCycleIds: program.studyCycles.map((cycle) => cycle.id),
          facultyId: program.faculty.id,
        };
      });
      setProgramId(program.id);
    }
  }, [program]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      console.log("FormData: ", formData)
      if (program) {
        api.put(`http://localhost:8080/program/${programId}`, formData)
          .then(() => {
            navigate("/programs")
            toast.success(t("program.updateSuccessful"));
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              const newErrors: Record<string, string> = {};
              newErrors.name = t("program.nameExists")
              setErrors(newErrors);
              const newErrorsKeys: Record<string, string> = {};
              newErrorsKeys.index = "program.nameExists"
              setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              navigate("/programs")
              toast.error(t("program.updateError"));
            }
          });
      } else {
        api.post('http://localhost:8080/program', formData)
          .then(() => {
            navigate("/programs")
            toast.success(t("program.addSuccessful"));
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              const newErrors: Record<string, string> = {};
              newErrors.name = t("program.nameExists")
              setErrors(newErrors);
              const newErrorsKeys: Record<string, string> = {};
              newErrorsKeys.index = "program.nameExists"
              setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              navigate("/programs")
              toast.error(t("program.addError"));
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
    const regexPatternForAbbr = /^[A-Z0-9]{1,5}-[A-Z]{1,5}-[A-Z0-9]{1,5}-[A-Z0-9]{1,6}$/;

    if (!formData.facultyId) {
      newErrors.faculty = errorRequireText;
      newErrorsKeys.faculty = 'general.management.fieldIsRequired';
      isValid = false;
    }

    if (!formData.studyFieldAbbr) {
      newErrors.studyField = errorRequireText;
      newErrorsKeys.studyField = 'general.management.fieldIsRequired';
      isValid = false;
    }

    if (!formData.name) {
      newErrors.name = errorRequireText;
      newErrorsKeys.name = 'general.management.fieldIsRequired';
      isValid = false;
    } else if (!regexPatternForAbbr.test(formData.name)) {
      newErrors.name = errorWrongFormat
      newErrorsKeys.name = "general.management.wrongFormat"
      isValid = false;
    }

    if (formData.studyCycleIds.length === 0) {
      newErrors.cycles = t('program.cycleRequired');
      newErrorsKeys.cycles = 'program.cycleRequired';
      isValid = false;
    }

    setErrors(newErrors);
    setErrorsKeys(newErrorsKeys);
    return isValid;
  };

  const [availableFaculties, setAvailableFaculties] = useState<Faculty[]>([]);
  const [availableFields, setAvailableFields] = useState<StudyField[]>([]);
  const [availableSpecializations, setAvailableSpecializations] = useState<Specialization[]>([]);
  const [availableStudyCycles, setAvailableStudyCycles] = useState<StudyCycle[]>([]);

  useEffect(() => {
    api.get('http://localhost:8080/faculty')
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
    api.get('http://localhost:8080/studyfield')
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

  useEffect(() => {
    api.get('http://localhost:8080/specialization')
      .then((response) => {
        setAvailableSpecializations(response.data);
      })
      .catch((error) => {
        console.error(error)
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  useEffect(() => {
    api.get('http://localhost:8080/studycycle')
      .then((response) => {
        setAvailableStudyCycles(response.data);
      })
      .catch((error) => {
        console.error(error)
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  const handleStudyCycleSelection = (cycle: StudyCycle) => {
    const updatedStudyCycles = formData.studyCycleIds.slice();
    if (updatedStudyCycles.includes(cycle.id)) {
      updatedStudyCycles.splice(updatedStudyCycles.indexOf(cycle.id), 1);
    } else {
      updatedStudyCycles.push(cycle.id);
    }
    setFormData({
      ...formData,
      studyCycleIds: updatedStudyCycles,
    });
  };

  return (
    <div className='page-margin'>
      <form onSubmit={handleSubmit} className="form">
        <div className='d-flex justify-content-begin  align-items-center mb-3'>
          <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
            &larr; {t('general.management.goBack')}
          </button>
          <button type="submit" className="custom-button">
            {program ? t('general.management.save') : t('general.management.add')}
          </button>
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="faculty">
            {t('general.university.faculty')}:
          </label>
          <select
            id="faculty"
            name="faculty"
            value={formData.facultyId}
            onChange={(e) => {
              setFormData({
                ...formData,
                facultyId: parseInt(e.target.value, 10),
                studyFieldAbbr: "",
              });
            }}
            className="form-control"
          >
            <option value="">{t('general.management.choose')}</option>
            {availableFaculties.map((faculty) => (
              <option key={faculty.id} value={faculty.id}>
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
              setFormData({
                ...formData,
                studyFieldAbbr: e.target.value,
                specializationAbbr: ""
              });
            }}
            className="form-control"
            disabled={formData.facultyId == -1}
          >
            <option value={""}>{t('general.management.choose')}</option>
            {formData.facultyId !== -1 &&
              availableFields
                .filter((fi) => fi.faculty.id === formData.facultyId)
                .map((field, fIndex) => (
                  <option key={fIndex} value={field.abbreviation}>
                    {field.name}
                  </option>
                ))}
          </select>
          {errors.studyField && <div className="text-danger">{errors.studyField}</div>}
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="specialization">
            {t('general.university.specialization')}:
          </label>
          <select
            id="specialization"
            name="studyField"
            value={formData.specializationAbbr}
            onChange={(e) => {
              setFormData({ ...formData, specializationAbbr: e.target.value })
            }}
            className="form-control"
            disabled={formData.studyFieldAbbr == ""}
          >
            <option value={""}>{t('general.management.choose')}</option>
            {formData.studyFieldAbbr !== "" &&
              availableSpecializations
                .filter((s) => s.studyField.abbreviation === formData.studyFieldAbbr)
                .map((specialization, sIndex) => (
                  <option key={sIndex} value={specialization.abbreviation}>
                    {specialization.name}
                  </option>
                ))}
          </select>
          {errors.specialization && <div className="text-danger">{errors.specialization}</div>}
        </div>
        <div className="mb-3">
          <label className="bold">{t('general.university.studyCycles')}:</label>
          {availableStudyCycles.map((cycle) => (
            <div key={cycle.id} className="mb-2">
              <input
                type="checkbox"
                id={`cycle-${cycle.id}`}
                name={`cycle-${cycle.id}`}
                checked={formData.studyCycleIds.includes(cycle.id)}
                onChange={() => handleStudyCycleSelection(cycle)}
                className="custom-checkbox"
              />
              <label style={{ marginLeft: '5px' }} htmlFor={`cycle-${cycle.id}`}>{cycle.name}</label>
            </div>
          ))}
          {errors.cycles && <div className="text-danger">{errors.cycles}</div>}
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
            {t('program.goodNameFormat')}
          </div>
          {errors.name && <div className="text-danger">{errors.name}</div>}
        </div>
      </form>
    </div>
  );
};

export default ProgramForm;