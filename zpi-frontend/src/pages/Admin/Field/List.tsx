import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { StudyField } from '../../../models/university/StudyField';
import { toast } from 'react-toastify';
import ChoiceConfirmation from '../../../components/ChoiceConfirmation';
import handleSignOut from "../../../auth/Logout";
import useAuth from '../../../auth/useAuth';
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import { handleDeletionError } from '../../../utils/handleDeleteError';
import SearchBar from '../../../components/SearchBar';
import api_access from '../../../utils/api_access';
import LoadingSpinner from "../../../components/LoadingSpinner";

const StudyFieldList: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const [studyFields, setStudyFields] = useState<StudyField[]>([]);
  const [refreshList, setRefreshList] = useState(false);
  const ITEMS_PER_PAGE = ['10', '25', '50', 'All'];
  const [currentITEMS_PER_PAGE, setCurrentITEMS_PER_PAGE] = useState(ITEMS_PER_PAGE);
  const [loaded, setLoaded] = useState<boolean>(false);

  useEffect(() => {
    api.get(api_access + 'studyfield')
      .then((response) => {
        const sortedStudyFields = response.data.sort((a: StudyField, b: StudyField) => {
          return a.abbreviation.localeCompare(b.abbreviation);
        });
        setStudyFields(sortedStudyFields);
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
  const [afterSearchStudyFields, setAfterSearchFields] = useState<StudyField[]>(studyFields);

  useEffect(() => {
    const searchText = searchTerm.toLowerCase();
    const filteredList = studyFields.filter((field) => {
      return (
        field.abbreviation.toLowerCase().includes(searchText) ||
        field.name.toLowerCase().includes(searchText)
      );
    });
    setAfterSearchFields(() => filteredList);

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

  }, [searchTerm, studyFields]);

  // Paginacja
  const [currentPage, setCurrentPage] = useState(1);
  const [inputValue, setInputValue] = useState(currentPage);
  const [itemsPerPage, setItemsPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
  const [chosenItemsPerPage, setChosenItemsPerPage] = useState(itemsPerPage);
  const indexOfLastItem = itemsPerPage === 'All' ? afterSearchStudyFields.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentStudyFields = afterSearchStudyFields.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(afterSearchStudyFields.length / parseInt(itemsPerPage, 10));

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
  const [studyFieldToDelete, setStudyFieldToDelete] = useState<number | null>(null);

  const handleDeleteClick = (studyFieldId: number) => {
    setShowDeleteConfirmation(true);
    setStudyFieldToDelete(studyFieldId);
  };

  const handleConfirmDelete = () => {
    api.delete(api_access + `studyfield/${studyFieldToDelete}`)
      .then(() => {
        toast.success(t('field.deleteSuccessful'));
        setRefreshList(!refreshList);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        handleDeletionError(error, t, 'study_field');
      });
    setShowDeleteConfirmation(false);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  return (
    <div className='page-margin'>
      <div >
        <button className="custom-button" onClick={() => { navigate('/fields/add') }}>
          {t('study_field.add')}
        </button>
      </div>
      {!loaded ? (
          <LoadingSpinner height="50vh" />
      ) : (<React.Fragment>
        {studyFields.length === 0 ? (
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

                      <span className='text'> {t('general.pagination')} {totalPages}</span>
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
          {afterSearchStudyFields.length === 0 ? (
            <div className='info-no-data'>
              <p>{t('general.management.noSearchData')}</p>
            </div>
          ) : (
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
                {currentStudyFields.map((studyField, index) => (
                  <React.Fragment key={studyField.abbreviation}>
                    <tr>
                      <td className="centered">{indexOfFirstItem + index + 1}</td>
                      <td className="centered">{studyField.abbreviation}</td>
                      <td>{studyField.name}</td>
                      <td>
                        <button
                          className="custom-button coverall"
                          onClick={() => {
                            navigate(`/fields/edit/${studyField.abbreviation}`, { state: { studyField } });
                          }}
                        >
                          <i className="bi bi-arrow-right"></i>
                        </button>
                      </td>
                      <td>
                        <button
                          className="custom-button coverall"
                          onClick={() => handleDeleteClick(studyField.id)}
                        >
                          <i className="bi bi-trash"></i>
                        </button>
                      </td>
                    </tr>
                    {studyFieldToDelete === studyField.id && showDeleteConfirmation && (
                      <tr>
                        <td colSpan={5}>
                          <ChoiceConfirmation
                            isOpen={showDeleteConfirmation}
                            onClose={handleCancelDelete}
                            onConfirm={handleConfirmDelete}
                            onCancel={handleCancelDelete}
                            questionText={t('study_field.deleteConfirmation')}
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

              <span className='text'> {t('general.pagination')} {totalPages}</span>
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

export default StudyFieldList;