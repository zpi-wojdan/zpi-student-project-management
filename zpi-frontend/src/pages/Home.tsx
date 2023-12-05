import React, { useEffect, useState } from 'react';
import { useTranslation } from "react-i18next";
import api from "../utils/api";
import { Deadline } from "../models/Deadline";
import api_access from '../utils/api_access';
import LoadingSpinner from "../components/LoadingSpinner";

const HomePage: React.FC = () => {
    const { i18n, t } = useTranslation();
    const [ITEMS_PER_PAGE, setITEMS_PER_PAGE] = useState(['10', '25', '50', 'All']);
    const [deadlines, setDeadlines] = useState<Deadline[]>([]);
    const [refreshList, setRefreshList] = useState(false);
    const [loaded, setLoaded] = useState<boolean>(false);

    useEffect(() => {
        api.get(api_access + 'deadline/ordered')
            .then((response) => {
                setDeadlines(response.data);
                const filteredItemsPerPage = ITEMS_PER_PAGE.filter(itemPerPage => {
                    if (itemPerPage === 'All') {
                        return true;
                    } else {
                        const perPageValue = parseInt(itemPerPage, 10);
                        return perPageValue < response.data.length;
                    }
                });
                setITEMS_PER_PAGE(filteredItemsPerPage);
                setLoaded(true);
            })
            .catch((error) => {
                console.error(error);
            });
    }, [refreshList]);

    const [currentPage, setCurrentPage] = useState(1);
    const [inputValue, setInputValue] = useState(currentPage);
    const [itemsPerPage, setItemsPerPage] = useState((ITEMS_PER_PAGE.length > 1) ? ITEMS_PER_PAGE[1] : ITEMS_PER_PAGE[0]);
    const indexOfLastItem = itemsPerPage === 'All' ? deadlines.length : currentPage * parseInt(itemsPerPage, 10);
    const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
    const currentDeadlines = deadlines.slice(indexOfFirstItem, indexOfLastItem);
    const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(deadlines.length / parseInt(itemsPerPage, 10));

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
            {!loaded ? (
                <LoadingSpinner height="50vh" />
            ) : (<React.Fragment>
                <div className='welcome'>
                    {t('home.welcome')}
                </div>
                {deadlines.length === 0 ? (
                <div className='schedule'>
                    <p>{t('home.noSchedule')}</p>
                </div>
                ) : (<React.Fragment>
                    <div className='schedule'>
                        {t('home.schedule')}
                    </div>
                    <div className='d-flex justify-content-between  align-items-center'>
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
                                <th style={{ width: '72%' }}>{t('deadline.activity')}</th>
                                <th style={{ width: '25%', textAlign: 'center' }}>{t('deadline.deadline')}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {currentDeadlines.map((deadline, index) => (
                                <React.Fragment key={deadline.id}>
                                    <tr>
                                        <td className="centered">{indexOfFirstItem + index + 1}</td>
                                        <td style={new Date(deadline.deadlineDate).setHours(0, 0, 0, 0) <
                                            new Date().setHours(0, 0, 0, 0) ?
                                            { textDecoration: 'line-through' } : {}}>
                                            {i18n.language === 'pl' ? (
                                                deadline.namePL
                                            ) : (
                                                deadline.nameEN
                                            )}
                                        </td>
                                        <td className="centered">{new Date(deadline.deadlineDate).toLocaleDateString()}</td>
                                    </tr>
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
                </React.Fragment>)}
            </React.Fragment>)}
        </div>
    );
};

export default HomePage;
