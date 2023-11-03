import React, { useEffect, useState } from 'react';
import Axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Faculty } from '../../../models/Faculty';
import Cookies from "js-cookie";
import { toast } from 'react-toastify';
import DeleteConfirmation from '../../../components/DeleteConfirmation';

const FacultyList: React.FC = () => {
  const navigate = useNavigate();
  const [ITEMS_PER_PAGE, setITEMS_PER_PAGE] = useState(['10', '25', '50', 'All']);
  const [faculties, setFaculties] = useState<Faculty[]>([]);
  const [refreshList, setRefreshList] = useState(false);
  useEffect(() => {
    Axios.get('http://localhost:8080/faculty', {
      headers: {
          'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
  })
      .then((response) => {
        const sortedFaculties = response.data.sort((a: Faculty, b: Faculty) => {
          return a.abbreviation.localeCompare(b.abbreviation);
        });
        setFaculties(sortedFaculties);
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
  }, [refreshList]);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState((ITEMS_PER_PAGE.length>1) ? ITEMS_PER_PAGE[1] : ITEMS_PER_PAGE[0]);
  const indexOfLastItem = itemsPerPage === 'All' ? faculties.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentFaculties = faculties.slice(indexOfFirstItem, indexOfLastItem);

  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(faculties.length / parseInt(itemsPerPage, 10));

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

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
  const [facultyToDelete, setFacultyToDelete] = useState<string | null>(null);

  const handleDeleteClick = (facultyAbbreviation: string) => {
    setShowDeleteConfirmation(true);
    setFacultyToDelete(facultyAbbreviation);
  };

  const handleConfirmDelete = () => {
    Axios.delete(`http://localhost:8080/faculty/${facultyToDelete}`, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
        .then(() => {
          toast.success("Wydział został usunięty");
          setRefreshList(!refreshList);
        })
        .catch((error) => {
            console.error(error);
            toast.error("Wydział nie może zostać usunięty!");
          });
    setShowDeleteConfirmation(false);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-between  align-items-center mb-3'>
        <div >
          <button className="custom-button" onClick={() => {navigate('/faculties/add')}}>
            Dodaj wydział
          </button>
        </div>
        <div >
          {ITEMS_PER_PAGE.length > 10 && (
            <div>
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
          )}
        </div>
      </div>
      <table className="custom-table">
        <thead>
          <tr>
            <th style={{ width: '3%', textAlign: 'center' }}>#</th>
            <th style={{ width: '15%', textAlign: 'center'   }}>Skrót</th>
            <th style={{ width: '62%' }}>Nazwa</th>
            <th style={{ width: '10%', textAlign: 'center'  }}>Edytuj</th>
            <th style={{ width: '10%', textAlign: 'center' }}>Usuń</th>
          </tr>
        </thead>
        <tbody>
  {currentFaculties.map((faculty, index) => (
    <React.Fragment key={faculty.abbreviation}>
      <tr>
        <td className="centered">{indexOfFirstItem + index + 1}</td>
        <td className="centered">{faculty.abbreviation}</td>
        <td>{faculty.name}</td>
        <td>
          <button
            className="custom-button coverall"
            onClick={() => {
              navigate(`/faculties/edit/${faculty.abbreviation}`, { state: { faculty } });
            }}
          >
            <i className="bi bi-arrow-right"></i>
          </button>
        </td>
        <td>
          <button
            className="custom-button coverall"
            onClick={() => handleDeleteClick(faculty.abbreviation)}
          >
            <i className="bi bi-trash"></i>
          </button>
        </td>
      </tr>
      {facultyToDelete === faculty.abbreviation && showDeleteConfirmation && (
        <tr>
          <td colSpan={5}>
          <DeleteConfirmation
            isOpen={showDeleteConfirmation}
            onClose={handleCancelDelete}
            onConfirm={handleConfirmDelete}
            onCancel={handleCancelDelete}
          />
          </td>
        </tr>
      )}
    </React.Fragment>
  ))}
</tbody>

      </table>
      {ITEMS_PER_PAGE.length > 10 && itemsPerPage !== 'All' && (
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

export default FacultyList;