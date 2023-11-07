import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import { Student } from '../../../models/Student';
import { Faculty } from '../../../models/Faculty';
import { StudentProgramCycle } from '../../../models/StudentProgramCycle';
import Cookies from "js-cookie";
import useAuth from "../../../auth/useAuth";
import handleSignOut from "../../../auth/Logout";
import { toast } from 'react-toastify';
import DeleteConfirmation from '../../../components/DeleteConfirmation';
import api from '../../../utils/api';

const StudentDetails: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const student = location.state?.student as Student;
    
  const [faculties, setFaculties] = useState<Faculty[]>([]);
  useEffect(() => {
    api.get('http://localhost:8080/faculty')
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

  const [expandedPrograms, setExpandedPrograms] = useState<number[]>([]);

  const toggleProgramExpansion = (programId: number) => {
    if (expandedPrograms.includes(programId)) {
      setExpandedPrograms(expandedPrograms.filter(id => id !== programId));
    } else {
      setExpandedPrograms([...expandedPrograms, programId]);
    }
  };

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

  const handleDeleteClick = (studentMail: string) => {
    setShowDeleteConfirmation(true);
  };

  const handleConfirmDelete = () => {
    api.delete(`http://localhost:8080/student/${student.mail}`)
        .then(() => {
          toast.success("Student został usunięty");
          navigate("/students");
        })
        .catch((error) => {
            console.error(error);
            toast.error("Student nie może zostać usunięty!");
            navigate("/students");
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
        <button type="button" className="custom-button" onClick={() => {navigate(`/students/edit/${student.mail}`, {state: {student}})}}>
          Edytuj
        </button>
        <button type="button" className="custom-button" onClick={() => handleDeleteClick(student.mail)}>
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
            questionText='Czy na pewno chcesz usunąć tego studenta?'
          />
          </td>
        </tr>
      )}
      </div>
      <div>
        {student ? (
          <div>
            <p><span className="bold">Imię:</span> <span>{student.name}</span></p>
            <p><span className="bold">Nazwisko:</span> <span>{student.surname}</span></p>
            <p><span className="bold">Indeks:</span> <span>{student.index}</span></p>
            <p><span className="bold">Status:</span> <span>{student.status}</span></p>
            {student.studentProgramCycles.length > 0 && (
            <div>
                <p className="bold">Programy:</p>
                <ul>
                    {student.studentProgramCycles.map((studentProgramCycle: StudentProgramCycle) => (
                    <li key={studentProgramCycle.program.id}>
                        {studentProgramCycle.program.name}
                        <button className='custom-toggle-button' onClick={() => toggleProgramExpansion(studentProgramCycle.program.id)}>
                        {expandedPrograms.includes(studentProgramCycle.program.id) ? '▼' : '▶'} 
                        </button>
                        {expandedPrograms.includes(studentProgramCycle.program.id) && (
                        <ul>
                            <li>
                                <p><span className="bold">Cykl - </span> <span>{studentProgramCycle.cycle.name}</span></p>
                            </li>
                            <li>
                            <p><span className="bold">Wydział - </span> <span>{studentProgramCycle.program.id}</span></p>
                            </li>
                            <li>
                            <p><span className="bold">Kierunek - </span> <span>{studentProgramCycle.program.studyField.name}</span></p>
                            </li>
                            <li>
                            <p><span className="bold">Specjalność - </span> <span>{studentProgramCycle.program.specialization ? studentProgramCycle.program.specialization.name : "brak"}</span></p>
                            </li>
                        </ul>
                        )}
                    </li>
                    ))}
                </ul>
            </div>
            )} 
          </div>
        ) : (
          <p>Błąd wczytywania danych</p>
        )}
      </div>
    </div>
  );
};

export default StudentDetails;
