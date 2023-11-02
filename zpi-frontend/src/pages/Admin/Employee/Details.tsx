import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import { Employee } from '../../../models/Employee';
import { Department } from '../../../models/Department';
import Cookies from "js-cookie";

const EmployeeDetails: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const employee = location.state?.employee as Employee;

  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-begin  align-items-center mb-3'>
        <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; Powrót
        </button>
        <button type="button" className="custom-button" onClick={() => {
            // go to employee edit
            }}>
          Edytuj
        </button>
      </div>
      <div>
        {employee ? (
        <div>
            <p><span className="bold">Tytuł:</span> <span>{employee.title}</span></p>
            <p><span className="bold">Imię:</span> <span>{employee.name}</span></p>
            <p><span className="bold">Nazwisko:</span> <span>{employee.surname}</span></p>
            <p><span className="bold">Mail:</span> <span>{employee.mail}</span></p>
            <p><span className="bold">Katedra:</span> <span>{employee.department.name}</span></p>
            <p className="bold">Role:</p>
                <ul>
                    {employee.roles.map((role) => (
                        <li key={role.id}>
                          {role.name === 'approver' ? 'zatwierdzający' : 
                            role.name === 'supervisor' ? 'prowadzący' :
                            role.name === 'admin' ? 'administrator' : role.name}
                        </li>
                    ))}
                </ul> 
        </div>
        ) : (
        <p>Błąd wczytywania danych</p>
        )}
      </div>
    </div>
  );
};

export default EmployeeDetails;
