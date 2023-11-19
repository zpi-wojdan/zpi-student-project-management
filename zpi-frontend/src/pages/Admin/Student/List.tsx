import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Student } from '../../../models/Student';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import SearchBar from '../../../components/SeatchBar';

const StudentList: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();
  const [students, setStudents] = useState<Student[]>([]);
  const ITEMS_PER_PAGE = ['10', '25', '50', 'All'];
  const [currentITEMS_PER_PAGE, setCurrentITEMS_PER_PAGE] = useState(ITEMS_PER_PAGE);

  useEffect(() => {
    api.get('http://localhost:8080/student')
      .then((response) => {
        response.data.sort((a: Student, b: Student) => parseInt(a.index, 10) - parseInt(b.index, 10));
        setStudents(response.data);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  const [searchTerm, setSearchTerm] = useState<string>('');
  const [filteredStudents, setFilteredStudents] = useState<Student[]>(students);

  useEffect(() => {
    const searchText = searchTerm.toLowerCase();
    const filteredList = students.filter((student) => {
      return (
        student.index.toLowerCase().includes(searchText) ||
        student.name.toLowerCase().includes(searchText) ||
        student.surname.toLowerCase().includes(searchText)
      );
    });

    // Update other logic as needed
    const filteredItemsPerPage = ITEMS_PER_PAGE.filter((itemPerPage) => {
      if (itemPerPage === 'All') {
        return true;
      } else {
        const perPageValue = parseInt(itemPerPage, 10);
        return perPageValue < filteredList.length;
      }
    });
    setCurrentITEMS_PER_PAGE(() => filteredItemsPerPage);
    setFilteredStudents(() => filteredList);


    // Przywróć stan początkowy paginacji
    handlePageChange(1);
    setItemsPerPage((filteredItemsPerPage.includes(chosenItemsPerPage)) ? chosenItemsPerPage : ((filteredItemsPerPage.length > 1) ? filteredItemsPerPage[1] : filteredItemsPerPage[0]));

    console.log("Items: ", currentITEMS_PER_PAGE);
    console.log("Item: ", itemsPerPage);
  }, [searchTerm, students]);

  const clearSearch = () => {
    setSearchTerm('');
    // Przywróć stan początkowy paginacji
    handlePageChange(1);
    setItemsPerPage((currentITEMS_PER_PAGE.includes(chosenItemsPerPage)) ? chosenItemsPerPage : ((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]));

  };

  const [currentPage, setCurrentPage] = useState(1);
  const [inputValue, setInputValue] = useState(currentPage);
  const [itemsPerPage, setItemsPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
  const [chosenItemsPerPage, setChosenItemsPerPage] = useState(itemsPerPage);
  const indexOfLastItem = itemsPerPage === 'All' ? filteredStudents.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentStudents = filteredStudents.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(filteredStudents.length / parseInt(itemsPerPage, 10));

  const handlePageChange = (newPage: number) => {
    if (!newPage || newPage < 1) {
      setCurrentPage(1);
      setInputValue(1);
    }
    else {
      if (newPage > totalPages) {
        setCurrentPage(totalPages);
        setInputValue(totalPages);
      }
      else {
        setCurrentPage(newPage);
        setInputValue(newPage);
      }
    }
  };

  return (
    <div className='page-margin'>
      <div >
        <button className="custom-button" onClick={() => { navigate('/students/add') }}>
          {t('student.add')}
        </button>
        <button className="custom-button" onClick={() => { navigate('/file/student') }}>
          {t('student.import')}
        </button>
      </div>
      <div className='d-flex justify-content-between  align-items-center'>
      <SearchBar
          searchTerm={searchTerm}
          setSearchTerm={setSearchTerm}
          clearSearch={clearSearch}
          placeholder={t('general.management.search')}
        />
        {currentITEMS_PER_PAGE.length > 1 && (
          <div className="d-flex justify-content-between">
            <div className="d-flex align-items-center">
              <label style={{ marginRight: '10px' }}>{t('general.management.view')}:</label>
              <select
                value={itemsPerPage}
                onChange={(e) => {
                  setItemsPerPage(e.target.value);
                  setChosenItemsPerPage(e.target.value);
                  handlePageChange(1);
                }}
              >
                {currentITEMS_PER_PAGE.map((value) => (
                  <option key={value} value={value}>
                    {value}
                  </option>
                ))}
              </select>
            </div>
            <div style={{ marginLeft: '30px' }}>
              {itemsPerPage !== 'All' && (
                <div className="pagination">
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                    className='custom-button'
                  >
                    &lt;
                  </button>

                  <input
                    type="number"
                    value={inputValue}
                    onChange={(e) => {
                      const newPage = parseInt(e.target.value, 10);
                      setInputValue(newPage);
                    }}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        handlePageChange(inputValue);
                      }
                    }}
                    onBlur={() => {
                      handlePageChange(inputValue);
                    }}
                    className='text'
                  />

                  <span className='text'> z {totalPages}</span>
                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                    className='custom-button'
                  >
                    &gt;
                  </button>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
      {filteredStudents.length === 0 ? (
        <div style={{ textAlign: 'center', marginTop: '40px' }}>
          <p style={{ fontSize: '1.5em' }}>{t('general.management.noSearchData')}</p>
        </div>
      ) : (
        <table className="custom-table">
          <thead>
            <tr>
              <th style={{ width: '3%', textAlign: 'center' }}>#</th>
              <th style={{ width: '17%' }}>{t('general.people.index')}</th>
              <th style={{ width: '35%' }}>{t('general.people.name')}</th>
              <th style={{ width: '35%' }}>{t('general.people.surname')}</th>
              <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.details')}</th>
            </tr>
          </thead>
          <tbody>
            {currentStudents.map((student, index) => (
              <tr key={student.mail}>
                <td className="centered">{indexOfFirstItem + index + 1}</td>
                <td>{student.index}</td>
                <td>{student.name}</td>
                <td>{student.surname}</td>
                <td>
                  <button
                    className="custom-button coverall"
                    onClick={() => {
                      navigate(`/students/${student.id}`)
                    }}
                  >
                    <i className="bi bi-arrow-right"></i>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {currentITEMS_PER_PAGE.length > 1 && itemsPerPage !== 'All' && (
        <div className="pagination">
          <button
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
            className='custom-button'
          >
            &lt;
          </button>

          <input
            type="number"
            value={inputValue}
            onChange={(e) => {
              const newPage = parseInt(e.target.value, 10);
              setInputValue(newPage);
            }}
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                handlePageChange(inputValue);
              }
            }}
            onBlur={() => {
              handlePageChange(inputValue);
            }}
            className='text'
          />

          <span className='text'> z {totalPages}</span>
          <button
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
            className='custom-button'
          >
            &gt;
          </button>
        </div>
      )}
    </div>
  );
};

export default StudentList;