import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { StudyField } from '../../../models/university/StudyField';
import { toast } from 'react-toastify';
import DeleteConfirmation from '../../../components/DeleteConfirmation';
import handleSignOut from "../../../auth/Logout";
import useAuth from '../../../auth/useAuth';
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";

const StudyFieldList: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const [ITEMS_PER_PAGE, setITEMS_PER_PAGE] = useState(['10', '25', '50', 'All']);
  const [studyFields, setStudyFields] = useState<StudyField[]>([]);
  const [refreshList, setRefreshList] = useState(false);

  useEffect(() => {
    api.get('http://localhost:8080/studyfield')
      .then((response) => {
        const sortedStudyFields = response.data.sort((a: StudyField, b: StudyField) => {
          return a.abbreviation.localeCompare(b.abbreviation);
        });
        setStudyFields(sortedStudyFields);
        const filteredItemsPerPage = ITEMS_PER_PAGE.filter(itemPerPage => {
          if (itemPerPage === 'All') {
            return true;
          } else {
            const perPageValue = parseInt(itemPerPage, 10);
            return perPageValue < response.data.length;
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
  }, [refreshList]);

  const [currentPage, setCurrentPage] = useState(1);
  const [inputValue, setInputValue] = useState(currentPage);
  const [itemsPerPage, setItemsPerPage] = useState(ITEMS_PER_PAGE[0]);
  //(ITEMS_PER_PAGE.length>1) ? ITEMS_PER_PAGE[1] : ITEMS_PER_PAGE[0]
  const indexOfLastItem = itemsPerPage === 'All' ? studyFields.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentStudyFields = studyFields.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(studyFields.length / parseInt(itemsPerPage, 10));

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
    api.delete(`http://localhost:8080/studyfield/${studyFieldToDelete}`)
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
        toast.error(t('field.deleteError'));
      });
    setShowDeleteConfirmation(false);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  return (
    <div className='page-margin'>
      <div className='d-flex justify-content-between  align-items-center'>
        <div >
          <button className="custom-button" onClick={() => { navigate('/fields/add') }}>
            {t('field.add')}
          </button>
        </div>
        {ITEMS_PER_PAGE.length > 1 && (
          <div className="d-flex justify-content-between">
            <div className="d-flex align-items-center">
              <label style={{ marginRight: '10px' }}>{t('general.management.view')}:</label>
              <select
                value={itemsPerPage}
                onChange={(e) => {
                  setItemsPerPage(e.target.value);
                  handlePageChange(1);
                }}
              >
                {ITEMS_PER_PAGE.map((value) => (
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
                    <DeleteConfirmation
                      isOpen={showDeleteConfirmation}
                      onClose={handleCancelDelete}
                      onConfirm={handleConfirmDelete}
                      onCancel={handleCancelDelete}
                      questionText={t('field.deleteConfirmation')}
                    />
                  </td>
                </tr>
              )}
            </React.Fragment>
          ))}
        </tbody>
      </table>
      {ITEMS_PER_PAGE.length > 1 && itemsPerPage !== 'All' && (
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

export default StudyFieldList;