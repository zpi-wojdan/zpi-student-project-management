import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import ChoiceConfirmation from '../../../components/ChoiceConfirmation';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import api_access from '../../../utils/api_access';
import LoadingSpinner from "../../../components/LoadingSpinner";
import {Title} from "../../../models/user/Title";

const TitleList: React.FC = () => {
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const navigate = useNavigate();
    const { i18n, t } = useTranslation();
    const [ITEMS_PER_PAGE, setITEMS_PER_PAGE] = useState(['10', '25', '50', 'All']);
    const [titles, setTitles] = useState<Title[]>([]);
    const [refreshList, setRefreshList] = useState(false);
    const [loaded, setLoaded] = useState<boolean>(false);

    useEffect(() => {
        api.get(api_access + 'title')
            .then((response) => {
                setTitles(response.data);
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
                if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });
    }, [refreshList]);

    const [currentPage, setCurrentPage] = useState(1);
    const [inputValue, setInputValue] = useState(currentPage);
    const [itemsPerPage, setItemsPerPage] = useState((ITEMS_PER_PAGE.length > 1) ? ITEMS_PER_PAGE[1] : ITEMS_PER_PAGE[0]);
    const indexOfLastItem = itemsPerPage === 'All' ? titles.length : currentPage * parseInt(itemsPerPage, 10);
    const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
    const currentTitles = titles.slice(indexOfFirstItem, indexOfLastItem);
    const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(titles.length / parseInt(itemsPerPage, 10));

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
    const [titleToDelete, setTitleToDelete] = useState<number | null>(null);

    const handleDeleteClick = (facultyId: number) => {
        setShowDeleteConfirmation(true);
        setTitleToDelete(facultyId);
    };

    const handleConfirmDelete = () => {
        api.delete(api_access + `title/${titleToDelete}`)
            .then(() => {
                toast.success(t('title.deleteSuccessful'));
                setRefreshList(!refreshList);
            })
            .catch((error) => {
                if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
                toast.error(t('title.deleteError'));
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
                    <button className="custom-button" onClick={() => { navigate('/titles/add') }}>
                        {t('title.add')}
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
            {!loaded ? (
                <LoadingSpinner height="50vh" />
            ) : (<React.Fragment>
                {titles.length === 0 ? (
                    <div className='info-no-data'>
                        <p>{t('general.management.noData')}</p>
                    </div>
                ) : (<React.Fragment>
                    <table className="custom-table">
                        <thead>
                            <tr>
                                <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                                <th style={{ width: '62%' }}>{t('general.university.name')}</th>
                                <th style={{ width: '15%', textAlign: 'center' }}>{t('general.thesesLimit')}</th>
                                <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.edit')}</th>
                                <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.delete')}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {currentTitles.map((title, index) => (
                                <React.Fragment key={title.id}>
                                    <tr>
                                        <td className="centered">{indexOfFirstItem + index + 1}</td>
                                        <td>{title.name}</td>
                                        <td className="centered">{title.numTheses}</td>
                                        <td>
                                            <button
                                                className="custom-button coverall"
                                                onClick={() => {
                                                    navigate(`/titles/edit/${title.id}`, { state: { title } });
                                                }}
                                            >
                                                <i className="bi bi-arrow-right"></i>
                                            </button>
                                        </td>
                                        <td>
                                            <button
                                                className="custom-button coverall"
                                                onClick={() => handleDeleteClick(title.id)}
                                            >
                                                <i className="bi bi-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                    {titleToDelete === title.id && showDeleteConfirmation && (
                                        <tr>
                                            <td colSpan={5}>
                                                <ChoiceConfirmation
                                                    isOpen={showDeleteConfirmation}
                                                    onClose={handleCancelDelete}
                                                    onConfirm={handleConfirmDelete}
                                                    onCancel={handleCancelDelete}
                                                    questionText={t('title.deleteConfirmation')}
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
                </React.Fragment>)}
            </React.Fragment>)}
        </div>
    );
};

export default TitleList;
