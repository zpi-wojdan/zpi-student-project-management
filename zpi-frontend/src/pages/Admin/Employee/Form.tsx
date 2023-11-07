import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import { EmployeeDTO } from '../../../models/Employee';
import { toast } from 'react-toastify';
import Cookies from 'js-cookie';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { Role, RoleDTO } from '../../../models/Role';
import { Department } from '../../../models/Department';

export type  Title = {
    name: string;
}

const EmployeeForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const [formData, setFormData] = useState<EmployeeDTO>({
    mail: '',
    name: '',
    surname: '',
    title: { name: '' },
    roles: [],
    departmentCode: '',
  });

  const navigate = useNavigate();
  const location = useLocation();
  const employee = location.state?.employee;
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (employee) {
      setFormData({
        mail: employee.mail,
        name: employee.name,
        surname: employee.surname,
        title: employee.title,
        roles: employee.roles.map((role:Role) => ({ name: role.name })),
        departmentCode: employee.department.code,
      });
    }
  }, [employee]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (employee) {
        Axios.put(`http://localhost:8080/employee/${formData.mail}`, formData, {
          headers: {
            'Authorization': `Bearer ${Cookies.get('google_token')}`
          }
        })
          .then(() => {
            navigate("/employees");
            toast.success("Pracownik został zaktualizowany");
          })
          .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
            }
            navigate("/employees");
            toast.error("Pracownik nie został zaktualizowany");
          });
      } else {
        Axios.post('http://localhost:8080/employee', formData, {
          headers: {
            'Authorization': `Bearer ${Cookies.get('google_token')}`
          }
        })
          .then(() => {
            navigate("/employees");
            toast.success("Pracownik został dodany");
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              const newErrors: Record<string, string> = {};
              newErrors.mail = 'Podany mail już istnieje!';
              setErrors(newErrors);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              navigate("/employees");
              toast.error("Pracownik nie został dodany");
            }
          })
      }
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    let isValid = true;
    const errorRequireText = 'Pole jest wymagane.';

    if (!formData.mail) {
        newErrors.mail = errorRequireText;
        isValid = false;
      } else if (!/.+@pwr\.edu\.pl$/.test(formData.mail)) {
        newErrors.mail = 'Mail musi być z domeny pwr.edu.pl';
        isValid = false;
      }

    if (!formData.title) {
        newErrors.title = errorRequireText;
        isValid = false;
    }

    if (!formData.name) {
      newErrors.name = errorRequireText;
      isValid = false;
    }

    if (!formData.surname) {
      newErrors.surname = errorRequireText;
      isValid = false;
    }

    if (!formData.departmentCode) {
        newErrors.department = errorRequireText;
        isValid = false;
    }

    if (formData.roles.length === 0) {
        newErrors.roles = 'Pracownik musi mieć przypisaną conajmniej jedną rolę.';
        isValid = false;
      }

    setErrors(newErrors);
    return isValid;
  };

  const [availableDepartments, setAvailableDepartments] = useState<Department[]>([]);
  const [availableRoles, setAvailableRoles] = useState<Role[]>([]);
  const roleLabels: { [key: string]: string } = {
    supervisor: 'prowadzący',
    approver: 'zatwierdzający',
    admin: 'administrator',
  };
  const [availableTitles, setAvailableTitles] = useState<Title[]>([
    { name: 'mgr' },
    { name: 'dr' },
    { name: 'dr hab.' },
    { name: 'prof' },
  ]);

  useEffect(() => {
    Axios.get('http://localhost:8080/departments', {
      headers: {
        'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
    })
      .then((response) => {
        setAvailableDepartments(response.data);
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
    Axios.get('http://localhost:8080/role', {
      headers: {
        'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
    })
      .then((response) => {
        const filteredRoles = response.data.filter((role:Role) => role.name !== "student");
      setAvailableRoles(filteredRoles);
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

//   useEffect(() => {
//     Axios.get('http://localhost:8080/title', {
//       headers: {
//         'Authorization': `Bearer ${Cookies.get('google_token')}`
//       }
//     })
//       .then((response) => {
//         setAvailableTitles(response.data);
//       })
//       .catch((error) => 
//       {
//         console.error(error);
//         if (error.response.status === 401 || error.response.status === 403) {
//           setAuth({ ...auth, reasonOfLogout: 'token_expired' });
//           handleSignOut(navigate);
//         }
//       });
//   }, []);


const handleRolesSelection = (role: Role) => {
    const updatedRoles = formData.roles.slice();
    const roleDTO: RoleDTO = { name: role.name };

    const roleIndex = updatedRoles.findIndex((r) => r.name === roleDTO.name);
  
    if (roleIndex !== -1) {
      updatedRoles.splice(roleIndex, 1);
    } else {
      updatedRoles.push(roleDTO);
    }
  
    setFormData({
      ...formData,
      roles: updatedRoles,
    });
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="form">
        <div className='d-flex justify-content-begin align-items-center mb-3'>
          <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
            &larr; Powrót
          </button>
          <button type="submit" className="custom-button">
            {employee ? 'Zapisz' : 'Dodaj'}
          </button>
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="mail">
            Mail:
          </label>
          <input
            type="text"
            id="mail"
            name="mail"
            value={formData.mail}
            onChange={(e) => setFormData({ ...formData, mail: e.target.value })}
            className="form-control"
          />
          {errors.mail && <div className="text-danger">{errors.mail}</div>}
        </div>
        <div className="mb-3">
            <label className="bold" htmlFor="title">
                Tytuł:
            </label>
            <select
                id="title"
                name="title"
                value={formData.title.name}
                onChange={(e) => setFormData({ ...formData, title: {name: e.target.value} })}
                className="form-control"
            >
                <option value="">Wybierz</option>
                {availableTitles.map((title) => (
                <option key={title.name} value={title.name}>
                    {title.name}
                </option>
                ))}
            </select>
            {errors.title && <div className="text-danger">{errors.title}</div>}
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="name">
            Imię:
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
            Nazwisko:
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
            <label className="bold" htmlFor="department">
            Katedra:
            </label>
            <select
            id="department"
            name="department"
            value={formData.departmentCode}
            onChange={(e) => {setFormData({ ...formData, departmentCode: e.target.value });}}
            className="form-control"
            >
            <option value="">Wybierz</option>
            {availableDepartments.map((department) => (
                <option key={department.code} value={department.code}>
                {department.name}
                </option>
            ))}
            </select>
            {errors.department && <div className="text-danger">{errors.department}</div>}
        </div>
        <div className="mb-3">
        <label className="bold">Role:</label>
            {availableRoles.map((role) => (
                <div key={role.id} className="mb-2">
                <input
                    type="checkbox"
                    id={`role-${role.id}`}
                    name={`role-${role.id}`}
                    checked={formData.roles.some((roleDTO) => roleDTO.name === role.name)}
                    onChange={() => handleRolesSelection(role)}
                    className="custom-checkbox"
                />
                <label style={{ marginLeft: '5px' }} htmlFor={`cycle-${role.id}`}>
                    {roleLabels[role.name] || role.name}
                </label>
                </div>
            ))}
            {errors.roles && <div className="text-danger">{errors.roles}</div>}
        </div>
      </form>
    </div>
  );
}

export default EmployeeForm;
