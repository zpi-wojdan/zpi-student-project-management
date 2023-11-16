import React, { useRef, useState, useEffect, ChangeEvent } from 'react';
import { SupervisorData, AddUpdateThesisProps, StatusEnum } from '../../utils/types';
import useAuth from "../../auth/useAuth";
import {useLocation, useNavigate} from "react-router-dom";
import handleSignOut from "../../auth/Logout";
import {useTranslation} from "react-i18next";
import api from "../../utils/api";
import { Thesis, ThesisDTO } from '../../models/Thesis';
import { Status } from '../../models/Status';
import { StudyCycle } from '../../models/StudyCycle';
import { Specialization } from '../../models/Specialization';
import { StudyField } from '../../models/StudyField';
import { Employee } from '../../models/Employee';


function AddThesisPage({ role, mail }: AddUpdateThesisProps) {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();

  const navigate = useNavigate();
  const location = useLocation();

  const namePLRef = useRef<HTMLTextAreaElement | null>(null);
  const nameENRef = useRef<HTMLTextAreaElement | null>(null);
  const descriptionPLRef = useRef<HTMLTextAreaElement | null>(null);
  const descriptionENRef = useRef<HTMLTextAreaElement | null>(null);
  
  const thesis = location.state?.thesis as Thesis;
  const [formData, setFormData] = useState<ThesisDTO>({
    namePL: '',
    nameEN: '',
    descriptionPL: '',
    descriptionEN: '',
    num_people: 0,
    supervisor: null,
    // programs: null,
    studyField: '',
    specialization: '',
    studyCycle: '',
    status: '',
    occupied: 0,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [errorKeys, setErrorKeys] = useState<Record<string, string>>({});

  const [statuses, setStatuses] = useState<Status[]>([]);
  const [studyCycles, setStudyCycles] = useState<StudyCycle[]>([]);
  const [specializations, setSpecializations] = useState<Specialization[]>([]);
  const [studyFields, setStudyFields] = useState<StudyField[]>([]);

  const [supervisors, setSupervisors] = useState<Employee[]>([]);
  const [suggestions, setSuggestions] = useState<Employee[]>([]);
  const [mailAbbrev, setMailAbbrev] = useState<string>('');
  const [selectedSupervisor, setSelectedSupervisor] = useState<Employee | null>(null);

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
    api.get('http://localhost:8080/specialization')
    .then((response) => {
      setSpecializations(response.data);
    })
    .catch((error) => {
      if (error.response.status === 401 || error.response.status ===403){
        setAuth({ ...auth, reasonOfLogout: 'token_expired' });
        handleSignOut(navigate);
      }
    });
  }, []);

  useEffect(() => {
    api.get('http://localhost:8080/studyfield')
    .then((response) => {
      setStudyFields(response.data);
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
  });

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

    // ...and so on

    setErrors(newErrors);
    setErrorKeys(newErrorsKeys);
    return isValid;
  };

  const handleSearchChange = (e: ChangeEvent<HTMLInputElement>) => {
    const abbrev = e.target.value;
    setMailAbbrev(abbrev);
    const filteredSupervisors = supervisors.filter(
      (supervisor) =>
        supervisor.mail.includes(abbrev.toLowerCase())
    );
    setSuggestions(filteredSupervisors);
  };

  const [showDropdown, setShowDropdown] = useState(false);

  const [formState, setFormState] = useState({
    namePL: '',
    nameEN: '',
    descriptionPL: '',
    descriptionEN: '',
    num_people: '4',
    supervisorMail: '',
    supervisor: {} as SupervisorData,
    faculty: '',
    field: '',
    edu_cycle: '',
    status: '',
  });

  useEffect(() => {
    if (role === 'employee') {
      setFormState((prevState) => ({
        ...prevState,
        supervisorMail: mail!,
      }));
    } else {

      setFormState((prevState) => ({
        ...prevState,
        supervisorMail: '',
      }));
    }
  }, [role, mail]);

  const fetchSupervisor = async (supervisorMail: string): Promise<SupervisorData | null> => {
    try{
      const response = await fetch(`http://localhost:8080/employee/${supervisorMail}`);

      if (response.ok){
        const supervisorData: SupervisorData = await response.json();
        return supervisorData;
      }
      return null;
    }
    catch (error) {
      console.error("Error fetching supervisor data: ", error);
      throw error;
    }
  }

  const fetchMatchingEmployees = async (supervisorMail: string): Promise<SupervisorData[] | null> => {
    try {
      const response = await fetch(`http://localhost:8080/employee/match/${supervisorMail}`);
  
      if (response.ok) {
        const supervisorData: SupervisorData[] = await response.json();
        return supervisorData;
      }
      return null;
    } catch (error) {
      console.error("Error fetching supervisor data: ", error);
      throw(error)
    }
  };
  
  const handleOptionSelect = (value: string) => {
    const selectedStatus = value || '';
    setFormState({
      ...formState,
      status: value,
    });
    setShowDropdown(false);
  };

  const handleInputClick = () => {
    setShowDropdown(!showDropdown);
  };

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setFormState({
      ...formState,
      [name]: value,
    });
  };

  const handleTextAreaChange = (
    event: React.ChangeEvent<HTMLTextAreaElement>,
    textareaRef: React.RefObject<HTMLTextAreaElement>
  ) => {
    const { name, value } = event.target;
    setFormState({
      ...formState,
      [name]: value,
    });
    if (textareaRef.current) {
      const textarea = textareaRef.current;
      textarea.style.height = 'auto';
      textarea.style.height = `${textarea.scrollHeight}px`;
    }
  };

  const handleBlurSupervisor = async (event: React.FocusEvent<HTMLInputElement>) => {
    const supervisorMail = event.target.value;

    if (supervisorMail){
      try{
        const supervisorData = await fetchSupervisor(supervisorMail);

        if (supervisorData != null){
          setFormState({
            ...formState,
            supervisor: supervisorData,
          });
          // setSupervisorError(null);
          // setSupervisorErrorKey(null);

        }
        else{
          // setSupervisorError(t('thesis.supervisorNotExists'));
          // setSupervisorErrorKey('thesis.supervisorNotExists')
        }

      }
      catch(error) {
        // setSupervisorError(t('thesis.errorOfLoadingSupervisorData'));
        // setSupervisorErrorKey('thesis.errorOfLoadingSupervisorData')
        console.log("Error fetching supervisor data: ", error);
      }
    }
  }

  const handleSuggestionClick = (selectedEmployee: SupervisorData) => {
    const selectedMail = selectedEmployee.mail || '';
    setFormState({
      ...formState,
      supervisorMail: selectedMail, 
      supervisor: selectedEmployee,
    });
    setSuggestions([]); 
  };

  // const handleSupervisorInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
  //   const { name, value } = event.target;
  //   setFormState({
  //     ...formState,
  //     [name]: value,
  //   });
  
  //   if (name === 'supervisorMail') {
  //     fetchMatchingEmployees(value).then((matchingEmployees) => {
  //       if (matchingEmployees) {
  //         setSuggestions(matchingEmployees);
  //       } else {
  //         setSuggestions([]);
  //         console.log("Supervisor does not exist in the database");
  //       }
  //     });
  //   }
  // };
  
  const handleSubmit = async (event: React.FormEvent, status: string) => {
    event.preventDefault();
    
    const formData = {
      namePL: formState.namePL,
      nameEN: formState.nameEN,
      descriptionPL: formState.descriptionPL,
      descriptionEN: formState.descriptionEN,
      num_people: parseInt(formState.num_people, 10),
      supervisor: {
        mail: formState.supervisorMail,
        name: formState.supervisor.name,
        surname: formState.supervisor.surname,
        role: formState.supervisor.role,
        department_symbol: formState.supervisor.department_symbol,
        title: formState.supervisor.title,
      },
      faculty: formState.faculty,
      field: formState.field,
      edu_cycle: formState.edu_cycle,
      status: status,
    };
  
    console.log(formData);
  
    try {
      const response = await api.post('http://localhost:8080/thesis', formData);
  
      if (response.status === 201) {
        console.log('Request was successful');
      } else {
        console.log('POST request was not successful');
      }
    } catch (error: any) {
      console.error('An error occurred in POST request:', error);
      if (error.response.status === 401 || error.response.status === 403) {
        setAuth({ ...auth, reasonOfLogout: 'token_expired' });
        handleSignOut(navigate);
      }
    }
  };

  const handleSupervisorSelect = (supervisor: Employee) => {
    setSelectedSupervisor(supervisor);
    setMailAbbrev('');
    setSuggestions([]);
  };

  return (
    <div>
      <form onSubmit={(event) => handleSubmit(event, formState.status)} className="form mb-5">

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
        </div>
        {errors.namePL && <div className="text-danger">{errors.namePL}</div>}

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
        </div>
        {errors.nameEN && <div className="text-danger">{errors.nameEN}</div>}

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
        </div>
        {errors.descriptionPL && <div className="text-danger">{errors.descriptionPL}</div>}

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
        </div>
        {errors.descriptionEN && <div className="text-danger">{errors.descriptionEN}</div>}

        <div className="mb-3">
          <label className="bold" htmlFor="num_people">
            {t('thesis.peopleLimit')}:
          </label>
          <input
            type="number"
            className="form-control"
            id="num_people"
            name="num_people"
            value={formState.num_people}
            onChange={handleInputChange}
            min={3}
            max={6}
          />
        </div>
        {errors.num_people && <div className="text-danger">{errors.num_people}</div>}

        <div className="mb-3">
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
              <div className="form-control">
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
                      {supervisor.title.name} {supervisor.surname} {supervisor.name}
                    </option>
                  ))}
                </select>
              </div>
          )}
          {/* {role == 'employee' && (
            <input
              type="text"
              className="form-control"
              id="supervisor"
              name="supervisor"
              value={formState.supervisorMail}
              readOnly
              disabled
          />
          )} */}
          {/* {role == 'admin' && (
            <>
              <input
                type="text"
                className="form-control"
                id="supervisorMail"
                name="supervisorMail"
                value={formState.supervisorMail}
                onChange={handleSupervisorInputChange}
                onBlur={handleBlurSupervisor}
            /> */}
            {/* {supervisorError && <div className="text-danger">{supervisorError}</div>} */}
          {/* </>
          )} */}
          {/* {suggestions.length > 0 && (
            <ul className="list-group">
              {suggestions.map((employee) => (
                <li
                  key={employee.mail}
                  className="list-group-item list-group-item-hover"
                  onClick={() => handleSuggestionClick(employee)}
                >
                  {employee.title} {employee.name} {employee.surname} - {employee.mail}
                </li>
              ))}
            </ul>
          )} */}
        {errors.supervisor && <div className="text-danger">{errors.supervisor}</div>}
        </div>

        <div className="mb-3">
          <label className="bold">{t('general.university.studyPrograms')}:</label>  

          <ul>

              <li key='studyCycleLi'>
                <div className='mb-3'>
                  <label className='bold' htmlFor='studyCycle'>
                    {t('general.university.studyCycle')}:
                  </label>
                  <select
                    id={'studyCycleSel'}
                    name={`studyCycle`}
                    value={formData.studyCycle}
                    onChange={(cyc) => setFormData({ ...formData, studyCycle: cyc.target.value })}
                    className='form-control'
                  >
                    <option value={-1}>{t('general.management.choose')}</option>
                    {studyCycles.map((cyc, cycIndex) => (
                      <option key={cycIndex} value={cyc.id}>
                        {cyc.name}
                      </option>
                    ))}
                  </select>
                </div>
              </li>

              <li key='studyFieldsLi'>
                <div className='mb-3'>
                  <label className='bold' htmlFor='studyFields'>
                    {t('general.university.field')}:
                  </label>
                  <select
                    id={'studyFieldsSel'}
                    name={`studyFields`}
                    value={formData.studyField}
                    onChange={(cyc) => setFormData({ ...formData, studyField: cyc.target.value })}
                    className='form-control'
                  >
                    <option value={-1}>{t('general.management.choose')}</option>
                    {studyFields.map((cyc, cycIndex) => (
                      <option key={cycIndex} value={cyc.id}>
                        {cyc.name}
                      </option>
                    ))}
                  </select>
                </div>
              </li>

              <li key='specializationLi'>
                <div className='mb-3'>
                  <label className='bold' htmlFor='specialization'>
                    {t('general.university.specialization')}:
                  </label>
                  <select
                    id={'specializationSel'}
                    name={`specialization`}
                    value={formData.specialization}
                    onChange={(cyc) => setFormData({ ...formData, specialization: cyc.target.value })}
                    className='form-control'
                  >
                    <option value={-1}>{t('general.management.choose')}</option>
                    {specializations.map((cyc, cycIndex) => (
                      <option key={cycIndex} value={cyc.id}>
                        {cyc.name}
                      </option>
                    ))}
                  </select>
                </div>
              </li>

          </ul>
        </div>
        {errors.edu_cycle && <div className="text-danger">{errors.edu_cycle}</div>}

        <div className="mb-3">
          <label className="bold" htmlFor="faculty">{t('general.university.fields')}:</label>
          <input
            type="text"
            className="form-control"
            id="faculty"
            name="faculty"
            value={formState.faculty}
            onChange={handleInputChange}
          />
        </div>
        {errors.supervisor && <div className="text-danger">{errors.supervisor}</div>}

        <div className="mb-3">
          <label className="bold" htmlFor="field">{t('general.university.specialization')}:</label>
          <input
            type="text"
            className="form-control"
            id="field"
            name="field"
            value={formState.field}
            onChange={handleInputChange}
          />
        </div>
        {errors.field && <div className="text-danger">{errors.field}</div>}

        <label className="bold" htmlFor="status">
          {t('general.university.status')}:
        </label>
        <select
          id="status"
          name="status"
          value={formData.status}
          className="form-control"
          onChange={(e) => setFormData({ ...formData, status: e.target.value })}
        >
          <option value={-1}>{t('general.management.choose')}</option>
          {statuses.map((status) => (
            <option key={status.id} value={status.name}>
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
