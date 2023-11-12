import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { StudyCycle } from '../../../models/StudyCycle';
import { toast } from 'react-toastify';
import DeleteConfirmation from '../../../components/DeleteConfirmation';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import {useTranslation} from "react-i18next";
import api from "../../../utils/api";

const StudyCycleList: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const [ITEMS_PER_PAGE, setITEMS_PER_PAGE] = useState(['10', '25', '50', 'All']);
  const [cycles, setCycles] = useState<StudyCycle[]>([]);
  const [refreshList, setRefreshList] = useState(false);

  useEffect(() => {
    api.get('http://localhost:8080/studycycle')
      .then((response) => {
        const sortedCycles = response.data.sort((a: StudyCycle, b: StudyCycle) => a.id - b.id);
        setCycles(sortedCycles);
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
  const [itemsPerPage, setItemsPerPage] = useState((ITEMS_PER_PAGE.length>1) ? ITEMS_PER_PAGE[1] : ITEMS_PER_PAGE[0]);
  const indexOfLastItem = itemsPerPage === 'All' ? cycles.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentCycles = cycles.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(cycles.length / parseInt(itemsPerPage, 10));

  const handlePageChange = (newPage: number) => {
    if(!newPage || newPage<1){
      setCurrentPage(1);
      setInputValue(1);
    }
    else {
      if(newPage>totalPages){
        setCurrentPage(totalPages);
        setInputValue(totalPages);
      }
      else{
        setCurrentPage(newPage);
        setInputValue(newPage);
      }
    }
  };

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
  const [studyCycleToDelete, setStudyCycleToDelete] = useState<number | null>(null);

  const handleDeleteClick = (studyCycleId: number) => {
    setShowDeleteConfirmation(true);
    setStudyCycleToDelete(studyCycleId);
  };

  const handleConfirmDelete = () => {
    api.delete(`http://localhost:8080/studycycle/${studyCycleToDelete}`)
        .then(() => {
          toast.success(t('cycle.deleteSuccessful'));
          setRefreshList(!refreshList);
        })
        .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
            }
            toast.error(t('cycle.deleteError'));
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
          <button className="custom-button" onClick={() => {navigate('/cycles/add')}}>
              {t('cycle.add')}
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
            <th style={{ width: '77%' }}>{t('general.university.name')}</th>
            <th style={{ width: '10%', textAlign: 'center'  }}>{t('general.management.edit')}</th>
            <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.delete')}</th>
          </tr>
        </thead>
        <tbody>
          {currentCycles.map((studyCycle, index) => (
            <React.Fragment key={studyCycle.id}>
              <tr>
                <td className="centered">{indexOfFirstItem + index + 1}</td>
                <td>{studyCycle.name}</td>
                <td>
                  <button
                    className="custom-button coverall"
                    onClick={() => {
                      navigate(`/cycles/edit/${studyCycle.id}`, { state: { studyCycle } });
                    }}
                  >
                    <i className="bi bi-arrow-right"></i>
                  </button>
                </td>
                <td>
                  <button
                    className="custom-button coverall"
                    onClick={() => handleDeleteClick(studyCycle.id)}
                  >
                    <i className="bi bi-trash"></i>
                  </button>
                </td>
              </tr>
              {studyCycleToDelete === studyCycle.id && showDeleteConfirmation && (
                <tr>
                  <td colSpan={5}>
                  <DeleteConfirmation
                    isOpen={showDeleteConfirmation}
                    onClose={handleCancelDelete}
                    onConfirm={handleConfirmDelete}
                    onCancel={handleCancelDelete}
                    questionText={t('cycle.deleteConfirmation')}
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

export default StudyCycleList;