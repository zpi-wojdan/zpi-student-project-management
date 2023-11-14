import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { StudentDTO } from '../../../models/Student';
import { Program } from '../../../models/Program';
import { toast } from 'react-toastify';
import { StudyCycle } from '../../../models/StudyCycle';
import { StudentProgramCycle, StudentProgramCycleDTO } from '../../../models/StudentProgramCycle';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";

const StudentForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const [studentId, setStudentId] = useState<number>();
  const [formData, setFormData] = useState<StudentDTO>({
    name: '',
    surname: '',
    index: '',
    status: '',
    programsCycles: [{ cycleId: -1, programId: -1 }],
  });

  const navigate = useNavigate();
  const location = useLocation();
  const student = location.state?.student;
  const statusOptions = ['STU', 'Inny/Other'];
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
    if (student) {
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          name: student.name,
          surname: student.surname,
          index: student.index,
          status: student.status,
          programsCycles: student.studentProgramCycles.map((programCycle: StudentProgramCycle) => ({
            cycleId: programCycle.cycle.id,
            programId: programCycle.program.id,
          })),
        };
      });
      setStudentId(student.id)
    }
  }, [student]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (student) {
        api.put(`http://localhost:8080/student/${studentId}`, formData)
          .then(() => {
            navigate("/students");
            toast.success(t("student.updateSuccessful"));
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              const newErrors: Record<string, string> = {};
              newErrors.index = t("student.indexExists")
              setErrors(newErrors);
              const newErrorsKeys: Record<string, string> = {};
              newErrorsKeys.index = "student.indexExists"
              setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              navigate("/students");
              toast.error(t("student.updateError"));
            }
          });
      } else {
        api.post('http://localhost:8080/student', formData)
          .then(() => {
            navigate("/students");
            toast.success(t("student.addSuccessful"));
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              const newErrors: Record<string, string> = {};
              newErrors.index = t("student.indexExists")
              setErrors(newErrors);
              const newErrorsKeys: Record<string, string> = {};
              newErrorsKeys.index = "student.indexExists"
              setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              navigate("/students");
              toast.error(t("student.addError"));
            }
          })
      }
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    const newErrorsKeys: Record<string, string> = {};
    let isValid = true;
    const errorRequireText = t('general.management.fieldIsRequired');

    if (!formData.name) {
      newErrors.name = errorRequireText;
      newErrorsKeys.name = 'general.management.fieldIsRequired';
      isValid = false;
    }

    if (!formData.surname) {
      newErrors.surname = errorRequireText;
      newErrorsKeys.surname = 'general.management.fieldIsRequired';
      isValid = false;
    }

    if (!formData.index) {
      newErrors.index = errorRequireText;
      newErrorsKeys.index = 'general.management.fieldIsRequired';
      isValid = false;
    } else if (!/^\d{6}$/.test(formData.index)) {
      newErrors.index = t('student.indexLength');
      newErrorsKeys.index = 'student.indexLength';
      isValid = false;
    }

    if (formData.programsCycles.some((programCycle) => programCycle.cycleId === -1 || programCycle.programId === -1)) {
      newErrors.studentProgramCycles = t('student.cycleProgramRequired');
      newErrorsKeys.studentProgramCycles = 'student.cycleProgramRequired';
      isValid = false;
    }

    if (hasDuplicateProgramCycle(formData.programsCycles)) {
      newErrors.studentProgramCycles = t('student.duplicatedPrograms');
      newErrorsKeys.studentProgramCycles = 'student.duplicatedPrograms';
      isValid = false;
    }

    if (!formData.status) {
      newErrors.status = errorRequireText;
      newErrorsKeys.status = 'general.management.fieldIsRequired';
      isValid = false;
    }

    setErrors(newErrors);
    setErrorsKeys(newErrorsKeys);
    return isValid;
  };

  const hasDuplicateProgramCycle = (programCycles: StudentProgramCycleDTO[]) => {
    const uniquePairs = new Set();
    for (const programCycle of programCycles) {
      const pair = JSON.stringify(programCycle);
      if (uniquePairs.has(pair)) {
        return true;
      }
      uniquePairs.add(pair);
    }
    return false;
  };

  const [availableCycles, setAvailableCycles] = useState<StudyCycle[]>([]);
  const [availablePrograms, setAvailablePrograms] = useState<Program[]>([]);

  useEffect(() => {
    api.get('http://localhost:8080/studycycle')
      .then((response) => {
        setAvailableCycles(response.data);
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
    api.get('http://localhost:8080/program')
      .then((response) => {
        setAvailablePrograms(response.data);
        console.log(availablePrograms)
      })
      .catch((error) => {
        console.error(error)
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  const handleCycleChange = (index: number, selectedCycleId: number) => {
    const updatedStudentProgramCycles = [...formData.programsCycles];
    updatedStudentProgramCycles[index].cycleId = selectedCycleId;
    updatedStudentProgramCycles[index].programId = -1;

    setFormData({ ...formData, programsCycles: updatedStudentProgramCycles });
  };

  const handleProgramChange = (index: number, selectedProgramId: number) => {
    const updatedStudentProgramCycles = [...formData.programsCycles];
    updatedStudentProgramCycles[index].programId = selectedProgramId;

    setFormData({ ...formData, programsCycles: updatedStudentProgramCycles });
  };

  const handleAddNext = () => {
    const newStudentProgramCycles = [...formData.programsCycles];
    newStudentProgramCycles.push({ cycleId: -1, programId: -1 });
    setFormData({ ...formData, programsCycles: newStudentProgramCycles });
  };

  const handleRemove = (index: number) => {
    const updatedStudentProgramCycles = [...formData.programsCycles];
    updatedStudentProgramCycles.splice(index, 1);
    setFormData({ ...formData, programsCycles: updatedStudentProgramCycles });
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="form">
        <div className='d-flex justify-content-begin align-items-center mb-3'>
          <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
            &larr; {t('general.management.goBack')}
          </button>
          <button type="submit" className="custom-button">
            {student ? t('general.management.save') : t('general.management.add')}
          </button>
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="name">
            {t('general.people.name')}:
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
          <label className="bold" htmlFor="surname">
            {t('general.people.surname')}:
          </label>
          <input
            type="text"
            id="surname"
            name="surname"
            value={formData.surname}
            onChange={(e) => setFormData({ ...formData, surname: e.target.value })}
            className="form-control"
          />
          {errors.surname && <div className="text-danger">{errors.surname}</div>}
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="index">
            {t('general.people.index')}:
          </label>
          <input
            type="text"
            id="index"
            name="index"
            value={formData.index}
            onChange={(e) => setFormData({ ...formData, index: e.target.value })}
            className="form-control"
          />
          {errors.index && <div className="text-danger">{errors.index}</div>}
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="status">
            {t('general.university.status')}:
          </label>
          <select
            id="status"
            name="status"
            value={formData.status}
            onChange={(e) => setFormData({ ...formData, status: e.target.value })}
            className="form-control"
          >
            <option value="">{t('general.management.choose')}</option>
            {statusOptions.map((status, index) => (
              <option key={index} value={status}>
                {status}
              </option>
            ))}
          </select>
          {errors.status && <div className="text-danger">{errors.status}</div>}
        </div>
        <div>
          <label className="bold">{t('general.university.studyPrograms')}:</label>
          <ul>
            {formData.programsCycles.map((programCycle, index) => (
              <li key={index}>
                <div className="mb-3">
                  <label className="bold" htmlFor={`cycle${index}`}>
                    {t('general.university.studyCycle')}:
                  </label>
                  <select
                    id={`cycle${index}`}
                    name={`cycle${index}`}
                    value={programCycle.cycleId > -1 ? programCycle.cycleId : ""}
                    onChange={(e) => {
                      const selectedCycleId = parseInt(e.target.value, 10);
                      handleCycleChange(index, selectedCycleId);
                    }}
                    className="form-control"
                  >
                    <option value={-1}>{t('general.management.choose')}</option>
                    {availableCycles.map((c, cIndex) => (
                      <option key={cIndex} value={c.id}>
                        {c.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="mb-3">
                  <label className="bold" htmlFor={`program${index}`}>
                    {t('general.university.studyProgram')}:
                  </label>
                  <select
                    id={`program${index}`}
                    name={`program${index}`}
                    value={programCycle.programId}
                    onChange={(e) => {
                      const selectedProgramId = parseInt(e.target.value, 10);
                      handleProgramChange(index, selectedProgramId);
                    }}
                    className="form-control"
                    disabled={programCycle.cycleId == -1}
                  >
                    <option value={-1}>{t('general.management.choose')}</option>
                    {programCycle.cycleId !== -1 &&
                      availablePrograms
                        .filter((p) => p.studyCycles.some((c) => c.id === programCycle.cycleId))
                        .map((program, pIndex) => (
                          <option key={pIndex} value={program.id}>
                            {program.name}
                          </option>
                        ))}
                  </select>
                </div>
                {formData.programsCycles.length > 1 && (
                  <button
                    type="button"
                    className="custom-button another-color"
                    onClick={() => handleRemove(index)}
                  >
                    {t('general.management.delete')}
                  </button>
                )}
              </li>
            ))}
            {errors.studentProgramCycles && <div className="text-danger">{errors.studentProgramCycles}</div>}
            <li>
              <button
                type="button"
                className="custom-button"
                onClick={handleAddNext}
              >
                {t('general.management.addNext')}
              </button>
            </li>
          </ul>
        </div>
      </form>
    </div>
  );
}

export default StudentForm;
