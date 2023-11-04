import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import { Student } from '../../../models/Student';
import { Faculty } from '../../../models/Faculty';
import { StudentProgramCycle } from '../../../models/StudentProgramCycle';
import Cookies from "js-cookie";
import useAuth from "../../../auth/useAuth";
import handleSignOut from "../../../auth/Logout";
import {useTranslation} from "react-i18next";

const StudentDetails: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
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
      .catch((error) => {
          console.error(error);
          if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
          }
      });
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
          &larr; {t('general.management.goBack')}
        </button>
        <button type="button" className="custom-button" onClick={() => {
            // go to student edit
            }}>
            {t('general.management.edit')}
        </button>
      </div>
      <div>
        {student ? (
          <div>
            <p><span className="bold">{t('general.people.name')}:</span> <span>{student.name}</span></p>
            <p><span className="bold">{t('general.people.surname')}:</span> <span>{student.surname}</span></p>
            <p><span className="bold">{t('general.people.index')}:</span> <span>{student.index}</span></p>
            <p><span className="bold">{t('general.university.status')}:</span> <span>{student.status}</span></p>
            {student.studentProgramCycles.length > 0 && (
            <div>
                <p className="bold">{t('general.university.studyPrograms')}:</p>
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
                                <p><span className="bold">{t('general.university.studyCycle')} - </span>
                                    <span>{studentProgramCycle.cycle.name}</span></p>
                            </li>
                            <li>
                            <p><span className="bold">{t('general.university.faculty')} - </span>
                                <span>{findFacultyNameByProgram(studentProgramCycle.program.id)}</span></p>
                            </li>
                            <li>
                            <p><span className="bold">{t('general.university.field')} - </span>
                                <span>{studentProgramCycle.program.studyField.name}</span></p>
                            </li>
                            <li>
                            <p><span className="bold">{t('general.university.specialization')} - </span>
                                <span>{studentProgramCycle.program.specialization ?
                                    studentProgramCycle.program.specialization.name : t('general.management.lack')}
                                </span>
                            </p>
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
          <p>{t('general.management.errorOfLoading')}</p>
        )}
      </div>
    </div>
  );
};

export default StudentDetails;
