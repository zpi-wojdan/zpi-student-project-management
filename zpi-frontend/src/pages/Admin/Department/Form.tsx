import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Axios from 'axios';
import { Department, DepartmentDTO } from '../../../models/Department';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { Faculty } from '../../../models/Faculty';

const DepartmentForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const department = location.state?.department as Department;
  const [formData, setFormData] = useState<DepartmentDTO>({
    code: '',
    name: '',
    facultyAbbreviation: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (department) {
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          code: department.code,
          name: department.name,
          facultyAbbr: department.faculty?.abbreviation,
        };
      });
    }
  }, [department]);

  const [faculties, setFaculties] = useState<Faculty[]>([]);

  useEffect(() => {
    Axios.get('http://localhost:8080/faculty', {
      headers: {
        'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
    })
      .then((response) => {
        setFaculties(response.data);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (department) {
        Axios.put(`http://localhost:8080/departments/${formData.code}`, formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/departments")
          toast.success("Katedra została zaktualizowana");
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                const newErrors: Record<string, string> = {};
                newErrors.code = 'Podany skrót już istnieje!';
                setErrors(newErrors);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error("Katedra nie została zaktualizowana");
            }
          });
      } else {
        Axios.post('http://localhost:8080/departments', formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/departments")
          toast.success("Katedra została dodana");
        })
        .catch((error) => {
            if (error.response && error.response.status === 409) {
                const newErrors: Record<string, string> = {};
                newErrors.code = 'Podany skrót już istnieje!';
                setErrors(newErrors);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error("Katedra nie została dodana");
            }
          });
      }
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    let isValid = true;

    const errorRequireText = 'Pole jest wymagane.';

    if (!formData.facultyAbbreviation) {
      newErrors.faculty = errorRequireText;
      isValid = false;
    }
    
    if (!formData.code) {
      newErrors.code = errorRequireText;
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
                {department ? 'Zapisz' : 'Dodaj'}
                </button>
            </div>
            <div className="mb-3">
              <label className="bold" htmlFor="faculty">
                Wydział:
              </label>
              <select
                id="faculty"
                name="faculty"
                value={formData.facultyAbbreviation}
                onChange={(e) => setFormData({ ...formData, facultyAbbreviation: e.target.value })}
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
                <label className="bold" htmlFor="code">
                Kod:
                </label>
                <input
                type="text"
                id="code"
                name="code"
                value={formData.code}
                onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                className="form-control"
                disabled={department ? true : false}
                />
                {errors.code && <div className="text-danger">{errors.code}</div>}
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

export default DepartmentForm;
