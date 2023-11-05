import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Axios from 'axios';
import { Program, ProgramDTO } from '../../../models/Program';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { StudyField } from '../../../models/StudyField';
import { Faculty } from '../../../models/Faculty';

const ProgramForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const program = location.state?.program as Program;
  const [oldId, setOldId] = useState<number>();
  const [formData, setFormData] = useState<ProgramDTO>({
    name: '',
    studyFieldAbbr: '',
    specializationAbbr: '',
    studyCyclesId: [],
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

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
        Axios.put(`http://localhost:8080/program/${oldId}`, formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/programs")
          toast.success("Program został zaktualizowany");
        })
        .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
            setAuth({ ...auth, reasonOfLogout: 'token_expired' });
            handleSignOut(navigate);
            }
            toast.error("Program nie został zaktualizowany");
          });
      } else {
        Axios.post('http://localhost:8080/program', formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/programs")
          toast.success("Program został dodany");
        })
        .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
            setAuth({ ...auth, reasonOfLogout: 'token_expired' });
            handleSignOut(navigate);
            }
            toast.error("Program nie został dodany");
          });
      }
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    let isValid = true;

    const errorRequireText = 'Pole jest wymagane.';

    if (!selectedFacultyAbbr) {
      newErrors.faculty = errorRequireText;
      isValid = false;
    }

    if (!formData.studyFieldAbbr) {
        newErrors.studyField = errorRequireText;
        isValid = false;
    }

    if (!formData.name) {
      newErrors.name = errorRequireText;
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  const [availableFaculties, setAvailableFaculties] = useState<Faculty[]>([]);
  const [availableFields, setAvailableFields] = useState<StudyField[]>([]);
  const [availableSpecializations, setAvailableSpecializations] = useState<StudyField[]>([]);
  const [selectedFacultyAbbr, setSelectedFacultyAbbr] = useState<string>();

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

  useEffect(() => {
    Axios.get('http://localhost:8080/specialization', {
      headers: {
        'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
    })
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

  return (
    <div className='page-margin'>
        <form onSubmit={handleSubmit} className="form">
            <div className='d-flex justify-content-begin  align-items-center mb-3'>
                <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
                &larr; Powrót
                </button>
                <button type="submit" className="custom-button">
                {program ? 'Zapisz' : 'Dodaj'}
                </button>
            </div>
            <div className="mb-3">
              <label className="bold" htmlFor="faculty">
                Wydział:
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
                <option value="">Wybierz</option>
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
                Kierunek:
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
                disabled={selectedFacultyAbbr == ""}
                  >
                    <option value={""}>Wybierz</option>
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
                Specjalność:
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
                    <option value={""}>Wybierz</option>
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
                <label className="bold" htmlFor="name">
                Nazwa:
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