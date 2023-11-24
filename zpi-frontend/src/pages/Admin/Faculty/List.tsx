import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Faculty } from '../../../models/university/Faculty';
import { toast } from 'react-toastify';
import DeleteConfirmation from '../../../components/DeleteConfirmation';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import SearchBar from '../../../components/SeatchBar';

const FacultyList: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const [faculties, setFaculties] = useState<Faculty[]>([]);
  const [refreshList, setRefreshList] = useState(false);
  const ITEMS_PER_PAGE = ['10', '25', '50', 'All'];
  const [currentITEMS_PER_PAGE, setCurrentITEMS_PER_PAGE] = useState(ITEMS_PER_PAGE);
  const [loaded, setLoaded] = useState<boolean>(false);

  useEffect(() => {
    api.get('http://localhost:8080/faculty')
      .then((response) => {
        const sortedFaculties = response.data.sort((a: Faculty, b: Faculty) => {
          return a.abbreviation.localeCompare(b.abbreviation);
        });
        setFaculties(sortedFaculties);
        setLoaded(true);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, [refreshList]);

  // Wyszukiwanie
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [afterSearchFaculties, setAfterSearchFaculties] = useState<Faculty[]>(faculties);

  useEffect(() => {
    const searchText = searchTerm.toLowerCase();
    const filteredList = faculties.filter((faculty) => {
      return (
        faculty.abbreviation.toLowerCase().includes(searchText) ||
        faculty.name.toLowerCase().includes(searchText)
      );
    });
    setAfterSearchFaculties(() => filteredList);

    // Aktualizacja ustawień paginacji
    const filteredItemsPerPage = ITEMS_PER_PAGE.filter((itemPerPage) => {
      if (itemPerPage === 'All') {
        return true;
      } else {
        const perPageValue = parseInt(itemPerPage, 10);
        return perPageValue < filteredList.length;
      }
    });
    setCurrentITEMS_PER_PAGE(() => filteredItemsPerPage);

    handlePageChange(1);
    setItemsPerPage((filteredItemsPerPage.includes(chosenItemsPerPage)) ? chosenItemsPerPage : ((filteredItemsPerPage.length > 1) ? filteredItemsPerPage[1] : filteredItemsPerPage[0]));

  }, [searchTerm, faculties]);

  // Paginacja
  const [currentPage, setCurrentPage] = useState(1);
  const [inputValue, setInputValue] = useState(currentPage);
  const [itemsPerPage, setItemsPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
  const [chosenItemsPerPage, setChosenItemsPerPage] = useState(itemsPerPage);
  const indexOfLastItem = itemsPerPage === 'All' ? afterSearchFaculties.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentFaculties = afterSearchFaculties.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(afterSearchFaculties.length / parseInt(itemsPerPage, 10));

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

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
  const [facultyToDelete, setFacultyToDelete] = useState<number | null>(null);

  const handleDeleteClick = (facultyId: number) => {
    setShowDeleteConfirmation(true);
    setFacultyToDelete(facultyId);
  };

  const handleConfirmDelete = () => {
    api.delete(`http://localhost:8080/faculty/${facultyToDelete}`)
      .then(() => {
        toast.success(t('faculty.deleteSuccessful'));
        setRefreshList(!refreshList);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        toast.error(t('faculty.deleteError'));
      });
    setShowDeleteConfirmation(false);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  return (
    <div className='page-margin'>
      <div >
        <button className="custom-button" onClick={() => { navigate('/faculties/add') }}>
          {t('faculty.add')}
        </button>
      </div>
      {!loaded ? (
        <div className='info-no-data'>
          <p>{t('general.management.load')}</p>
        </div>
      ) : (<React.Fragment>
        {faculties.length === 0 ? (
          <div className='info-no-data'>
            <p>{t('general.management.noData')}</p>
          </div>
        ) : (<React.Fragment>
          <div className='d-flex justify-content-between  align-items-center'>
            <SearchBar
              searchTerm={searchTerm}
              setSearchTerm={setSearchTerm}
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

          <table className="custom-table">
            <thead>
              <tr>
                <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                <th style={{ width: '15%', textAlign: 'center' }}>{t('general.university.abbreviation')}</th>
                <th style={{ width: '62%' }}>{t('general.university.name')}</th>
                <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.edit')}</th>
                <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.delete')}</th>
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
                        onClick={() => handleDeleteClick(faculty.id)}
                      >
                        <i className="bi bi-trash"></i>
                      </button>
                    </td>
                  </tr>
                  {facultyToDelete === faculty.id && showDeleteConfirmation && (
                    <tr>
                      <td colSpan={5}>
                        <DeleteConfirmation
                          isOpen={showDeleteConfirmation}
                          onClose={handleCancelDelete}
                          onConfirm={handleConfirmDelete}
                          onCancel={handleCancelDelete}
                          questionText={t('faculty.deleteConfirmation')}
                        />
                      </td>
                    </tr>
                  )}
                </React.Fragment>
              ))}
            </tbody>
          </table>
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
        </React.Fragment>)}
      </React.Fragment>)}
    </div>
  );
};

export default FacultyList;