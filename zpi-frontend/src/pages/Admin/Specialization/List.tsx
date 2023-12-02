import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Specialization } from '../../../models/university/Specialization';
import { toast } from 'react-toastify';
import ChoiceConfirmation from '../../../components/ChoiceConfirmation';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import { handleDeletionError } from '../../../utils/handleDeleteError';
import SearchBar from '../../../components/SearchBar';
import LoadingSpinner from "../../../components/LoadingSpinner";
import api_access from '../../../utils/api_access';

const SpecializationList: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const [specializations, setSpecializations] = useState<Specialization[]>([]);
  const [refreshList, setRefreshList] = useState(false);
  const ITEMS_PER_PAGE = ['10', '25', '50', 'All'];
  const [currentITEMS_PER_PAGE, setCurrentITEMS_PER_PAGE] = useState(ITEMS_PER_PAGE);
  const [loaded, setLoaded] = useState<boolean>(false);

  useEffect(() => {
    api.get(api_access + 'specialization')
      .then((response) => {
        const sortedSpecializations = response.data.sort((a: Specialization, b: Specialization) => {
          const studyFieldComparison = a.studyField.name.localeCompare(b.studyField.name);
          if (studyFieldComparison === 0) {
            return a.abbreviation.localeCompare(b.abbreviation);
          }
          return studyFieldComparison;
        });
        setSpecializations(sortedSpecializations);
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
  const [afterSearchSpecializations, setAfterSearchSpecializations] = useState<Specialization[]>(specializations);

  useEffect(() => {
    const searchText = searchTerm.toLowerCase();
    const filteredList = specializations.filter((specialization) => {
      return (
        specialization.abbreviation.toLowerCase().includes(searchText) ||
        specialization.name.toLowerCase().includes(searchText) ||
        specialization.studyField.name.toLowerCase().includes(searchText)
      );
    });
    setAfterSearchSpecializations(() => filteredList);

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

  }, [searchTerm, specializations]);

  // Paginacja
  const [currentPage, setCurrentPage] = useState(1);
  const [inputValue, setInputValue] = useState(currentPage);
  const [itemsPerPage, setItemsPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
  const [chosenItemsPerPage, setChosenItemsPerPage] = useState(itemsPerPage);
  const indexOfLastItem = itemsPerPage === 'All' ? afterSearchSpecializations.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentSpecializations = afterSearchSpecializations.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(afterSearchSpecializations.length / parseInt(itemsPerPage, 10));

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
  const [specializationToDelete, setSpecializationToDelete] = useState<number | null>(null);

  const handleDeleteClick = (specializationId: number) => {
    setShowDeleteConfirmation(true);
    setSpecializationToDelete(specializationId);
  };

  const handleConfirmDelete = () => {
    api.delete(api_access + `specialization/${specializationToDelete}`)
      .then(() => {
        toast.success(t('specialization.deleteSuccessful'));
        setRefreshList(!refreshList);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        handleDeletionError(error, t, 'specialization');
      });
    setShowDeleteConfirmation(false);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  return (
    <div className='page-margin'>
      <div >
        <button className="custom-button" onClick={() => { navigate('/specializations/add') }}>
          {t('specialization.add')}
        </button>
      </div>
      {!loaded ? (
          <LoadingSpinner height="50vh" />
      ) : (<React.Fragment>
        {specializations.length === 0 ? (
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
          {afterSearchSpecializations.length === 0 ? (
            <div className='info-no-data'>
              <p>{t('general.management.noSearchData')}</p>
            </div>
          ) : (
            <table className="custom-table">
              <thead>
                <tr>
                  <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                  <th style={{ width: '15%', textAlign: 'center' }}>{t('general.university.abbreviation')}</th>
                  <th style={{ width: '40%' }}>{t('general.university.name')}</th>
                  <th style={{ width: '22%' }}>{t('general.university.field')}</th>
                  <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.edit')}</th>
                  <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.delete')}</th>
                </tr>
              </thead>
              <tbody>
                {currentSpecializations.map((specialization, index) => (
                  <React.Fragment key={specialization.abbreviation}>
                    <tr>
                      <td className="centered">{indexOfFirstItem + index + 1}</td>
                      <td className="centered">{specialization.abbreviation}</td>
                      <td>{specialization.name}</td>
                      <td>{specialization.studyField.name}</td>
                      <td>
                        <button
                          className="custom-button coverall"
                          onClick={() => {
                            navigate(`/specializations/edit/${specialization.abbreviation}`, { state: { specialization } });
                          }}
                        >
                          <i className="bi bi-arrow-right"></i>
                        </button>
                      </td>
                      <td>
                        <button
                          className="custom-button coverall"
                          onClick={() => handleDeleteClick(specialization.id)}
                        >
                          <i className="bi bi-trash"></i>
                        </button>
                      </td>
                    </tr>
                    {specializationToDelete === specialization.id && showDeleteConfirmation && (
                      <tr>
                        <td colSpan={5}>
                          <ChoiceConfirmation
                            isOpen={showDeleteConfirmation}
                            onClose={handleCancelDelete}
                            onConfirm={handleConfirmDelete}
                            onCancel={handleCancelDelete}
                            questionText={t('specialization.deleteConfirmation')}
                          />
                        </td>
                      </tr>
                    )}
                  </React.Fragment>
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
        </React.Fragment>)}
      </React.Fragment>)}
    </div>
  );
};

export default SpecializationList;