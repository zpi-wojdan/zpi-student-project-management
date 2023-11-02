import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import { Student } from '../../../models/Student';
import { Faculty } from '../../../models/Faculty';
import { StudentProgramCycle } from '../../../models/StudentProgramCycle';
import Cookies from "js-cookie";

const StudentDetails: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const student = location.state?.student as Student;
    
  const [faculties, setFaculties] = useState<Faculty[]>([]);
  useEffect(() => {
    Axios.get('http://localhost:8080/faculty', {
      headers: {
          'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
  })
      .then((response) => {
        setFaculties(response.data);
        console.log(faculties);
      })
      .catch((error) => console.error(error));
  }, []);

  function findFacultyNameByProgram(programId: number): string | null {
    for (const faculty of faculties) {
        for (const program of faculty.programs) {
            if (program.id === programId) {
                return faculty.name;
            }
        }
    }
    return null;
}

  const [expandedPrograms, setExpandedPrograms] = useState<number[]>([]);

  const toggleProgramExpansion = (programId: number) => {
    if (expandedPrograms.includes(programId)) {
      setExpandedPrograms(expandedPrograms.filter(id => id !== programId));
    } else {
      setExpandedPrograms([...expandedPrograms, programId]);
    }
  };

  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-begin  align-items-center mb-3'>
        <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; Powrót
        </button>
        <button type="button" className="custom-button" onClick={() => {
            // go to student edit
            }}>
          Edytuj
        </button>
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
                            <p><span className="bold">Wydział - </span> <span>{findFacultyNameByProgram(studentProgramCycle.program.id)}</span></p>
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
