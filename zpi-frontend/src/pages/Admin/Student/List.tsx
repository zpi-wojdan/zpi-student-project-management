import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Student } from '../../../models/user/Student';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";
import { useTranslation } from "react-i18next";
import api from "../../../utils/api";
import SearchBar from '../../../components/SeatchBar';
import { Faculty } from '../../../models/university/Faculty';
import { StudyField } from '../../../models/university/StudyField';
import { Specialization } from '../../../models/university/Specialization';

const StudentList: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();
  const [students, setStudents] = useState<Student[]>([]);
  const ITEMS_PER_PAGE = ['10', '25', '50', 'All'];
  const [currentITEMS_PER_PAGE, setCurrentITEMS_PER_PAGE] = useState(ITEMS_PER_PAGE);

  useEffect(() => {
    api.get('http://localhost:8080/student')
      .then((response) => {
        response.data.sort((a: Student, b: Student) => parseInt(a.index, 10) - parseInt(b.index, 10));
        setStudents(response.data);
        setFilteredStudents(response.data);
        setAfterSearchStudents(response.data);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  // Filtrowanie
  const [filteredStudents, setFilteredStudents] = useState<Student[]>(students);

  const [availableFaculties, setAvailableFaculties] = useState<Faculty[]>([]);
  const [selectedFacultyAbbr, setSelectedFacultyAbbr] = useState<string>("");
  const [submittedFacultyAbbr, setSubmittedFacultyAbbr] = useState<string>("");
  const [availableFields, setAvailableFields] = useState<StudyField[]>([]);
  const [selectedFieldAbbr, setSelectedFieldAbbr] = useState<string>("");
  const [submittedFieldAbbr, setSubmittedFieldAbbr] = useState<string>("");
  const [availableSpecializations, setAvailableSpecializations] = useState<Specialization[]>([]);
  const [selectedSpecializationAbbr, setSelectedSpecializationAbbr] = useState<string>("");
  const [submittedSpecializationAbbr, setSubmittedSpecializationAbbr] = useState<string>("");

  useEffect(() => {
    api.get('http://localhost:8080/faculty')
      .then((response) => {
        setAvailableFaculties(response.data);
      })
      .catch((error) => {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  useEffect(() => {
    api.get('http://localhost:8080/studyfield')
      .then((response) => {
        setAvailableFields(response.data);
      })
      .catch((error) => {
        console.error(error)
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  useEffect(() => {
    api.get('http://localhost:8080/specialization')
      .then((response) => {
        setAvailableSpecializations(response.data);
      })
      .catch((error) => {
        console.error(error)
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleToggleSidebar = (submitted: boolean) => {

    if (submitted) {
      setSubmittedFacultyAbbr(selectedFacultyAbbr)
      setSubmittedFieldAbbr(selectedFieldAbbr)
      setSubmittedSpecializationAbbr(selectedSpecializationAbbr)
    }
    if (!sidebarOpen) {
      setSelectedFacultyAbbr(submittedFacultyAbbr)
      setSelectedFieldAbbr(submittedFieldAbbr)
      setSelectedSpecializationAbbr(submittedSpecializationAbbr)
    }
    setSidebarOpen(!sidebarOpen);
  };

  const handleFiltration = () => {
    handleToggleSidebar(true)

    const facultyFilter = selectedFacultyAbbr ? (student: Student) => student.studentProgramCycles.some(sp => sp.program.faculty.abbreviation === selectedFacultyAbbr) : () => true;
    const fieldFilter = selectedFieldAbbr ? (student: Student) => student.studentProgramCycles.some(sp => sp.program.studyField ? sp.program.studyField.abbreviation === selectedFieldAbbr : sp.program.specialization.studyField.abbreviation === selectedFieldAbbr) : () => true;
    const specializationFilter = selectedSpecializationAbbr ? (student: Student) => student.studentProgramCycles.some(sp => sp.program.specialization ? sp.program.specialization.abbreviation === selectedSpecializationAbbr : false) : () => true;

    const newFilteredStudents = students.filter(student =>
      facultyFilter(student) &&
      fieldFilter(student) &&
      specializationFilter(student)
    );
    setFilteredStudents(newFilteredStudents);
  }

  // Wyszukiwanie
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [afterSearchStudents, setAfterSearchStudents] = useState<Student[]>(students);

  useEffect(() => {
    const searchText = searchTerm.toLowerCase();
    const filteredList = filteredStudents.filter((student) => {
      return (
        student.index.toLowerCase().includes(searchText) ||
        student.name.toLowerCase().includes(searchText) ||
        student.surname.toLowerCase().includes(searchText)
      );
    });
    setAfterSearchStudents(() => filteredList);

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

  }, [searchTerm, filteredStudents]);

  // Paginacja
  const [currentPage, setCurrentPage] = useState(1);
  const [inputValue, setInputValue] = useState(currentPage);
  const [itemsPerPage, setItemsPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
  const [chosenItemsPerPage, setChosenItemsPerPage] = useState(itemsPerPage);
  const indexOfLastItem = itemsPerPage === 'All' ? afterSearchStudents.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentStudents = afterSearchStudents.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(afterSearchStudents.length / parseInt(itemsPerPage, 10));

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
        <button className={`bold custom-button sidebar-button ${sidebarOpen ? 'open' : ''}`} onClick={() => handleToggleSidebar(false)}>
          {t('general.management.filtration')} {sidebarOpen ? '◀' : '▶'}
        </button>
        <h3 className='bold my-4' style={{ textAlign: 'center' }}>{t('general.management.filtration')}</h3>
        <div className="mb-4">
          <label className="bold" htmlFor="faculty">
            {t('general.university.faculty')}:
          </label>
          <select
            id="faculty"
            name="faculty"
            value={selectedFacultyAbbr}
            onChange={(e) => {
              setSelectedFacultyAbbr(e.target.value);
              setSelectedFieldAbbr("")
              setSelectedSpecializationAbbr("")
            }}
            className="form-control"
          >
            <option value="">{t('general.management.choose')}</option>
            {availableFaculties.map((faculty) => (
              <option key={faculty.abbreviation} value={faculty.abbreviation}>
                {faculty.name}
              </option>
            ))}
          </select>
        </div>
        <div className="mb-4">
          <label className="bold" htmlFor="studyField">
            {t('general.university.field')}:
          </label>
          <select
            id="studyField"
            name="studyField"
            value={selectedFieldAbbr}
            onChange={(e) => {
              setSelectedFieldAbbr(e.target.value);
              setSelectedSpecializationAbbr("")
            }}
            className="form-control"
            disabled={selectedFacultyAbbr === ""}
          >
            <option value={""}>{t('general.management.choose')}</option>
            {selectedFacultyAbbr !== "" &&
              availableFields
                .filter((fi) => fi.faculty.abbreviation === selectedFacultyAbbr)
                .map((field, fIndex) => (
                  <option key={fIndex} value={field.abbreviation}>
                    {field.name}
                  </option>
                ))}
          </select>
        </div>
        <div className="mb-4">
          <label className="bold" htmlFor="specialization">
            {t('general.university.specialization')}:
          </label>
          <select
            id="specialization"
            name="specialization"
            value={selectedSpecializationAbbr}
            onChange={(e) => {
              setSelectedSpecializationAbbr(e.target.value);
            }}
            className="form-control"
            disabled={selectedFieldAbbr === ""}
          >
            <option value={""}>{t('general.management.choose')}</option>
            {selectedFieldAbbr !== "" &&
              availableSpecializations
                .filter((s) => s.studyField.abbreviation === selectedFieldAbbr)
                .map((specialization, sIndex) => (
                  <option key={sIndex} value={specialization.abbreviation}>
                    {specialization.name}
                  </option>
                ))}
          </select>
        </div>
        <div className="d-flex justify-content-center mt-5">
          <button className="custom-button another-color"
            onClick={() => {
              setSelectedFacultyAbbr("");
              setSelectedFieldAbbr("");
              setSelectedSpecializationAbbr("");
            }}>
            {t('general.management.filterClear')}
          </button>
          <button className="custom-button" onClick={() => handleFiltration()}>
            {t('general.management.filter')}
          </button>
        </div>
      </div>
      <div >
        <button className="custom-button" onClick={() => { navigate('/students/add') }}>
          {t('student.add')}
        </button>
        <button className="custom-button" onClick={() => { navigate('/file/student') }}>
          {t('student.import')}
        </button>
      </div>
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
      {afterSearchStudents.length === 0 ? (
        <div style={{ textAlign: 'center', marginTop: '40px' }}>
          <p style={{ fontSize: '1.5em' }}>{t('general.management.noSearchData')}</p>
        </div>
      ) : (
        <table className="custom-table">
          <thead>
            <tr>
              <th style={{ width: '3%', textAlign: 'center' }}>#</th>
              <th style={{ width: '17%' }}>{t('general.people.index')}</th>
              <th style={{ width: '35%' }}>{t('general.people.name')}</th>
              <th style={{ width: '35%' }}>{t('general.people.surname')}</th>
              <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.details')}</th>
            </tr>
          </thead>
          <tbody>
            {currentStudents.map((student, index) => (
              <tr key={student.mail}>
                <td className="centered">{indexOfFirstItem + index + 1}</td>
                <td>{student.index}</td>
                <td>{student.name}</td>
                <td>{student.surname}</td>
                <td>
                  <button
                    className="custom-button coverall"
                    onClick={() => {
                      navigate(`/students/${student.id}`)
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
    </div>
  );
};

export default StudentList;