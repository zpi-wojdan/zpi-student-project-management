import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Axios from 'axios';

interface Thesis {
  Id: number;
  namePL: string;
  nameEN: string;
  description: string;
  supervisor: {
    title: string;
    name: string;
    surname: string;
  };
  faculty: string;
  field: string;
  edu_cycle: string;
  num_people: number;
  occupied: number;
}

const ThesisDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [thesis, setThesis] = useState<Thesis | null>(null);

  useEffect(() => {
    Axios.get(`http://localhost:8080/thesis/${id}`)
      .then((response) => setThesis(response.data))
      .catch((error) => console.error(error));
  }, [id]);

  return (
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
            <p><span className="bold">Wydział:</span> <span>{thesis.faculty}</span></p>
            <p><span className="bold">Kierunek:</span> <span>{thesis.field}</span></p>
            <p><span className="bold">Cykl kształcenia:</span> <span>{thesis.edu_cycle}</span></p>
            <div>
                <p><span className="bold">Zapisani:</span> <span>{thesis.occupied + "/" + thesis.num_people}</span></p>
                {/* Tabela zapisanych */}
            </div>
        </div>
      ) : (
        <p>Loading... {id}</p>
      )}
    </div>
  );
};

export default ThesisDetails;
