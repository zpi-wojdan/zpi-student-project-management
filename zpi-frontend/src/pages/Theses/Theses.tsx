import React, { useEffect, useState } from 'react';
import Axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { ThesisFront, Thesis } from '../../models/Thesis';
import Cookies from "js-cookie";
import api from '../../utils/api';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";


const ThesesTable: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const [theses, setTheses] = useState<ThesisFront[]>([]);
  
  const [ITEMS_PER_PAGE, setITEMS_PER_PAGE] = useState(['10', '25', '50', 'All']);

  useEffect(() => {
    api.get('http://localhost:8080/thesis')
      .then((response) => {
        console.log(response);
        const thesis_response = response.data.map((thesisDb: Thesis) => {
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
          return thesis;
        });
        thesis_response.sort((a: ThesisFront, b: ThesisFront) => a.id - b.id);
        setTheses(thesis_response);
        const filteredItemsPerPage = ITEMS_PER_PAGE.filter(itemPerPage => {
          if (itemPerPage === 'All') {
            return true;
          } else {
            const perPageValue = parseInt(itemPerPage, 10);
            return perPageValue <= response.data.length;
          }
        });
        setITEMS_PER_PAGE(filteredItemsPerPage);
      })
      .catch((error) => {
          console.error(error);
          if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
          }
      });
  }, []);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState((ITEMS_PER_PAGE.length>1) ? ITEMS_PER_PAGE[1] : ITEMS_PER_PAGE[0]);
  const indexOfLastItem = itemsPerPage === 'All' ? theses.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentTheses = theses.slice(indexOfFirstItem, indexOfLastItem);

  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(theses.length / parseInt(itemsPerPage, 10));

  const handlePageChange = (newPage: number) => {
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
    <div className='page-margin'>
      <div className='d-flex justify-content-end  align-items-center mb-3'>
        <div >
            <label style={{ marginRight: '10px' }}>Widok:</label>
            <select
            value={itemsPerPage}
            onChange={(e) => setItemsPerPage(e.target.value)}
            >
            {ITEMS_PER_PAGE.map((value) => (
                <option key={value} value={value}>
                {value}
                </option>
            ))}
            </select>
        </div>
      </div>
      <table className="custom-table">
        <thead>
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
              <td className="centered">{indexOfFirstItem + index + 1}</td>
              <td><button onClick={() =>{navigate(`/theses/${thesis.id}`, {state: {thesis}})}} className="link-style btn">{thesis.namePL}</button></td>
              <td>{thesis.supervisor.title + " " + thesis.supervisor.name + " " + thesis.supervisor.surname}</td>
              <td className="centered">{thesis.occupied + "/" + thesis.num_people}</td>
            </tr>
          ))}
        </tbody>
      </table>
      {itemsPerPage !== 'All' && (
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
      )}
    </div>
  );
}

export default ThesesTable;
