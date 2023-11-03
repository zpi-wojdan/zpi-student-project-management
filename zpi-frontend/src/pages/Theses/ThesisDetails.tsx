import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import StudentTable from '../../components/StudentsTable';
import { ThesisFront, Thesis } from '../../models/Thesis';
import { Program } from '../../models/Program';
import { Faculty } from '../../models/Faculty';

import Cookies from 'js-cookie';
import { spawn } from 'child_process';
import { Employee } from '../../models/Employee';
import { Student } from '../../models/Student';
import useAuth from "../../auth/useAuth";
import handleSignOut from "../../auth/Logout";

const ThesisDetails: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();

  const { id } = useParams<{ id: string }>();
  const [thesis, setThesis] = useState<ThesisFront>();

  useEffect(() => {
    const response = Axios.get(`http://localhost:8080/thesis/${id}`, {
      headers: {
          'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
  })
      .then((response) => {
        const thesisDb = response.data as Thesis;
        const thesis: ThesisFront = {
          id: thesisDb.id,
          namePL: thesisDb.namePL,
          nameEN: thesisDb.nameEN,
          description: thesisDb.description,
          programs: thesisDb.programs,
          studyCycle: thesisDb.studyCycle,
          num_people: thesisDb.num_people,
          occupied: thesisDb.occupied,
          supervisor: thesisDb.supervisor,
          status: thesisDb.status,
          leader: thesisDb.leader,
          students: thesisDb.reservations.map((reservation) => reservation.student),
          reservations: thesisDb.reservations,
        };
        setThesis(thesis);
      })
      .catch((error) => {
          console.error(error);
          if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
          }
      });

  }, [id]);

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
  };  const [user, setUser] = useState<Student & Employee>();

  useEffect(() => {
    setUser(JSON.parse(Cookies.get("user") || "{}"));
  }, []);

  return (
    <>
      <div className='row d-flex justify-content-between'>
        <button type="button" className="col-sm-2 btn btn-secondary m-3" onClick={() => navigate(-1)}>
          &larr; Powrót
        </button>
        {(user?.role?.name === 'student' || user?.roles?.some(role => role.name === 'supervisor') &&
            user?.mail === thesis?.supervisor.mail) ?
          (
          <button type="button" className="col-sm-2 btn btn-primary m-3" onClick={() => {
            if (user?.role?.name === 'student') {
              if (thesis?.reservations.length === 0) {
                navigate('/reservation', { state: { thesis: thesis } })
              } else {
                navigate('/single-reservation', { state: { thesis: thesis } })
              }
            } else {
              navigate('/supervisor-reservation', { state: { thesis: thesis } })
            }
          }
          }>
            {user?.role?.name === 'student' ? (
              <span>Zarezerwuj</span>
            ) : (
              user?.mail === thesis?.supervisor.mail ?
                (
                  <span>Zapisz studentów</span>
                ) : (
                  <></>
                )
            )}
          </button>
          ) : (
            <span></span>
          )
        }
      </div>
      <div>
        {thesis ? (
          <div>
            <p className="bold">Nazwa tematu:</p>
            <p>{thesis.namePL}</p>
            <p className="bold">Opis:</p>
            <p>{thesis.description}</p>
            <p><span className="bold">Promotor:</span> <span>{thesis.supervisor.title + " " + thesis.supervisor.name + " " + thesis.supervisor.surname}</span></p>
            <p><span className="bold">Cykl:</span> <span>{thesis.studyCycle ? thesis.studyCycle.name : 'N/A'}</span></p>
            <p className="bold">Programy:</p>
            <ul>
            {thesis.programs.map((program: Program) => (
              <li key={program.id}>
                {program.name}
                <button className='custom-toggle-button' onClick={() => toggleProgramExpansion(program.id)}>
                  {expandedPrograms.includes(program.id) ? '▼' : '▶'} 
                </button>
                {expandedPrograms.includes(program.id) && (
                  <ul>
                    <li>
                      <p><span className="bold">Wydział - </span> <span>{findFacultyNameByProgram(program.id)}</span></p>
                    </li>
                    <li>
                      <p><span className="bold">Kierunek - </span> <span>{program.studyField.name}</span></p>
                    </li>
                    <li>
                      <p><span className="bold">Specjalność - </span> <span>{program.specialization ? program.specialization.name : "brak"}</span></p>
                    </li>
                </ul>
                )}
              </li>
            ))}
          </ul>
            <div>
              <p><span className="bold">Zapisani:</span> <span>{thesis.occupied + "/" + thesis.num_people}</span></p>
              {thesis.students.length > 0 ? (
                <StudentTable students={thesis.students} thesis={thesis} role={"student"} />
              ) : (
                <></>
              )}
            </div>
          </div>
        ) : (
          <p>Błąd wczytywania danych {id}</p>
        )}
      </div>
    </>
  );
};

export default ThesisDetails;
