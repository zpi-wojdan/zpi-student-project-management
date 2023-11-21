import React, { useRef, useState, useEffect, ChangeEvent } from 'react';
import useAuth from "../../auth/useAuth";
import {useLocation, useNavigate} from "react-router-dom";
import handleSignOut from "../../auth/Logout";
import {useTranslation} from "react-i18next";
import api from "../../utils/api";
import { Thesis, ThesisDTO } from '../../models/thesis/Thesis';
import { Status } from '../../models/thesis/Status';
import { StudyCycle } from '../../models/university/StudyCycle';
import { Employee } from '../../models/user/Employee';
import { toast } from 'react-toastify';
import { Program } from '../../models/university/Program';
import Cookies from 'js-cookie';


function AddThesisPageAdmin() {
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
  const [thesisId, setThesisId] = useState<number>();
  const [formData, setFormData] = useState<ThesisDTO>({
    namePL: '',
    nameEN: '',
    descriptionPL: '',
    descriptionEN: '',
    numPeople: 4,
    supervisorId: -1,
    programIds: [-1],
    studyCycleId: -1,
    statusId: -1,
  });

  const [statuses, setStatuses] = useState<Status[]>([]);
  const [studyCycles, setStudyCycles] = useState<StudyCycle[]>([]);

  const [programs, setPrograms] = useState<Program[]>([]);
  const [programSuggestions, setProgramSuggestions] = useState<Program[]>([]);

  const [employees, setEmployees] = useState<Employee[]>([]);
  const [employeeSuggestions, setEmployeeSuggestions] = useState<Employee[]>([]);
  const [mailAbbrev, setMailAbbrev] = useState<string>('');
  const [user, setUser] = useState<Employee | null>(null);

  useEffect(() => {
    const newErrors: Record<string, string> = {};
    Object.keys(errorKeys).forEach((key) => {
      newErrors[key] = t(errorKeys[key]);
    });
    setErrors(newErrors);
  }, [i18n.language]);

  useEffect(() => {
    if (user === null){
      const u = JSON.parse(Cookies.get("user") || "{}")
      setUser(u);
      if (!formData.supervisorId || formData.supervisorId === -1){
        const cookieId = u?.id ?? -1;
        setFormData({ ...formData, supervisorId: cookieId });
      }
    }
  }, [])

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

  useEffect(() => {
    api.get('http://localhost:8080/employee')
    .then((response) => {
      setEmployees(response.data);
    })
    .catch((error) => {
      if (error.response.status === 401 || error.response.status ===403){
        setAuth({ ...auth, reasonOfLogout: 'token_expired' });
        handleSignOut(navigate);
      }
    });
  }, []);

  useEffect(() => {
    const emp = employees.find(elem => elem.id === formData.supervisorId);
    const mail = emp ? emp.mail : '';
    if (mail){
      setMailAbbrev(mail);
    }
  }, [employees, formData.supervisorId])


  useEffect(() => {
    if (thesis){
      setFormData((prev) => {
        return{
          ...prev,
          namePL: thesis.namePL,
          nameEN: thesis.nameEN,
          descriptionPL: thesis.descriptionPL,
          descriptionEN: thesis.descriptionEN,
          numPeople: thesis.numPeople,
          supervisorId: thesis.supervisor.id,
          programIds: thesis.programs.map((p) => p.id),
          studyCycleId: thesis.studyCycle?.id,
          statusId: thesis.status.id,
        };
      });
      setThesisId(thesis.id);
    }
  }, [thesis]);

  const validateForm = () => {
    const newErrors: Record<string, string> =  {};
    const newErrorsKeys: Record<string, string> = {};
    let isValid = true;

    const errorRequireText = t('general.management.fieldIsRequired');
    const errorBigNumberText = t('thesis.numPeopleTooBig');
    const errorSmallNumberText = t('thesis.numPeopleTooSmall');
    const mailDoesNotExistText = t('employee.doesNotExist');
    const regularEmployeeAsSupervisorText = t('employee.cantBecomeASupervisor');

    const mailRegex = /^[a-z0-9-]{1,50}(\.[a-z0-9-]{1,50}){0,4}@(?:student\.)?(pwr\.edu\.pl|pwr\.wroc\.pl)$/;

    if (!formData.namePL || formData.namePL === ''){
      newErrors.namePL = errorRequireText
      newErrorsKeys.namePL = "general.management.fieldIsRequired";
      isValid = false;
    }

    if (!formData.nameEN || formData.nameEN === ''){
      newErrors.nameEN = errorRequireText
      newErrorsKeys.nameEN = "general.management.fieldIsRequired";
      isValid = false;
    }

    const name = statuses.find((s) => s.id === formData.statusId);
    if (!name || name.name !== 'Draft'){
      if (!formData.descriptionPL || formData.namePL === ''){
        newErrors.descriptionPL = errorRequireText
        newErrorsKeys.descriptionPL = "general.management.fieldIsRequired";
        isValid = false;
      }
  
      if (!formData.numPeople){
        newErrors.numPeople = errorRequireText
        newErrorsKeys.numPeople = "general.management.fieldIsRequired";
        isValid = false;
      }
  
      if (formData.numPeople > 5){
        newErrors.numPeople = errorBigNumberText
        newErrorsKeys.numPeople = "thesis.numPeopleTooBig";
        isValid = false;
      }
  
      if (formData.numPeople < 3){
        newErrors.numPeople = errorSmallNumberText
        newErrorsKeys.numPeople = "thesis.numPeopleTooSmall";
        isValid = false;
      }
      
      if ((!formData.supervisorId || formData.supervisorId === -1) &&
            mailAbbrev.trim() === ''){
        newErrors.supervisor = errorRequireText
        newErrorsKeys.supervisor = "general.management.fieldIsRequired";
        isValid = false;
      }
      else if (formData.supervisorId === -1 &&
            (!employees.some(e => e.mail === mailAbbrev ||
              !mailRegex.test(mailAbbrev)))){
        newErrors.supervisor = mailDoesNotExistText
        newErrorsKeys.supervisor = "employee.doesNotExist";
        isValid = false;
      }
      else if (employees.some(e =>
            (e.id === formData.supervisorId &&
              !e.roles.some(r => r.name === 'supervisor')))){
        newErrors.supervisor = regularEmployeeAsSupervisorText
        newErrorsKeys.supervisor = "employee.cantBecomeASupervisor";
        isValid = false;
      }
      
      if (!formData.programIds || formData.programIds.includes(-1)){
        newErrors.programIds = errorRequireText
        newErrorsKeys.programIds = "general.management.fieldIsRequired";
        isValid = false;
      }
  
      if (!formData.studyCycleId || formData.studyCycleId === -1){
        newErrors.studyCycle = errorRequireText
        newErrorsKeys.studyCycle = "general.management.fieldIsRequired";
        isValid = false;
      }
  
      if (!formData.statusId || formData.statusId === -1){
        newErrors.status = errorRequireText
        newErrorsKeys.status = "general.management.fieldIsRequired";
        isValid = false;
      }
    }
    else{
      if (!formData.statusId || formData.statusId === -1){
        newErrors.status = errorRequireText
        newErrorsKeys.status = "general.management.fieldIsRequired";
        isValid = false;
      }
      else{
        if (!formData.supervisorId || formData.supervisorId === -1){
          if (user === null){
            const cookieUser = JSON.parse(Cookies.get("user") || "{}");
            setFormData({ ...formData, supervisorId: cookieUser?.id })
            setUser(cookieUser);
            setMailAbbrev(cookieUser?.mail);
          }
          else{
            setFormData({ ...formData, supervisorId: user?.id })
            setMailAbbrev(user?.name);
          }
        }
        if (!formData.numPeople || formData.numPeople > 5 || formData.numPeople < 3){
          setFormData({ ...formData, numPeople: 4 })
        }
      }
    }

    setErrors(newErrors);
    setErrorsKeys(newErrorsKeys);
    return isValid;
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (validateForm()){
      if (thesis){
        api.put(`http://localhost:8080/thesis/${thesis.id}`, formData)
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
        api.post('http://localhost:8080/thesis', formData)
        .then(() => {
          navigate("/theses");
          toast.success(t("thesis.addSuccessful"));
        })
        .catch((error) => {
          if (error.response && error.response.status === 409) {
            const newErrors: Record<string, string> = {};
            newErrors.index = t("thesis.addError")
            setErrors(newErrors);
            const newErrorsKeys: Record<string, string> = {};
            newErrorsKeys.index = "thesis.addError"
            setErrorsKeys(newErrorsKeys);
          } else {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
            }
            navigate("/theses");
            toast.error(t("thesis.addError"));
          }
        })
      }
    }
  };

  const handleMailSearchChange = (e: ChangeEvent<HTMLInputElement>) => {
    const abbrev = e.target.value;
    setMailAbbrev(abbrev);
    
    const filteredSupervisors = employees.filter(
      (supervisor) =>
        supervisor.mail.includes(abbrev.toLowerCase())
    );
    setEmployeeSuggestions(filteredSupervisors);

    const selectedSupervisor = employeeSuggestions.find(
      (supervisor) => supervisor.mail.toLowerCase() === abbrev.toLowerCase()
    );
    if (selectedSupervisor){
      setFormData({ ...formData, supervisorId: selectedSupervisor.id });
    }
    else{
      setFormData({ ...formData, supervisorId: -1 });
    }
  };

  const handleNumPeopleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
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
    const updatedProgramSuggestions = programs
                  .filter((p) => p.studyCycles
                  .map((c)=> c.id === selectedCycleId));
    setProgramSuggestions(updatedProgramSuggestions);
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

  const statusLabels: { [key:string]:string } = {
    "Draft": t('status.draft'),
    "Pending approval": t('status.pending'),
    "Rejected": t('status.rejected'),
    "Approved": t('status.approved'),
    "Assigned": t('status.assigned'),
    "Closed": t('status.closed')
  }


  return (
    <div className='page-margin'>
      <form noValidate onSubmit={(event) => handleSubmit(event)} className="form">

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
        <div className="text-info">
          {t('general.management.fieldIsOptional')}
        </div>
      </div>

      <div className="mb-3">
        <label className="bold" htmlFor="numPeople">
          {t('thesis.peopleLimit')}:
        </label>
        <input
          type="number"
          className="form-control"
          id="numPeople"
          name="numPeople"
          value={formData.numPeople}
          onChange={handleNumPeopleChange}
          min={3}
          max={5}
        />
        {errors.numPeople && <div className="text-danger">{errors.numPeople}</div>}
      </div>

      

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
          onChange={handleMailSearchChange}
          list="supervisorList"
          className="form-control"
        />
        <datalist id="supervisorList">
          {employeeSuggestions.map((supervisor) => (
            <option 
              key={supervisor.mail} 
              value={supervisor.mail}
              >
              {supervisor.title.name} {supervisor.surname} {supervisor.name}
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
                  {programSuggestions
                    .filter((p) =>
                      p.studyCycles.some((cycle) => cycle.id === formData.studyCycleId)
                    )
                    .map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.name}
                      </option>
                    ))}
                </select>
                {errors.programIds && <div className="text-danger">{errors.programIds}</div>}
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

      <div className='mb-3'>
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
              {statusLabels[status.name] || status.name}
            </option>
          ))}
        </select>
        {errors.status && <div className="text-danger">{errors.status}</div>}
      </div>

      </form>
    </div>
  )
};

export default AddThesisPageAdmin;
