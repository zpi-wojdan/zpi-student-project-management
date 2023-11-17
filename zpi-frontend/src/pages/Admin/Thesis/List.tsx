import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Thesis } from '../../../models/Thesis';
import api from '../../../utils/api';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";


const ThesisList: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();
  const [theses, setTheses] = useState<Thesis[]>([]);

  const [ITEMS_PER_PAGE, setITEMS_PER_PAGE] = useState(['10', '25', '50', 'All']);

  useEffect(() => {
    api.get('http://localhost:8080/thesis')
      .then((response) => {
        response.data.sort((a: Thesis, b: Thesis) => a.id - b.id);
        setTheses(response.data);
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
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  const [currentPage, setCurrentPage] = useState(1);
  const [inputValue, setInputValue] = useState(currentPage);
  const [itemsPerPage, setItemsPerPage] = useState((ITEMS_PER_PAGE.length > 1) ? ITEMS_PER_PAGE[1] : ITEMS_PER_PAGE[0]);
  const indexOfLastItem = itemsPerPage === 'All' ? theses.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentTheses = theses.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(theses.length / parseInt(itemsPerPage, 10));

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
      <div className='d-flex justify-content-between  align-items-center'>
        <div >
          <button className="custom-button" onClick={() => { navigate('/theses/add') }}>
            {t('thesis.add')}
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
            <th style={{ width: '60%' }}>{t('general.university.thesis')}</th>
            <th style={{ width: '17%' }}>{t('general.people.supervisor')}</th>
            <th style={{ width: '10%', textAlign: 'center' }}>{t('general.university.status')}</th>
            <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.details')}</th>
          </tr>
        </thead>
        <tbody>
          {currentTheses.map((thesis, index) => (
            <tr key={thesis.id}>
              <td className="centered">{indexOfFirstItem + index + 1}</td>
              <td>
                {i18n.language === 'pl' ? (
                  thesis.namePL
                ) : (
                  thesis.nameEN
                )}
              </td>
              <td>{thesis.supervisor.title.name + " " + thesis.supervisor.name + " " + thesis.supervisor.surname}</td>
              <td className="centered">{thesis.status.name}</td>
              <td>
                <button
                  className="custom-button coverall"
                  onClick={() => { navigate(`/theses/${thesis.id}`) }}
                >
                  <i className="bi bi-arrow-right"></i>
                </button>
              </td>
            </tr>
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
}

export default ThesisList;