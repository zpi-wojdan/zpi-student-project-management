import React, { useEffect, useState } from 'react';
import Axios from 'axios';

interface Thesis {
  Id: number;
  namePL: string;
  supervisor: {
    title: string;
    name: string;
    surname: string;
  };
  num_people: number;
}

const ThesisTable: React.FC = () => {
  const [theses, setTheses] = useState<Thesis[]>([]);

  useEffect(() => {
    Axios.get('http://localhost:8080/thesis')
    .then((response) => {
      console.log(response.data);
      setTheses(response.data);
    })
      .catch((error) => console.error(error));
  }, []);

  return (
    <div>
      <table>
        <thead className ='active'>
          <tr>
            <th>#</th>
            <th>Topic Name (PL)</th>
            <th>Supervisor</th>
            <th>Number of People</th>
          </tr>
        </thead>
        <tbody>
          {theses.map((thesis, index) => (
            <tr key={thesis.Id}>
              <td>{index + 1}</td>
              <td>{thesis.namePL}</td>
              <td>{thesis.supervisor.title + thesis.supervisor.name + thesis.supervisor.surname}</td>
              <td>{thesis.num_people}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ThesisTable;
