import React, { useEffect, useState } from 'react';
import Axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { Thesis } from '../../models/Models';
import Cookies from "js-cookie";


const ThesesTable: React.FC = () => {
  const navigate = useNavigate();
  const [theses, setTheses] = useState<Thesis[]>([]);
  
  const [currentPage, setCurrentPage] = useState(1);
  const recordsPerPage = 25;

  useEffect(() => {
    Axios.get('http://localhost:8080/thesis', {
        headers: {
            'Authorization': `Bearer ${Cookies.get('google_token')}`
        }
    })
      .then((response) => {
        setTheses(response.data);
      })
      .catch((error) => console.error(error));
  }, []);

  const indexOfLastRecord = currentPage * recordsPerPage;
  const indexOfFirstRecord = indexOfLastRecord - recordsPerPage;
  const currentTheses = theses.slice(indexOfFirstRecord, indexOfLastRecord);

  const totalPages = Math.ceil(theses.length / recordsPerPage);

  const handlePageChange = (newPage:number) => {
    setCurrentPage(newPage);
  };

  const renderPageNumbers = () => {
    const pageNumbers = [];
    for (let i = Math.max(1, currentPage - 2); i <= Math.min(currentPage + 2, totalPages); i++) {
      pageNumbers.push(i);
    }

    return pageNumbers.map((pageNumber) => (
      <button
        key={pageNumber}
        onClick={() => handlePageChange(pageNumber)}
        className={currentPage === pageNumber ? 'active' : ''}
      >
        {pageNumber}
      </button>
    ));
  };

  return (
    <div>
      <table className="theses-table">
        <thead className ='active'>
          <tr>
            <th style={{ width: '3%', textAlign: 'center'}}>#</th>
            <th style={{ width: '70%' }}>Temat</th>
            <th style={{ width: '17%' }}>Promotor</th>
            <th style={{ width: '10%', textAlign: 'center'}}>Zajęte miejsca</th>
          </tr>
        </thead>
        <tbody>
          {currentTheses.map((thesis, index) => (
            <tr key={thesis.id}>
              <td className="centered">{indexOfFirstRecord + index + 1}</td>
              <td><button onClick={() =>{navigate(`/theses/${thesis.id}`, {state: {thesis}})}} className="link-style btn">{thesis.namePL}</button></td>
              <td>{thesis.supervisor.title + " " + thesis.supervisor.name + " " + thesis.supervisor.surname}</td>
              <td className="centered">{thesis.occupied + "/" + thesis.num_people}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="pagination">
        <button
          onClick={() => handlePageChange(Math.max(1, currentPage - 1))}
          disabled={currentPage === 1}
        >
          &lt;
        </button>
        {renderPageNumbers()}
        <button
          onClick={() => handlePageChange(Math.min(currentPage + 1, totalPages))}
          disabled={currentPage === totalPages}
        >
          &gt;
        </button>
      </div>
    </div>
  );
}

export default ThesesTable;
