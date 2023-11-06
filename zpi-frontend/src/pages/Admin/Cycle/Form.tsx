import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Axios from 'axios';
import { StudyCycle } from '../../../models/StudyCycle';
import Cookies from 'js-cookie';
import { toast } from 'react-toastify';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";

const StudyCycleForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const studyCycle = location.state?.studyCycle as StudyCycle;
  const [formData, setFormData] = useState<StudyCycle>({
    id: 4,
    name: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (studyCycle) {
      setFormData(studyCycle);
    }
  }, [studyCycle]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (studyCycle) {
        Axios.put(`http://localhost:8080/studycycle/${formData.id}`, formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/cycles")
          toast.success("Cykl został zaktualizowany");
        })
        .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
            }
            toast.error("Cykl nie został zaktualizowany");
          });
      } else {
        Axios.post('http://localhost:8080/studycycle', formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          navigate("/cycles")
          toast.success("Cykl został dodany");
        })
        .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
            }
            toast.error("Cykl nie został dodany");
          });
      }
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    let isValid = true;

    const errorRequireText = 'Pole jest wymagane.';
    const regexPattern = /^\d{4}\/\d{2}-[A-Z]{1,3}$/;

    if (!formData.name) {
      newErrors.name = errorRequireText;
      isValid = false;
    } else if (!regexPattern.test(formData.name)) {
      newErrors.name = 'Zły format';
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
                {studyCycle ? 'Zapisz' : 'Dodaj'}
                </button>
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
                <div className="text-info">
                  Poprawny format: "xxxx/xx-XYZ", gdzie "xxxx" to 4 cyfry, "xx" to 2 cyfry, a "XYZ" to od 1 do 3 dużych liter alfabetu.
                </div>
                {errors.name && <div className="text-danger">{errors.name}</div>}
            </div>
        </form>
    </div>
  );
};

export default StudyCycleForm;
