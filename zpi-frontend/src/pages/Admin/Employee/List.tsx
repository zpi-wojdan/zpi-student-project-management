import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Employee } from '../../../models/user/Employee';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import { Department } from '../../../models/university/Department'
import SearchBar from '../../../components/SearchBar';
import api_access from '../../../utils/api_access';
import LoadingSpinner from "../../../components/LoadingSpinner";
import { Title } from "../../../models/user/Title";

const EmployeeList: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();
  const [employees, setEmployees] = useState<Employee[]>([]);
  const ITEMS_PER_PAGE = ['10', '25', '50', 'All'];
  const [currentITEMS_PER_PAGE, setCurrentITEMS_PER_PAGE] = useState(ITEMS_PER_PAGE);
  const [loaded, setLoaded] = useState<boolean>(false);

  useEffect(() => {
    api.get(api_access + 'employee')
      .then((response) => {
        const sortedEmployees = response.data.sort((a: Employee, b: Employee) => {
          return a.mail.localeCompare(b.mail);
        });
        setEmployees(sortedEmployees);
        setFilteredEmployees(sortedEmployees);
        setAfterSearchEmployees(sortedEmployees);
        setLoaded(true);
      })
      .catch((error) => {
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  // Filtrowanie
  const [filteredEmployees, setFilteredEmployees] = useState<Employee[]>(employees);

  const [availableDepartments, setAvailableDepartments] = useState<Department[]>([]);
  const [selectedDepartmentCode, setSelectedDepartmentCode] = useState<string>("");
  const [submittedDepartmentCode, setSubmittedDepartmentCode] = useState<string>("");
  const availableRoles: { [key: string]: string } = {
    supervisor: t('general.people.supervisorLC'),
    approver: t('general.people.approverLC'),
    admin: t('general.people.adminLC'),
  };
  const [selectedRoleName, setSelectedRoleName] = useState<string>("");
  const [submittedRoleName, setSubmittedRoleName] = useState<string>("");
  const [availableTitles, setAvailableTitles] = useState<Title[]>([]);
  const [selectedTitleName, setSelectedTitleName] = useState<string>("");
  const [submittedTitleName, setSubmittedTitleName] = useState<string>("");

  useEffect(() => {
    api.get(api_access + 'departments')
      .then((response) => {
        const sortedDepartments = response.data.sort((a: Department, b: Department) => {
          return a.name.localeCompare(b.name);
        });
        setAvailableDepartments(sortedDepartments);
      })
      .catch((error) => {
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  useEffect(() => {
    api.get(api_access + 'title')
      .then((response) => {
        const sortedTitles = response.data.sort((a: Title, b: Title) => {
          return a.name.localeCompare(b.name);
        });
        setAvailableTitles(response.data);
      })
      .catch((error) => {
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  useEffect(() => {
    if (loaded)
      handleFiltration(false);
  }, [loaded]);

  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleSubmitFilters = (toogle: boolean) => {

    setSubmittedDepartmentCode(selectedDepartmentCode)
    setSubmittedRoleName(selectedRoleName)
    setSubmittedTitleName(selectedTitleName)
    localStorage.setItem('employeeFilterDepartment', selectedDepartmentCode);
    localStorage.setItem('employeeFilterRole', selectedRoleName);
    localStorage.setItem('employeeFilterTitle', selectedTitleName);

    if (toogle)
      handleToggleSidebar()
  };

  const handleToggleSidebar = () => {

    if (!sidebarOpen) {
      setSelectedDepartmentCode(submittedDepartmentCode)
      setSelectedRoleName(submittedRoleName)
      setSelectedTitleName(submittedTitleName)
    }
    setSidebarOpen(!sidebarOpen);
  };

  const handleDeleteFilters = () => {
    setSelectedDepartmentCode("");
    setSelectedRoleName("");
    setSelectedTitleName("");

    localStorage.removeItem('employeeFilterDepartment');
    localStorage.removeItem('employeeFilterRole');
    localStorage.removeItem('employeeFilterTitle');

    setSubmittedDepartmentCode("");
    setSubmittedRoleName("");
    setSubmittedTitleName("");

    setFilteredEmployees(employees);
  };

  const handleFiltration = (toggle: boolean) => {

    if (toggle) {
      handleSubmitFilters(true)

      const departmentFilter = selectedDepartmentCode ? (employee: Employee) => employee.department.code === selectedDepartmentCode : () => true;
      const roleFilter = selectedRoleName ? (employee: Employee) => employee.roles.some(r => r.name === selectedRoleName) : () => true;
      const titleFilter = selectedTitleName ? (employee: Employee) => employee.title.name === selectedTitleName : () => true;

      const newFilteredEmployees = employees.filter(employee =>
        departmentFilter(employee) &&
        roleFilter(employee) &&
        titleFilter(employee)
      );
      setFilteredEmployees(newFilteredEmployees);
    }
    else {
      const savedDepartmentCode = localStorage.getItem('employeeFilterDepartment') || '';
      const savedRoleName = localStorage.getItem('employeeFilterRole') || '';
      const savedTitleName = localStorage.getItem('employeeFilterTitle') || '';

      setSubmittedDepartmentCode(savedDepartmentCode)
      setSubmittedRoleName(savedRoleName)
      setSubmittedTitleName(savedTitleName)

      const departmentFilter = savedDepartmentCode ? (employee: Employee) => employee.department.code === savedDepartmentCode : () => true;
      const roleFilter = savedRoleName ? (employee: Employee) => employee.roles.some(r => r.name === savedRoleName) : () => true;
      const titleFilter = savedTitleName ? (employee: Employee) => employee.title.name === savedTitleName : () => true;

      const newFilteredEmployees = employees.filter(employee =>
        departmentFilter(employee) &&
        roleFilter(employee) &&
        titleFilter(employee)
      );
      setFilteredEmployees(newFilteredEmployees);
    }
  }

  const filtered = () => {
    if (submittedDepartmentCode ||
      submittedRoleName ||
      submittedTitleName) {
      return true
    }
    return false
  }

  // Wyszukiwanie
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [afterSearchEmployees, setAfterSearchEmployees] = useState<Employee[]>(employees);

  useEffect(() => {
    const searchText = searchTerm.toLowerCase();
    const filteredList = filteredEmployees.filter((employee) => {
      return (
        employee.mail.toLowerCase().includes(searchText) ||
        (employee.title.name + ' ' + employee.name + ' ' + employee.surname).toLowerCase().includes(searchText)
      );
    });
    setAfterSearchEmployees(() => filteredList);

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

  }, [searchTerm, filteredEmployees]);

  // Paginacja
  const [currentPage, setCurrentPage] = useState(1);
  const [inputValue, setInputValue] = useState(currentPage);
  const [itemsPerPage, setItemsPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
  const [chosenItemsPerPage, setChosenItemsPerPage] = useState(itemsPerPage);
  const indexOfLastItem = itemsPerPage === 'All' ? afterSearchEmployees.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentEmployees = afterSearchEmployees.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(afterSearchEmployees.length / parseInt(itemsPerPage, 10));

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
      <div className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <button className={`bold custom-button ${filtered() ? '' : 'another-color'} sidebar-button ${sidebarOpen ? 'open' : ''}`} onClick={() => handleToggleSidebar()}>
          {t('general.management.filtration')} {sidebarOpen ? '◀' : '▶'}
        </button>
        <h3 className='bold my-4' style={{ textAlign: 'center' }}>{t('general.management.filtration')}</h3>
        <div className="mb-4">
          <label className="bold" htmlFor="department">
            {t('general.university.department')}:
          </label>
          <select
            id="department"
            name="department"
            value={selectedDepartmentCode}
            onChange={(e) => {
              setSelectedDepartmentCode(e.target.value);
            }}
            className="form-control"
          >
            <option value="">{t('general.management.choose')}</option>
            {availableDepartments.map((department) => (
              <option key={department.code} value={department.code}>
                {department.name}
              </option>
            ))}
          </select>
        </div>
        <hr className="my-4" />
        <div className="mb-4">
          <label className="bold" htmlFor="role">
            {t('general.people.role')}:
          </label>
          <select
            id="role"
            name="role"
            value={selectedRoleName}
            onChange={(e) => {
              setSelectedRoleName(e.target.value);
            }}
            className="form-control"
          >
            <option value="">{t('general.management.choose')}</option>
            {Object.keys(availableRoles).map((roleKey) => (
              <option key={roleKey} value={roleKey}>
                {availableRoles[roleKey]}
              </option>
            ))}
          </select>
        </div>
        <hr className="my-4" />
        <div className="mb-4">
          <label className="bold" htmlFor="title">
            {t('general.title')}:
          </label>
          <select
            id="title"
            name="title"
            value={selectedTitleName}
            onChange={(e) => {
              setSelectedTitleName(e.target.value);
            }}
            className="form-control"
          >
            <option value={""}>{t('general.management.choose')}</option>
            {availableTitles.map((title) => (
              <option key={title.name} value={title.name}>
                {title.name}
              </option>
            ))}
          </select>
        </div>
        <hr className="my-4" />
        <div className="d-flex justify-content-center my-4">
          <button className="custom-button another-color"
            onClick={() => { handleDeleteFilters() }}>
            {t('general.management.filterClear')}
          </button>
          <button className="custom-button" onClick={() => handleFiltration(true)}>
            {t('general.management.filter')}
          </button>
        </div>
      </div>
      <div>
        <button className="custom-button" onClick={() => { navigate('/employees/add') }}>
          {t('employee.add')}
        </button>
        <button className="custom-button" onClick={() => { navigate('/employees/file') }}>
          {t('employee.import')}
        </button>
      </div>
      {!loaded ? (
        <LoadingSpinner height="50vh" />
      ) :
        (<React.Fragment>

          {employees.length === 0 ? (
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
            {afterSearchEmployees.length === 0 ? (
              <div className='info-no-data'>
                <p>{t('general.management.noSearchData')}</p>
              </div>
            ) : (
              <table className="custom-table">
                <thead>
                  <tr>
                    <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                    <th style={{ width: '44%' }}>{t('general.people.fullName')}</th>
                    <th style={{ width: '43%' }}>{t('general.people.mail')}</th>
                    <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.details')}</th>
                  </tr>
                </thead>
                <tbody>
                  {currentEmployees.map((employee, index) => (
                    <tr key={employee.mail}>
                      <td className="centered">{indexOfFirstItem + index + 1}</td>
                      <td>{employee.title.name + " " + employee.name + " " + employee.surname}</td>
                      <td>{employee.mail}</td>
                      <td>
                        <button
                          className="custom-button coverall"
                          onClick={() => {
                            navigate(`/employees/${employee.id}`)
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
          </React.Fragment>)}
        </React.Fragment>)}
    </div>
  );
};

export default EmployeeList;