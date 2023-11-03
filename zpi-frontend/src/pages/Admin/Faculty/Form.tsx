import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Axios from 'axios';
import { Faculty } from '../../../models/Faculty';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";

const FacultyForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const faculty = location.state?.faculty as Faculty;
  const [oldAbbr, setOldAbbr] = useState<String>();
  const [formData, setFormData] = useState<Faculty>({
    abbreviation: '',
    name: '',
    studyFields: [],
    programs: [],
    departments: [],
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (faculty) {
      setFormData(faculty);
      setOldAbbr(faculty.abbreviation);
    }
  }, [faculty]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (faculty) {
        Axios.put(`http://localhost:8080/faculty/${oldAbbr}`, formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/faculties")
          toast.success("Wydział został zaktualizowany");
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
            }
          });
      } else {
        Axios.post('http://localhost:8080/faculty', formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/faculties")
          toast.success("Wydział został dodany");
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
                {faculty ? 'Zapisz' : 'Dodaj'}
                </button>
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
                disabled={faculty ? true : false}
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

export default FacultyForm;
