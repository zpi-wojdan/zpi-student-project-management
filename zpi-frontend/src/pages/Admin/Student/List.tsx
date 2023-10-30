import React, { useEffect, useState } from 'react';
import Axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Student } from '../../../models/Student';

const StudentList: React.FC = () => {
  const navigate = useNavigate();
  const [students, setStudents] = useState<Student[]>([]);
  const [ITEMS_PER_PAGE, setITEMS_PER_PAGE] = useState(['10', '25', '50', 'All']);
  useEffect(() => {
    Axios.get('http://localhost:8080/student')
      .then((response) => {
        response.data.sort((a: Student, b: Student) => parseInt(a.index, 10) - parseInt(b.index, 10));
        setStudents(response.data);
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
      .catch((error) => console.error(error));
  }, []);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState((ITEMS_PER_PAGE.length>1) ? ITEMS_PER_PAGE[1] : ITEMS_PER_PAGE[0]);
  const indexOfLastItem = itemsPerPage === 'All' ? students.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentStudents = students.slice(indexOfFirstItem, indexOfLastItem);

  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(students.length / parseInt(itemsPerPage, 10));

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
      <div className='d-flex justify-content-between  align-items-center mb-3'>
        <div >
          <button className="custom-button" onClick={() => {
            // Obsługa dodawania nowego studenta
          }}>
            Dodaj studenta
          </button>
        </div>
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
            <th style={{ width: '3%', textAlign: 'center' }}>#</th>
            <th style={{ width: '17%' }}>Indeks</th>
            <th style={{ width: '35%' }}>Imię</th>
            <th style={{ width: '35%' }}>Nazwisko</th>
            <th style={{ width: '10%', textAlign: 'center' }}>Szczegóły</th>
          </tr>
        </thead>
        <tbody>
          {currentStudents.map((student, index) => (
            <tr key={student.mail}>
              <td className="centered">{indexOfFirstItem + index + 1}</td>
              <td>{student.index}</td>
              <td>{student.name}</td>
              <td>{student.surname}</td>
              <td style={{display: "flex"}}>
                <button
                  className="custom-button coverall"
                  onClick={() => {
                    navigate(`/students/${student.mail}`, {state: {student}})
                  }}
                >
                  <i className="bi bi-arrow-right"></i>
                </button>
              </td>
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
};

export default StudentList;