import React, { useRef, useState, useEffect, ChangeEvent } from 'react';
import useAuth from "../../auth/useAuth";
import {useLocation, useNavigate} from "react-router-dom";
import handleSignOut from "../../auth/Logout";
import {useTranslation} from "react-i18next";
import api from "../../utils/api";
import { Thesis, ThesisDTO } from '../../models/Thesis';
import { Status } from '../../models/Status';
import { StudyCycle } from '../../models/StudyCycle';
import { Employee } from '../../models/Employee';
import { toast } from 'react-toastify';
import { Program } from '../../models/Program';
import { Student } from '../../models/Student';


function AddThesisPage() {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();

  const navigate = useNavigate();
  const location = useLocation();

  const namePLRef = useRef<HTMLTextAreaElement | null>(null);
  const nameENRef = useRef<HTMLTextAreaElement | null>(null);
  const descriptionPLRef = useRef<HTMLTextAreaElement | null>(null);
  const descriptionENRef = useRef<HTMLTextAreaElement | null>(null);

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [errorKeys, setErrorsKeys] = useState<Record<string, string>>({});
  
  const thesis = location.state?.thesis as Thesis;
  const [formData, setFormData] = useState<ThesisDTO>({
    namePL: '',
    nameEN: '',
    descriptionPL: '',
    descriptionEN: '',
    num_people: 4,
    supervisorId: -1,
    programIds: [-1],
    studyCycleId: -1,
    statusId: -1,
    students: [],
  });

  const [statuses, setStatuses] = useState<Status[]>([]);
  const [studyCycles, setStudyCycles] = useState<StudyCycle[]>([]);
  const [students, setStudents] = useState<Student[]>([]);

  const [programs, setPrograms] = useState<Program[]>([]);
  const [programSuggestions, setProgramSuggestions] = useState<Program[]>([]);

  const [supervisors, setSupervisors] = useState<Employee[]>([]);
  const [supervisorSuggestions, setSupervisorSuggestions] = useState<Employee[]>([]);
  const [mailAbbrev, setMailAbbrev] = useState<string>('');

  const [cycleChosen, setCycleChosen] = useState<boolean>(false);

  useEffect(() => {
    const newErrors: Record<string, string> = {};
    Object.keys(errorKeys).forEach((key) => {
      newErrors[key] = t(errorKeys[key]);
    });
    setErrors(newErrors);
  }, [i18n.language]);

  useEffect(() => {
    api.get('http://localhost:8080/status')
      .then((response) => {
        setStatuses(response.data);
      })
      .catch((error) => {
        if (error.response.status === 401 || error.response.status ===403){
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  useEffect(() => {
    api.get('http://localhost:8080/studycycle')
    .then((response) => {
      setStudyCycles(response.data);
    })
    .catch((error) => {
      if (error.response.status === 401 || error.response.status ===403){
        setAuth({ ...auth, reasonOfLogout: 'token_expired' });
        handleSignOut(navigate);
      }
    });
  }, []);

  useEffect(() => {
    api.get('http://localhost:8080/employee')
    .then((response) => {
      setSupervisors(response.data);
    })
    .catch((error) => {
      if (error.response.status === 401 || error.response.status ===403){
        setAuth({ ...auth, reasonOfLogout: 'token_expired' });
        handleSignOut(navigate);
      }
    });
  }, []);

  useEffect(() => {
    api.get('http://localhost:8080/student')
    .then((response) => {
      setStudents(response.data);
    })
    .catch((error) => {
      if (error.response.status === 401 || error.response.status ===403){
        setAuth({ ...auth, reasonOfLogout: 'token_expired' });
        handleSignOut(navigate);
      }
    });
  }, []);

  useEffect(() => {
    api.get('http://localhost:8080/program')
    .then((response) => {
      setPrograms(response.data);
    })
    .catch((error) => {
      if (error.response.status === 401 || error.response.status ===403){
        setAuth({ ...auth, reasonOfLogout: 'token_expired' });
        handleSignOut(navigate);
      }
    });
  }, []);

  // useEffect(() => {
  //     if (thesis){
  //       setFormData((prevFormData) => {
  //         return {
  //           ...prevFormData,
  //           statusName: statuses.find((status) => status.id === thesis.status.id)?.name || '',
  //           studyCycleName: studyCycles.find((cycle) => cycle.id === thesis.studyCycle?.id)?.name || '',
  //           // specializationAbbr: specializations.find((spec) => spec.id === thesis.specialization?.id)?.name || '',
  //           // studyFieldAbbr = studyFields.find((field) => field.id === thesis.s)
  //           supervisorMail: supervisors.find((supervisor) => supervisor.mail === thesis.supervisor.mail) || '',
  //           // studyCyclesId: thesis.studyCycles.map((cycle) => cycle.id),
  //         }
  //       })
  //     }
  // }, []);

  useEffect(() => {
    if (thesis){
      setFormData((prev) => {
        return{
          ...prev,
          num_people: thesis.num_people,
        }
      });
    }
  }, []);

  const validateForm = () => {
    const newErrors: Record<string, string> =  {};
    const newErrorsKeys: Record<string, string> = {};
    let isValid = true;

    const errorRequireText = t('general.management.fieldIsRequired');

    if (!formData.namePL){
      newErrors.namePL = errorRequireText
      newErrorsKeys.namePL = "general.management.fieldIsRequired";
      isValid = false;
    }

    if (!formData.nameEN){
      newErrors.nameEN = errorRequireText
      newErrorsKeys.nameEN = "general.management.fieldIsRequired";
      isValid = false;
    }

    if (!formData.descriptionPL){
      newErrors.descriptionPL = errorRequireText
      newErrorsKeys.descriptionPL = "general.management.fieldIsRequired";
      isValid = false;
    }

    if (!formData.descriptionEN){
      newErrors.descriptionEN = errorRequireText
      newErrorsKeys.descriptionEN = "general.management.fieldIsRequired";
      isValid = false;
    }

    if (!formData.num_people){
      newErrors.num_people = errorRequireText
      newErrorsKeys.num_people = "general.management.fieldIsRequired";
      isValid = false;
    }

    if (!formData.supervisorId){
      newErrors.supervisor = errorRequireText
      newErrorsKeys.supervisor = "general.management.fieldIsRequired";
      isValid = false;
    }
    // // programs: null,
    // if (!formData.num_people){
    //   newErrors.num_people = errorRequireText
    //   newErrorsKeys.descriptionEN = "general.management.fieldIsRequired";
    //   isValid = false;
    // }

    if (!formData.studyCycleId){
      newErrors.studyCycle = errorRequireText
      newErrorsKeys.studyCycle = "general.management.fieldIsRequired";
      isValid = false;
    }

    if (!formData.statusId){
      newErrors.status = errorRequireText
      newErrorsKeys.status = "general.management.fieldIsRequired";
      isValid = false;
    }

    setErrors(newErrors);
    setErrorsKeys(newErrorsKeys);
    return isValid;
  };

  const handleSearchChange = (e: ChangeEvent<HTMLInputElement>) => {
    const abbrev = e.target.value;
    setMailAbbrev(abbrev);
    const filteredSupervisors = supervisors.filter(
      (supervisor) =>
        supervisor.mail.includes(abbrev.toLowerCase())
    );
    setSupervisorSuggestions(filteredSupervisors);
  };

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleTextAreaChange = (
    event: React.ChangeEvent<HTMLTextAreaElement>,
    textareaRef: React.RefObject<HTMLTextAreaElement>
  ) => {
    const { name, value } = event.target;
    setFormData({
      ...formData,
      [name]: value,
    });
    if (textareaRef.current) {
      const textarea = textareaRef.current;
      textarea.style.height = 'auto';
      textarea.style.height = `${textarea.scrollHeight}px`;
    }
  };

  const handleCycleChange = (selectedCycleId: number) => {
    const updatedProgramSuggestions = 1; // filter program suggestions here
    setFormData({ ...formData, studyCycleId: selectedCycleId});
  }

  const handleProgramChange = (index: number, selectedProgramId: number) => {
    if (!formData.programIds.includes(selectedProgramId)) {
      const updatedPrograms = [...formData.programIds];
      updatedPrograms[index] = selectedProgramId;
      setFormData({ ...formData, programIds: updatedPrograms });
    }
  }

  const handleRemove = (index: number) => {
    const updatedPrograms = [...formData.programIds];
    updatedPrograms.splice(index, 1);
    setFormData({ ...formData, programIds: updatedPrograms });
  }

  const handleAddNext = () => {
    const newProgram = [...formData.programIds];
    newProgram.push(-1);
    setFormData({ ...formData, programIds: newProgram });
  }
  
  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (validateForm()){
      if (thesis){
        api.put(`http://localhost:8080/theses/${thesis.id}`, formData)
        .then(() => {
          navigate("/theses");
          toast.success(t("thesis.updateSuccessful"));
        })
        .catch((error) => {
          console.error(error);
          if (error.response.status === 401 || error.response.status === 403) {
            setAuth({ ...auth, reasonOfLogout: 'token_expired' });
            handleSignOut(navigate);
          }
          navigate("/theses");
          toast.error(t("thesis.updateError"));
        });
      }
      else{
        api.post('http://localhost:8080/theses', formData)
        .then(() => {
          navigate("/theses");
          toast.success(t("thesis.addSuccessful"));
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

  return (
    <div>
      <form onSubmit={(event) => handleSubmit(event)} className="form mb-5">

      <div className='d-flex justify-content-begin  align-items-center mb-3'>
          <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; {t('general.management.goBack')}
          </button>
          <button type="submit" className="custom-button">
          {thesis ? t('general.management.save') : t('general.management.add')}
          </button>
      </div>


      <div className="mb-3">
        <label className="bold" htmlFor="namePL">
          {t('general.title')} (PL):
        </label>
        <textarea
          className="form-control resizable-input"
          id="namePL"
          name="namePL"
          value={formData.namePL}
          onChange={(event) => handleTextAreaChange(event, namePLRef)}
          maxLength={1000}
          ref={namePLRef}
        />
        {errors.namePL && <div className="text-danger">{errors.namePL}</div>}
      </div>
    
      <div className="mb-3">
        <label className="bold" htmlFor="nameEN">
          {t('general.title')} (EN):
        </label>
        <textarea
          className="form-control resizable-input"
          id="nameEN"
          name="nameEN"
          value={formData.nameEN}
          onChange={(event) => handleTextAreaChange(event, nameENRef)}
          maxLength={200}
          ref={nameENRef}
        />
        {errors.nameEN && <div className="text-danger">{errors.nameEN}</div>}
      </div>

      <div className="mb-3">
        <label className="bold" htmlFor="descriptionPL">
          {t('general.university.description')} (PL):
        </label>
        <textarea
          className="form-control resizable-input"
          id="descriptionPL"
          name="descriptionPL"
          value={formData.descriptionPL}
          onChange={(event) => handleTextAreaChange(event, descriptionPLRef)}
          maxLength={1000}
          ref={descriptionPLRef}
        />
        {errors.descriptionPL && <div className="text-danger">{errors.descriptionPL}</div>}
      </div>

      <div className="mb-3">
        <label className="bold" htmlFor="descriptionEN">
          {t('general.university.description')} (EN):
        </label>
        <textarea
            className="form-control resizable-input"
            id="descriptionEN"
            name="descriptionEN"
            value={formData.descriptionEN}
            onChange={(event) => handleTextAreaChange(event, descriptionENRef)}
            maxLength={1000}
            ref={descriptionENRef}
        />
        {errors.descriptionEN && <div className="text-danger">{errors.descriptionEN}</div>}
      </div>

      <div className="mb-3">
        <label className="bold" htmlFor="num_people">
          {t('thesis.peopleLimit')}:
        </label>
        <input
          type="number"
          className="form-control"
          id="num_people"
          name="num_people"
          value={formData.num_people}
          onChange={handleInputChange}
          min={3}
          max={5}
        />
        {errors.num_people && <div className="text-danger">{errors.num_people}</div>}
      </div>

      {/* {/* <div className="mb-3">
        <label className="bold" htmlFor="supervisor">
          {t('general.people.supervisor')}:
        </label>
        <input 
          type="text" 
          id="supervisor"
          name="supervisor"
          value={mailAbbrev} 
          onChange={handleSearchChange}
          className="form-control"
          />
        {suggestions.length > 0 && (
              <select
                id="supervisor"
                name="supervisor"
                className="form-control"
                value={selectedSupervisor ? selectedSupervisor.mail : ''}
                onChange={(e) => {
                  const selected = supervisors.find(
                    (supervisor) => supervisor.mail === e.target.value
                  );
                  if (selected) {
                    handleSupervisorSelect(selected);
                  }
                }}
              >
                <option value="">{t('general.management.choose')}</option>
                {suggestions.map((supervisor) => (
                  <option key={supervisor.mail} value={supervisor.mail}>
                    {supervisor.title.name} {supervisor.surname} {supervisor.name} - {supervisor.mail}
                  </option>
                ))}
              </select>
        )}
        {errors.supervisor && <div className="text-danger">{errors.supervisor}</div>}
      </div> */}

      {/* <div className='mb-3'>
        <label className='bold' htmlFor='supervisor'>
          {t('general.people.supervisor')}:
        </label>
        <div className="dropdown">
          <input
            type="text"
            id="supervisor"
            name="supervisor"
            value={mailAbbrev}
            onChange={handleSearchChange}
            className="form-control"
          />
          {suggestions.length > 0 && (
            <ul className="dropdown-menu form-control" style={{ display: 'block' }}>
              {suggestions.map((supervisor) => (
                <li
                  key={supervisor.mail}
                  className="dropdown-item"
                  onClick={() => handleSupervisorSelect(supervisor)}
                >
                  {supervisor.title.name} {supervisor.surname} {supervisor.name} - {supervisor.mail}
                </li>
              ))}
            </ul>
          )}
        </div>
        {errors.supervisor && <div className="text-danger">{errors.supervisor}</div>}
      </div> */}

      <div className='mb-3'>
        <label className='bold' htmlFor='supervisor'>
          {t('general.people.supervisor')}:
        </label>
        <div className="dropdown">
          <input
            type="text"
            id="supervisor"
            name="supervisor"
            value={mailAbbrev}
            onChange={handleSearchChange}
            list="supervisorList"
            className="form-control"
          />
          <datalist id="supervisorList">
            {supervisorSuggestions.map((supervisor) => (
              <option key={supervisor.mail} value={supervisor.mail}>
                {supervisor.title.name} {supervisor.surname} {supervisor.name} - {supervisor.mail}
              </option>
            ))}
          </datalist>
        </div>
        {errors.supervisor && <div className="text-danger">{errors.supervisor}</div>}
      </div>

      <div className='mb-3'>
        <label className='bold' htmlFor='studyCycle'>
          {t('general.university.studyCycle')}:
        </label>
        <select
          id={'studyCycleSel'}
          name={`studyCycle`}
          value={formData.studyCycleId}
          onChange={(e) => {
            const selectedCycleId = parseInt(e.target.value, 10);
            handleCycleChange(selectedCycleId);
          }}
          className='form-control'
        >
          <option value={-1}>{t('general.management.choose')}</option>
          {studyCycles.map((cyc, cycIndex) => (
            <option key={cycIndex} value={cyc.id}>
              {cyc.name}
            </option>
          ))}
        </select>
        {errors.studyCycle && <div className="text-danger">{errors.studyCycle}</div>}
      </div>

      <div className="mb-3">
        <label className="bold">{t('general.university.studyPrograms')}:</label>  

        <ul>

          {formData.programIds.map((programId, index) => (
            
            <li key={index}>
              <div className='mb-3'>
                <select
                  id={`programId${index}`}
                  name={`programId${index}`}
                  value={formData.programIds[index]} 
                  onChange={(e) => {
                    const selectedProgramId = parseInt(e.target.value, 10);    
                    handleProgramChange(index, selectedProgramId);
                  }}
                  className='form-control'
                  disabled={formData.studyCycleId === -1}
                >
                  <option value={-1}>{t('general.management.choose')}</option>
                  {programs
                    .filter((p) =>
                      p.studyCycles.some((cycle) => cycle.id === formData.studyCycleId)
                    )
                    .map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.name}
                      </option>
                    ))}
                </select>
                {errors.program && <div className="text-danger">{errors.program}</div>}
              </div>
              {formData.programIds.length > 1 && (
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

      <label className="bold" htmlFor="status">
        {t('general.university.status')}:
      </label>
      <select
        id="status"
        name="status"
        value={formData.statusId}
        className="form-control"
        onChange={(s) => setFormData({ ...formData, statusId: parseInt(s.target.value, 10) })}
      >
        <option value={-1}>{t('general.management.choose')}</option>
        {statuses.map((status) => (
          <option key={status.id} value={status.id}>
            {status.name}
          </option>
        ))}
      </select>
      {errors.status && <div className="text-danger">{errors.status}</div>}
      </form>
    </div>
  )
};

export default AddThesisPage;
