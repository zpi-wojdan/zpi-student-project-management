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
import {useTranslation} from "react-i18next";

export type  Title = {
    name: string;
}

const EmployeeForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const [formData, setFormData] = useState<EmployeeDTO>({
    mail: '',
    name: '',
    surname: '',
    title: '',
    roles: [],
    departmentCode: '',
  });

  const navigate = useNavigate();
  const location = useLocation();
  const employee = location.state?.employee;
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [errorsKeys, setErrorsKeys] = useState<Record<string, string>>({});

  useEffect(() => {
    const newErrors: Record<string, string> = {};
    Object.keys(errorsKeys).forEach((key) => {
      newErrors[key] = t(errorsKeys[key]);
    });
    setErrors(newErrors);
  }, [i18n.language]);

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
            toast.success(t("employee.updateSuccessful"));
          })
          .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
            }
            navigate("/employees");
            toast.error(t("employee.updateError"));
          });
      } else {
        Axios.post('http://localhost:8080/employee', formData, {
          headers: {
            'Authorization': `Bearer ${Cookies.get('google_token')}`
          }
        })
          .then(() => {
            navigate("/employees");
            toast.success(t("employee.addSuccessful"));
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              const newErrors: Record<string, string> = {};
              newErrors.mail = t("general.management.mailExists")
              setErrors(newErrors);
              const newErrorsKeys: Record<string, string> = {};
              newErrorsKeys.mail = "general.management.mailExists"
              setErrorsKeys(newErrorsKeys);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              navigate("/employees");
              toast.error(t("employee.addError"));
            }
          })
      }
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    const newErrorsKeys: Record<string, string> = {};
    let isValid = true;
    const errorRequireText = t('general.management.fieldIsRequired');

    if (!formData.mail) {
        newErrors.mail = errorRequireText;
        newErrorsKeys.mail = "general.management.fieldIsRequired"
        isValid = false;
      } else if (!/.+@pwr\.edu\.pl$/.test(formData.mail)) {
        newErrors.mail = t('general.management.mailMustBePwr');
        newErrorsKeys.mail = "general.management.mailMustBePwr"
        isValid = false;
      }

    if (!formData.title) {
        newErrors.title = errorRequireText;
        newErrorsKeys.title = "general.management.fieldIsRequired"
        isValid = false;
    }

    if (!formData.name) {
      newErrors.name = errorRequireText;
      newErrorsKeys.name = "general.management.fieldIsRequired"
      isValid = false;
    }

    if (!formData.surname) {
      newErrors.surname = errorRequireText;
      newErrorsKeys.surname = "general.management.fieldIsRequired"
      isValid = false;
    }

    if (!formData.departmentCode) {
        newErrors.department = errorRequireText;
        newErrorsKeys.department = "general.management.fieldIsRequired"
        isValid = false;
    }

    if (formData.roles.length === 0) {
        newErrors.roles = t('employee.rolesRequired');
        newErrorsKeys.roles = "employee.rolesRequired"
        isValid = false;
      }

    setErrors(newErrors);
    setErrorsKeys(newErrorsKeys);
    return isValid;
  };

  const [availableDepartments, setAvailableDepartments] = useState<Department[]>([]);
  const [availableRoles, setAvailableRoles] = useState<Role[]>([]);
  const roleLabels: { [key: string]: string } = {
    supervisor: t('general.people.supervisorLC'),
    approver: t('general.people.approverLC'),
    admin: t('general.people.adminLC'),
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
            &larr; {t('general.management.goBack')}
          </button>
          <button type="submit" className="custom-button">
            {employee ? t('general.management.save') : t('general.management.add')}
          </button>
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="mail">
            {t('general.people.mail')}:
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
              {t('general.title')}:
            </label>
            <select
                id="title"
                name="title"
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                className="form-control"
            >
                <option value="">{t('general.management.choose')}</option>
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
            {t('general.people.name')}:
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
            {t('general.people.surname')}:
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
              {t('general.university.department')}:
            </label>
            <select
            id="department"
            name="department"
            value={formData.departmentCode}
            onChange={(e) => {setFormData({ ...formData, departmentCode: e.target.value });}}
            className="form-control"
            >
            <option value="">{t('general.management.choose')}</option>
            {availableDepartments.map((department) => (
                <option key={department.code} value={department.code}>
                {department.name}
                </option>
            ))}
            </select>
            {errors.department && <div className="text-danger">{errors.department}</div>}
        </div>
        <div className="mb-3">
        <label className="bold">{t('general.people.roles')}:</label>
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
