import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Axios from 'axios';
import { StudyField, StudyFieldDTO } from '../../../models/StudyField';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import { Faculty } from '../../../models/Faculty';
import handleSignOut from "../../../auth/Logout";
import useAuth from '../../../auth/useAuth';

const StudyFieldForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const studyField = location.state?.studyField as StudyField;
  const [oldAbbr, setOldAbbr] = useState<String>();
  const [formData, setFormData] = useState<StudyFieldDTO>({
    abbreviation: '',
    name: '',
    facultyAbbr: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [faculties, setFaculties] = useState<Faculty[]>([]);

  useEffect(() => {
    Axios.get('http://localhost:8080/faculty', {
      headers: {
        'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
    })
      .then((response) => {
        setFaculties(response.data);
        if (studyField) {
          formData.abbreviation = studyField.abbreviation;
          formData.name = studyField.name;
          formData.facultyAbbr = findFacultyAbbrByField(studyField.abbreviation);
          setOldAbbr(studyField.abbreviation);
        }
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  function findFacultyAbbrByField(fieldAbbr: string): string {
    for (const faculty of faculties) {
        for (const field of faculty.studyFields) {
            if (field.abbreviation === fieldAbbr) {
                return faculty.abbreviation;
            }
        }
    }
    return "";
  }

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      console.log(formData);
      if (studyField) {
        Axios.put(`http://localhost:8080/studyfield/${oldAbbr}`, formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/fields")
          toast.success("Kierunek został zaktualizowany");
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                const newErrors: Record<string, string> = {};
                newErrors.abbreviation = 'Podany skrót już istnieje!';
                setErrors(newErrors);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error("Kierunek nie został zaktualizowany");
            }
          });
      } else {
        Axios.post('http://localhost:8080/studyfield', formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/fields")
          toast.success("Kierunek został dodany");
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                const newErrors: Record<string, string> = {};
                newErrors.abbreviation = 'Podany skrót już istnieje!';
                setErrors(newErrors);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error("Kierunek nie został dodany");
            }
          });
      }
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    let isValid = true;

    const errorRequireText = 'Pole jest wymagane.';

    if (!formData.abbreviation) {
      newErrors.abbreviation = errorRequireText;
      isValid = false;
    }

    if (!formData.name) {
      newErrors.name = errorRequireText;
      isValid = false;
    }

    if (!formData.facultyAbbr) {
      newErrors.faculty = errorRequireText;
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  return (
    <div className='page-margin'>
        <form onSubmit={handleSubmit} className="form">
            <div className='d-flex justify-content-begin  align-items-center mb-3'>
                <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
                &larr; Powrót
                </button>
                <button type="submit" className="custom-button">
                {studyField ? 'Zapisz' : 'Dodaj'}
                </button>
            </div>
            <div className="mb-3">
              <label className="bold" htmlFor="faculty">
                Wydział:
              </label>
              <select
                id="faculty"
                name="faculty"
                value={formData.facultyAbbr}
                onChange={(e) => setFormData({ ...formData, facultyAbbr: e.target.value })}
                className="form-control"
              >
                <option value="">Wybierz</option>
                {faculties.map((faculty) => (
                  <option key={faculty.abbreviation} value={faculty.abbreviation}>
                    {faculty.name}
                  </option>
                ))}
              </select>
              {errors.faculty && <div className="text-danger">{errors.faculty}</div>}
            </div>
            <div className="mb-3">
                <label className="bold" htmlFor="abbreviation">
                Skrót:
                </label>
                <input
                type="text"
                id="abbreviation"
                name="abbreviation"
                value={formData.abbreviation}
                onChange={(e) => setFormData({ ...formData, abbreviation: e.target.value })}
                className="form-control"
                disabled={studyField ? true : false}
                />
                {errors.abbreviation && <div className="text-danger">{errors.abbreviation}</div>}
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

export default StudyFieldForm;
