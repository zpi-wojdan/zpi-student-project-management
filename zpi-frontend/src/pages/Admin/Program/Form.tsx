import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Program, ProgramDTO } from '../../../models/Program';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { StudyField } from '../../../models/StudyField';
import { Faculty } from '../../../models/Faculty';
import { StudyCycle } from '../../../models/StudyCycle';
import {useTranslation} from "react-i18next";
import api from "../../../utils/api";

const ProgramForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();
  const program = location.state?.program as Program;
  const [oldId, setOldId] = useState<number>();
  const [formData, setFormData] = useState<ProgramDTO>({
    name: '',
    studyFieldAbbr: '',
    specializationAbbr: '',
    studyCyclesId: [],
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
          studyFieldAbbr: program.studyField.abbreviation,
          specializationAbbr: program.specialization?.abbreviation,
          studyCyclesId: program.studyCycles.map((cycle) => cycle.id),
        };
      });
      setOldId(program.id);
      //setSelectedFacultyAbbr(program.studyField.faculty.abbreviation)
    }
  }, [program]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (program) {
        api.put(`http://localhost:8080/program/${oldId}`, formData)
        .then(() => {
          navigate("/programs")
          toast.success(t("program.updateSuccessful"));
        })
        .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
            setAuth({ ...auth, reasonOfLogout: 'token_expired' });
            handleSignOut(navigate);
            }
            toast.error(t("program.updateError"));
          });
      } else {
        api.post('http://localhost:8080/program', formData)
        .then(() => {
          navigate("/programs")
          toast.success(t("program.addSuccessful"));
        })
        .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
            setAuth({ ...auth, reasonOfLogout: 'token_expired' });
            handleSignOut(navigate);
            }
            toast.error(t("program.addError"));
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

    if (!formData.studyFieldAbbr) {
        newErrors.studyField = errorRequireText;
        newErrorsKeys.studyField = 'general.management.fieldIsRequired';
        isValid = false;
    }

    if (!formData.name) {
      newErrors.name = errorRequireText;
      newErrorsKeys.name = 'general.management.fieldIsRequired';
      isValid = false;
    }

    if (formData.studyCyclesId.length === 0) {
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
  const [availableSpecializations, setAvailableSpecializations] = useState<StudyField[]>([]);
  const [availableStudyCycles, setAvailableStudyCycles] = useState<StudyCycle[]>([]);
  const [selectedFacultyAbbr, setSelectedFacultyAbbr] = useState<string>();

  useEffect(() => {
    api.get('http://localhost:8080/faculty')
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
  const updatedStudyCycles = formData.studyCyclesId.slice();
  if (updatedStudyCycles.includes(cycle.id)) {
    updatedStudyCycles.splice(updatedStudyCycles.indexOf(cycle.id), 1);
  } else {
    updatedStudyCycles.push(cycle.id);
  }
  setFormData({
    ...formData,
    studyCyclesId: updatedStudyCycles,
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
                value={selectedFacultyAbbr}
                onChange={(e) => {
                    setSelectedFacultyAbbr(e.target.value);
                    setFormData({ ...formData, studyFieldAbbr: "" });
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
                    setFormData({
                      ...formData,
                      studyFieldAbbr: e.target.value,
                      specializationAbbr: ""
                    });
                }}
                className="form-control"
                disabled={!selectedFacultyAbbr}
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
                    {/* {formData.studyFieldAbbr == "" &&
                      availableSpecializations
                        .filter((s) => s.studyField.abbreviation === formData.studyFieldAbbr))
                        .map((specialization, sIndex) => (
                          <option key={sIndex} value={specialization.abbreviation}>
                            {specialization.name}
                        */} 
                    {availableSpecializations.map((specialization) => (
                        <option key={specialization.abbreviation} value={specialization.abbreviation}>
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
                  checked={formData.studyCyclesId.includes(cycle.id)}
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
                {errors.name && <div className="text-danger">{errors.name}</div>}
            </div>
        </form>
    </div>
  );
};

export default ProgramForm;