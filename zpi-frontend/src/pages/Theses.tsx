import React, { useEffect, useState } from 'react';
import Axios from 'axios';

interface Thesis {
  id: number;
  namePL: string;
  supervisor: {
    name: string;
  };
  num_people: number;
}

const ThesisTable: React.FC = () => {
  const [theses, setTheses] = useState<Thesis[]>([]);

  useEffect(() => {
    Axios.get('http://localhost:8080/thesis')
      .then((response) => setTheses(response.data))
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
            <tr key={thesis.id}>
              <td>{index + 1}</td>
              <td>{thesis.namePL}</td>
              <td>{thesis.supervisor.name}</td>
              <td>{thesis.num_people}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ThesisTable;
