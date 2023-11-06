import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import { Employee } from '../../../models/Employee';
import { Department } from '../../../models/Department';
import Cookies from "js-cookie";
import { toast } from 'react-toastify';
import DeleteConfirmation from '../../../components/DeleteConfirmation';

const EmployeeDetails: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const employee = location.state?.employee as Employee;
  const roleLabels: { [key: string]: string } = {
    supervisor: 'prowadzący',
    approver: 'zatwierdzający',
    admin: 'administrator',
  };

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

  const handleDeleteClick = (studentMail: string) => {
    setShowDeleteConfirmation(true);
  };

  const handleConfirmDelete = () => {
    Axios.delete(`http://localhost:8080/employee/${employee.mail}`, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          toast.success("Pracownik został usunięty");
          navigate("/employees");
        })
        .catch((error) => {
            console.error(error);
            toast.error("Pracownik nie może zostać usunięty!");
            navigate("/employees");
          });
    setShowDeleteConfirmation(false);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-begin  align-items-center mb-3'>
        <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; Powrót
        </button>
        <button type="button" className="custom-button" onClick={() => {navigate(`/employees/edit/${employee.mail}`, {state: {employee}})}}>
          Edytuj
        </button>
        <button type="button" className="custom-button" onClick={() => handleDeleteClick(employee.mail)}>
          <i className="bi bi-trash"></i>
        </button>
        { showDeleteConfirmation && (
        <tr>
          <td colSpan={5}>
          <DeleteConfirmation
            isOpen={showDeleteConfirmation}
            onClose={handleCancelDelete}
            onConfirm={handleConfirmDelete}
            onCancel={handleCancelDelete}
            questionText='Czy na pewno chcesz usunąć tego pracownika?'
          />
          </td>
        </tr>
      )}
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
                          {roleLabels[role.name] || role.name}
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
