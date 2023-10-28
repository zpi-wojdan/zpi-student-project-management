import React, { useEffect, useState } from 'react';
import Axios from 'axios';
import { Student } from '../../../models/Student';

const StudentList: React.FC = () => {
    const [students, setStudents] = useState<Student[]>([]);

    useEffect(() => {
        Axios.get('http://localhost:8080/student')
        .then((response) => {
            response.data.sort((a: Student, b: Student) => parseInt(a.index, 10) - parseInt(b.index, 10));
            setStudents(response.data);
        })
        .catch((error) => console.error(error));
    }, []);
  
    const ITEMS_PER_PAGE = [10, 25, 50];
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(ITEMS_PER_PAGE[0]);

    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentStudents = students.slice(indexOfFirstItem, indexOfLastItem);

    const totalPages = Math.ceil(students.length / itemsPerPage);

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
        <div className='page-margin'>
            <div className='d-flex justify-content-end align-items-center mb-3'>
                <label style={{ marginRight: '10px' }}>Widok:</label>
                <select
                value={itemsPerPage}
                onChange={(e) => setItemsPerPage(Number(e.target.value))}
                >
                {ITEMS_PER_PAGE.map((value) => (
                    <option key={value} value={value}>
                    {value}
                    </option>
                ))}
                </select>
            </div>
            <table className="custom-table">
                <thead className ='active'>
                <tr>
                    <th style={{ width: '3%', textAlign: 'center'}}>#</th>
                    <th style={{ width: '17%'}}>Indeks</th>
                    <th style={{ width: '35%'}}>Imię</th>
                    <th style={{ width: '35%'}}>Nazwisko</th>
                    <th style={{ width: '10%', textAlign: 'center'}}>Szczegóły</th>
                </tr>
                </thead>
                <tbody>
                {currentStudents.map((student, index) => (
                    <tr key={student.mail}>
                    <td>{indexOfFirstItem + index + 1}</td>
                    <td>{student.index}</td>
                    <td>{student.name}</td>
                    <td>{student.surname}</td>
                    <td>
                        <button
                        className="active"
                        onClick={() => {
                            //Go to student details
                        }}
                        >
                        <i className="bi bi-arrow-right"></i>
                        </button>
                    </td>
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
};

export default StudentList;
