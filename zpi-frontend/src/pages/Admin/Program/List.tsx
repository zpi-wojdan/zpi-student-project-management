import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Program } from '../../../models/university/Program';
import { toast } from 'react-toastify';
import ChoiceConfirmation from '../../../components/ChoiceConfirmation';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import SearchBar from '../../../components/SearchBar';
import { handleDeletionError } from '../../../utils/handleDeleteError';
import LoadingSpinner from "../../../components/LoadingSpinner";
import api_access from '../../../utils/api_access';

const ProgramList: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const [programs, setPrograms] = useState<Program[]>([]);
  const [refreshList, setRefreshList] = useState(false);
  const ITEMS_PER_PAGE = ['10', '25', '50', 'All'];
  const [currentITEMS_PER_PAGE, setCurrentITEMS_PER_PAGE] = useState(ITEMS_PER_PAGE);
  const [loaded, setLoaded] = useState<boolean>(false);

  useEffect(() => {
    api.get(api_access + 'program')
      .then((response) => {
        const sortedPrograms = response.data.sort((a: Program, b: Program) => {
          return a.name.localeCompare(b.name);
        });
        setPrograms(sortedPrograms);
        setLoaded(true);
      })
      .catch((error) => {
        if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, [refreshList]);

  // Wyszukiwanie
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [afterSearchPrograms, setAfterSearchPrograms] = useState<Program[]>(programs);

  useEffect(() => {
    const searchText = searchTerm.toLowerCase();
    const filteredList = programs.filter((program) => {
      return (
        program.name.toLowerCase().includes(searchText) ||
        getLatestCycle(program).toLowerCase().includes(searchText)
      );
    });
    setAfterSearchPrograms(() => filteredList);

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

  }, [searchTerm, programs]);

  // Paginacja
  const [currentPage, setCurrentPage] = useState(1);
  const [inputValue, setInputValue] = useState(currentPage);
  const [itemsPerPage, setItemsPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
  const [chosenItemsPerPage, setChosenItemsPerPage] = useState(itemsPerPage);
  const indexOfLastItem = itemsPerPage === 'All' ? afterSearchPrograms.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentPrograms = afterSearchPrograms.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(afterSearchPrograms.length / parseInt(itemsPerPage, 10));

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
  const [programToDelete, setProgramToDelete] = useState<number | null>(null);

  const handleDeleteClick = (programId: number) => {
    setShowDeleteConfirmation(true);
    setProgramToDelete(programId);
  };

  const handleConfirmDelete = () => {
    api.delete(api_access + `program/${programToDelete}`)
      .then(() => {
        toast.success(t('program.deleteSuccessful'));
        setRefreshList(!refreshList);
      })
      .catch((error) => {
        if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
        handleDeletionError(error, t, 'program');
      });
    setShowDeleteConfirmation(false);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  const getLatestCycle = (program: Program) => {
    if (program.studyCycles.length === 0) {
      return "-";
    }
  
    const sortedCycles = program.studyCycles.slice().sort((a, b) => b.name.localeCompare(a.name));
  
    return sortedCycles[0].name;
  };

  return (
    <div className='page-margin'>
      <div >
        <button className="custom-button" onClick={() => { navigate('/programs/add') }}>
          {t('program.add')}
        </button>
      </div>
      {!loaded ? (
          <LoadingSpinner height="50vh" />
      ) : (<React.Fragment>
        {programs.length === 0 ? (
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
          {afterSearchPrograms.length === 0 ? (
            <div className='info-no-data'>
              <p>{t('general.management.noSearchData')}</p>
            </div>
          ) : (
            <table className="custom-table">
              <thead>
                <tr>
                  <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                  <th style={{ width: '65%' }}>{t('general.university.name')}</th>
                  <th style={{ width: '12%', textAlign: 'center' }}>{t('general.university.latestStudyCycle')}</th>
                  <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.edit')}</th>
                  <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.delete')}</th>
                </tr>
              </thead>
              <tbody>
                {currentPrograms.map((program, index) => (
                  <React.Fragment key={program.id}>
                    <tr>
                      <td className="centered">{indexOfFirstItem + index + 1}</td>
                      <td>{program.name}</td>
                      <td className="centered">{getLatestCycle(program)}</td>
                      <td>
                        <button
                          className="custom-button coverall"
                          onClick={() => {
                            navigate(`/programs/edit/${program.id}`, { state: { program } });
                          }}
                        >
                          <i className="bi bi-arrow-right"></i>
                        </button>
                      </td>
                      <td>
                        <button
                          className="custom-button coverall"
                          onClick={() => handleDeleteClick(program.id)}
                        >
                          <i className="bi bi-trash"></i>
                        </button>
                      </td>
                    </tr>
                    {programToDelete === program.id && showDeleteConfirmation && (
                      <tr>
                        <td colSpan={5}>
                          <ChoiceConfirmation
                            isOpen={showDeleteConfirmation}
                            onClose={handleCancelDelete}
                            onConfirm={handleConfirmDelete}
                            onCancel={handleCancelDelete}
                            questionText={t('program.deleteConfirmation')}
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

export default ProgramList;