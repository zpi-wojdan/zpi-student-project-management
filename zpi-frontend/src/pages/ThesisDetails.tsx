import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import StudentTable from '../components/StudentsTable';
import { ThesisFront, Thesis } from '../models/Thesis';

const ThesisDetails: React.FC = () => {
  const navigate = useNavigate();

  const { id } = useParams<{ id: string }>();
  const [thesis, setThesis] = useState<ThesisFront>();

  useEffect(() => {
    const response = Axios.get(`http://localhost:8080/thesis/${id}`)
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
      .catch((error) => console.error(error));

  }, [id]);

  return (
    <>
      <div className='row d-flex justify-content-between'>
        <button type="button" className="col-sm-2 btn btn-secondary m-3" onClick={() => navigate(-1)}>
          &larr; Powrót
        </button>
        <button type="button" className="col-sm-2 btn btn-primary m-3" onClick={() => {
          if (thesis?.reservations.length === 0) {
            navigate('/reservation', { state: { thesis: thesis } })
          } else {
            navigate('/single-reservation', { state: { thesis: thesis } })
          }
        }
        }>
          Zarezerwuj
        </button>
      </div>
      <div className='thesis-details'>
        {thesis ? (
          <div>
            <p className="bold">Temat po polsku:</p>
            <p>{thesis.namePL}</p>
            <p className="bold">Temat po angielsku:</p>
            <p>{thesis.nameEN}</p>
            <p className="bold">Opis:</p>
            <p>{thesis.description}</p>
            <p><span className="bold">Promotor:</span> <span>{thesis.supervisor.title + " " + thesis.supervisor.name + " " + thesis.supervisor.surname}</span></p>
            <p className="bold">Programy:</p>
            <ul>
              {thesis.programs.map((program) => (
                <li key={program.id}>{program.name}</li>
              ))}
            </ul>
            <p><span className="bold">Cykl:</span> <span>{thesis.studyCycle ? thesis.studyCycle.name : 'N/A'}</span></p>
            {/* <p><span className="bold">Wydział:</span> <span>{thesis.faculty}</span></p>
            <p><span className="bold">Kierunek:</span> <span>{thesis.field}</span></p>
            <p><span className="bold">Cykl kształcenia:</span> <span>{thesis.eduCycle}</span></p> */}
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
          <p>Loading... {id}</p>
        )}
      </div>
    </>
  );
};

export default ThesisDetails;
