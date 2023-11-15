import React, { useRef, useState, useEffect } from 'react';
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
  const descriptionRef = useRef<HTMLTextAreaElement | null>(null);
  
  const thesis = location.state?.thesis as Thesis;
  const [formData, setFormData] = useState<ThesisDTO>({
    namePL: '',
    nameEN: '',
    descriptionPL: '',
    descriptionEN: '',
    num_people: 0,
    supervisor: null,
    // programs: null,
    studyFieldAbbr: '',
    specializationAbbr: '',
    studyCycleName: '',
    statusName: '',
    occupied: 0,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [errorKeys, setErrorKeys] = useState<Record<string, string>>({});

  const [statuses, setStatuses] = useState<Status[]>([]);
  const [studyCycles, setStudyCycles] = useState<StudyCycle[]>([]);
  const [specializations, setSpecializations] = useState<Specialization[]>([]);
  const [studyFields, setStudyFields] = useState<StudyField[]>([]);
  const [supervisors, setSupervisors] = useState<Employee[]>([]);

  const [suggestions, setSuggestions] = useState<SupervisorData[]>([]);

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

      if (thesis){
        setFormData((prevFormData) => {
          return {
            ...prevFormData,
            statusName: statuses.find((status) => status.id === thesis.status.id)?.name || '',
            studyCycleName: studyCycles.find((cycle) => cycle.id === thesis.studyCycle?.id)?.name || '',
            // specializationAbbr: specializations.find((spec) => spec.id === thesis.specialization?.id)?.name || '',
            // studyFieldAbbr = studyFields.find((field) => field.id === thesis.s)
            supervisorMail: supervisors.find((supervisor) => supervisor.mail === thesis.supervisor.mail) || '',
            // studyCyclesId: thesis.studyCycles.map((cycle) => cycle.id),
          }
        })
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

  const handleSupervisorInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setFormState({
      ...formState,
      [name]: value,
    });
  
    if (name === 'supervisorMail') {
      fetchMatchingEmployees(value).then((matchingEmployees) => {
        if (matchingEmployees) {
          setSuggestions(matchingEmployees);
        } else {
          setSuggestions([]);
          console.log("Supervisor does not exist in the database");
        }
      });
    }
  };
  
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

  return (
    <div className='page-margin'>
      <form onSubmit={(event) => handleSubmit(event, formState.status)} className="form">

      <div className='d-flex justify-content-begin  align-items-center mb-3'>
          <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; {t('general.management.goBack')}
          </button>
          <button type="submit" className="custom-button">
          {thesis ? t('general.management.save') : t('general.management.add')}
          </button>
      </div>


      <div className="mb-3">
        <label htmlFor="namePL">{t('general.title')} (PL):</label>
        <textarea
          className="form-control resizable-input"
          id="namePL"
          name="namePL"
          value={formState.namePL}
          onChange={(event) => handleTextAreaChange(event, namePLRef)}
          maxLength={200}
          ref={namePLRef}
        />
        </div>

        <div className="mb-3">
          <label htmlFor="nameEN">{t('general.title')} (EN):</label>
          <textarea
            className="form-control resizable-input"
            id="nameEN"
            name="nameEN"
            value={formState.nameEN}
            onChange={(event) => handleTextAreaChange(event, nameENRef)}
            maxLength={200}
            ref={nameENRef}
          />
        </div>

        <div className="mb-3">
          <label htmlFor="descriptionPL">{t('general.university.description')} (PL):</label>
          <textarea
            className="form-control resizable-input"
            id="descriptionPL"
            name="descriptionPL"
            value={formState.descriptionPL}
            onChange={(event) => handleTextAreaChange(event, descriptionRef)}
            maxLength={1000}
            ref={descriptionRef}
          />
        </div>

        <div className="mb-3">
          <label htmlFor="descriptionEN">{t('general.university.description')} (EN):</label>
          <textarea
              className="form-control resizable-input"
              id="descriptionEN"
              name="descriptionEN"
              value={formState.descriptionEN}
              onChange={(event) => handleTextAreaChange(event, descriptionRef)}
              maxLength={1000}
              ref={descriptionRef}
          />
        </div>

        <div className="mb-3">
          <label htmlFor="num_people">{t('thesis.peopleLimit')}:</label>
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

        <div className="mb-3">
          <label htmlFor="supervisor">{t('general.people.supervisor')}:</label>

          {role == 'employee' && (
            <input
              type="text"
              className="form-control"
              id="supervisor"
              name="supervisor"
              value={formState.supervisorMail}
              readOnly
              disabled
          />
          )}
          {role == 'admin' && (
            <>
              <input
                type="text"
                className="form-control"
                id="supervisorMail"
                name="supervisorMail"
                value={formState.supervisorMail}
                onChange={handleSupervisorInputChange}
                onBlur={handleBlurSupervisor}
            />
            {/* {supervisorError && <div className="text-danger">{supervisorError}</div>} */}
          </>
          )}
          {suggestions.length > 0 && (
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
          )}

        </div>
        <div className="mb-3">
          <label htmlFor="faculty">{t('general.university.fields')}:</label>
          <input
            type="text"
            className="form-control"
            id="faculty"
            name="faculty"
            value={formState.faculty}
            onChange={handleInputChange}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="field">{t('general.university.specialization')}:</label>
          <input
            type="text"
            className="form-control"
            id="field"
            name="field"
            value={formState.field}
            onChange={handleInputChange}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="edu_cycle">{t('general.university.studyCycle')}:</label>
          <input
            type="text"
            className="form-control"
            id="edu_cycle"
            name="edu_cycle"
            value={formState.edu_cycle}
            onChange={handleInputChange}
            minLength={9}
            maxLength={11}
          />
        </div>

        {role === 'admin' && (
          <div className="mb-3">
            <label htmlFor="status">{t('general.university.status')}:</label>
            <input
              type="text"
              id="status"
              name="status"
              value={formState.status}
              onClick={handleInputClick}
              readOnly
              style={{ outline: 'none' }}
              className="form-control"
            />
            {showDropdown && (
              <ul className="list-group">
                {Object.values(StatusEnum).map((cycle, index) => (
                  <li
                    key={index}
                    className="list-group-item list-group-item-hover"
                    onClick={() => handleOptionSelect(cycle)}
                  >
                    {cycle}
                  </li>
                ))}
              </ul>
            )}
          </div>
        )}

        {role === 'employee' && (
          <div>
            <button 
              type="submit" 
              // className={`btn btn-primary ${supervisorError ? "disabled" : ""}`} 
              style={{ marginRight: '10px' }}
              onClick={(event) => handleSubmit(event, 'To be reviewed')}
            >
              {t('thesis.reportThesis')}
            </button>
            <button 
              type="submit" 
              // className={`btn btn-primary ${supervisorError ? "disabled" : ""}`} 
              onClick={(event) => handleSubmit(event, 'Draft')}
            >
              {t('thesis.saveDraft')}
            </button>
          </div>
        )}

        {role === 'admin' && (
          <button
          type="submit"
          // className={`btn btn-primary ${supervisorError ? 'disabled' : ''}`}
          style={{ marginRight: '10px' }}
          onClick={(event) => handleSubmit(event, formState.status)}
      >
            {t('general.management.save')}
      </button>
        )}


        {/* {supervisorError && (
          <div className="text-danger mt-2">{supervisorError}</div>
         )}         */}
      </form>
    </div>
  )
};

export default AddThesisPage;
