import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ThesisFront, Thesis } from '../../models/thesis/Thesis';
import api from '../../utils/api';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import { useTranslation } from "react-i18next";
import SearchBar from '../../components/SearchBar';
import Slider from 'rc-slider';
import 'rc-slider/assets/index.css';
import { Role } from '../../models/user/Role';
import { Faculty } from '../../models/university/Faculty';
import { Specialization } from '../../models/university/Specialization';
import { StudyCycle } from '../../models/university/StudyCycle';
import { StudyField } from '../../models/university/StudyField';
import { Employee } from '../../models/user/Employee';
import api_access from '../../utils/api_access';
import LoadingSpinner from "../../components/LoadingSpinner";


const ThesesTable: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { i18n, t } = useTranslation();
  const navigate = useNavigate();
  const [theses, setTheses] = useState<ThesisFront[]>([]);
  const ITEMS_PER_PAGE = ['10', '25', '50', 'All'];
  const [currentITEMS_PER_PAGE, setCurrentITEMS_PER_PAGE] = useState(ITEMS_PER_PAGE);
  const [loaded, setLoaded] = useState<boolean>(false);

  useEffect(() => {
    api.get(api_access + 'thesis/public')
      .then((response) => {
        const thesis_response = response.data.map((thesisDb: Thesis) => {
          const thesis: ThesisFront = {
            id: thesisDb.id,
            namePL: thesisDb.namePL,
            nameEN: thesisDb.nameEN,
            descriptionPL: thesisDb.descriptionPL,
            descriptionEN: thesisDb.descriptionEN,
            programs: thesisDb.programs,
            studyCycle: thesisDb.studyCycle,
            numPeople: thesisDb.numPeople,
            occupied: thesisDb.occupied,
            supervisor: thesisDb.supervisor,
            status: thesisDb.status,
            leader: thesisDb.leader,
            students: thesisDb.reservations.map((reservation) => reservation.student),
            reservations: thesisDb.reservations,
            comments: thesisDb.comments,
          };
          return thesis;
        });
        setTheses(thesis_response);
        setFilteredTheses(thesis_response);
        setAfterSearchTheses(thesis_response);
        setLoaded(true);
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
  const [filteredTheses, setFilteredTheses] = useState<ThesisFront[]>(theses);

  const [availableSupervisors, setAvailableSupervisor] = useState<Employee[]>([]);
  const [selectedSupervisors, setSelectedSupervisors] = useState<number[]>([]);
  const [submittedSupervisors, setSubmittedSupervisors] = useState<number[]>([]);
  const [selectedMinVacancies, setSelectedMinVacancies] = useState<number>(0);
  const [submittedMinVacancies, setSubmittedMinVacancies] = useState<number>(0);
  const [selectedMaxVacancies, setSelectedMaxVacancies] = useState<number>(5);
  const [submittedMaxVacancies, setSubmittedMaxVacancies] = useState<number>(5);
  const [availableCycles, setAvailableCycles] = useState<StudyCycle[]>([]);
  const [selectedCycleName, setSelectedCycleName] = useState<string>("");
  const [submittedCycleName, setSubmittedCycleName] = useState<string>("");
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
    api.get(api_access + 'employee')
      .then((response) => {
        const supervisors = response.data
          .filter((employee: Employee) => employee.roles.some((role: Role) => role.name === 'supervisor'))
          .sort((a: Employee, b: Employee) => a.surname.localeCompare(b.surname));;
        setAvailableSupervisor(supervisors);
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
    api.get(api_access + 'studycycle')
      .then((response) => {
        const sortedCycles = response.data.sort((a: StudyCycle, b: StudyCycle) => {
          return a.name.localeCompare(b.name);
        });
        setAvailableCycles(sortedCycles);
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
    api.get(api_access + 'faculty')
      .then((response) => {
        const sortedFaculties = response.data.sort((a: Faculty, b: Faculty) => {
          return a.name.localeCompare(b.name);
        });
        setAvailableFaculties(sortedFaculties);
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
    api.get(api_access + 'studyfield')
      .then((response) => {
        const sortedStudyFields = response.data.sort((a: StudyField, b: StudyField) => {
          return a.name.localeCompare(b.name);
        });
        setAvailableFields(sortedStudyFields);
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
    api.get(api_access + 'specialization')
      .then((response) => {
        const sortedSpecializations = response.data.sort((a: Specialization, b: Specialization) => {
          return a.name.localeCompare(b.name);
        });
        setAvailableSpecializations(sortedSpecializations);
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
    if (loaded)
      handleFiltration(false);
  }, [loaded]);

  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleSubmitFilters = (toogle: boolean) => {

    setSubmittedFacultyAbbr(selectedFacultyAbbr)
    setSubmittedFieldAbbr(selectedFieldAbbr)
    setSubmittedSpecializationAbbr(selectedSpecializationAbbr)
    setSubmittedMinVacancies(selectedMinVacancies)
    setSubmittedMaxVacancies(selectedMaxVacancies)
    setSubmittedCycleName(selectedCycleName)
    setSubmittedSupervisors(selectedSupervisors)
    localStorage.setItem('publicThesesFilterFaculty', selectedFacultyAbbr);
    localStorage.setItem('publicThesesFilterField', selectedFieldAbbr);
    localStorage.setItem('publicThesesFilterSpecialization', selectedSpecializationAbbr);
    localStorage.setItem('publicThesesFilterMinVacancies', selectedMinVacancies.toString());
    localStorage.setItem('publicThesesFilterMaxVacancies', selectedMaxVacancies.toString());
    localStorage.setItem('publicThesesFilterCycle', selectedCycleName);
    localStorage.setItem('publicThesesFilterSupervisors', JSON.stringify(selectedSupervisors));

    if (toogle)
      handleToggleSidebar()
  };

  const handleToggleSidebar = () => {

    if (!sidebarOpen) {
      setSelectedFacultyAbbr(submittedFacultyAbbr)
      setSelectedFieldAbbr(submittedFieldAbbr)
      setSelectedSpecializationAbbr(submittedSpecializationAbbr)
      setSelectedMinVacancies(submittedMinVacancies)
      setSelectedMaxVacancies(submittedMaxVacancies)
      setSelectedCycleName(submittedCycleName)
      setSelectedSupervisors(submittedSupervisors)
    }
    setSidebarOpen(!sidebarOpen);
  };

  const handleDeleteFilters = () => {
    setSelectedCycleName("");
    setSelectedFacultyAbbr("");
    setSelectedFieldAbbr("");
    setSelectedSpecializationAbbr("");
    setSelectedMinVacancies(0);
    setSelectedMaxVacancies(5);
    setSelectedSupervisors([]);

    localStorage.removeItem('publicThesesFilterFaculty');
    localStorage.removeItem('publicThesesFilterField');
    localStorage.removeItem('publicThesesFilterSpecialization');
    localStorage.removeItem('publicThesesFilterMinVacancies');
    localStorage.removeItem('publicThesesFilterMaxVacancies');
    localStorage.removeItem('publicThesesFilterCycle');
    localStorage.removeItem('publicThesesFilterSupervisors');

    setSubmittedCycleName("");
    setSubmittedFacultyAbbr("");
    setSubmittedFieldAbbr("");
    setSubmittedSpecializationAbbr("");
    setSubmittedMinVacancies(0);
    setSubmittedMaxVacancies(5);
    setSubmittedSupervisors([]);

    setFilteredTheses(theses);
  };

  const handleFiltration = (toggle: boolean) => {

    if (toggle) {
      handleSubmitFilters(true)

      const facultyFilter = selectedFacultyAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.faculty.abbreviation === selectedFacultyAbbr) : () => true;
      const fieldFilter = selectedFieldAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.studyField ? p.studyField.abbreviation === selectedFieldAbbr : p.specialization.studyField.abbreviation === selectedFieldAbbr) : () => true;
      const specializationFilter = selectedSpecializationAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.specialization ? p.specialization.abbreviation === selectedSpecializationAbbr : false) : () => true;
      const vacanciesFilter = (thesis: ThesisFront) => {
        let vacancies = thesis.numPeople - thesis.occupied;
        if (thesis.occupied > thesis.numPeople) {
          vacancies = 0;
        }
        return vacancies >= selectedMinVacancies && vacancies <= selectedMaxVacancies;
      };
      const cycleFilter = selectedCycleName ? (thesis: ThesisFront) => thesis.studyCycle?.name === selectedCycleName : () => true;
      const supervisorFilter = selectedSupervisors.length ? (thesis: ThesisFront) => selectedSupervisors.includes(thesis.supervisor.id) : () => true;

      const newFilteredTheses = theses.filter(thesis =>
        facultyFilter(thesis) &&
        fieldFilter(thesis) &&
        specializationFilter(thesis) &&
        vacanciesFilter(thesis) &&
        cycleFilter(thesis) &&
        supervisorFilter(thesis)
      );
      setFilteredTheses(newFilteredTheses);
    }
    else {
      const savedFacultyAbbr = localStorage.getItem('publicThesesFilterFaculty') || '';
      const savedFieldAbbr = localStorage.getItem('publicThesesFilterField') || '';
      const savedSpecializationAbbr = localStorage.getItem('publicThesesFilterSpecialization') || '';
      const savedMinVacancies = parseInt(localStorage.getItem('publicThesesFilterMinVacancies') || '0', 10);
      const savedMaxVacancies = parseInt(localStorage.getItem('publicThesesFilterMaxVacancies') || '5', 10);
      const savedCycleName = localStorage.getItem('publicThesesFilterCycle') || '';
      const savedsupervisors = JSON.parse(localStorage.getItem('publicThesesFilterSupervisors') || '[]');

      setSubmittedFacultyAbbr(savedFacultyAbbr)
      setSubmittedFieldAbbr(savedFieldAbbr)
      setSubmittedSpecializationAbbr(savedSpecializationAbbr)
      setSubmittedMinVacancies(savedMinVacancies)
      setSubmittedMaxVacancies(savedMaxVacancies)
      setSubmittedCycleName(savedCycleName)
      setSubmittedSupervisors(savedsupervisors)

      const facultyFilter = savedFacultyAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.faculty.abbreviation === savedFacultyAbbr) : () => true;
      const fieldFilter = savedFieldAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.studyField ? p.studyField.abbreviation === savedFieldAbbr : p.specialization.studyField.abbreviation === selectedFieldAbbr) : () => true;
      const specializationFilter = savedSpecializationAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.specialization ? p.specialization.abbreviation === savedSpecializationAbbr : false) : () => true;
      const vacanciesFilter = (thesis: ThesisFront) => {
        let vacancies = thesis.numPeople - thesis.occupied;
        if (thesis.occupied > thesis.numPeople) {
          vacancies = 0;
        }
        return vacancies >= savedMinVacancies && vacancies <= savedMaxVacancies;
      };
      const cycleFilter = savedCycleName ? (thesis: ThesisFront) => thesis.studyCycle?.name === savedCycleName : () => true;
      const supervisorFilter = savedsupervisors.length ? (thesis: ThesisFront) => savedsupervisors.includes(thesis.supervisor.id) : () => true;

      const newFilteredTheses = theses.filter(thesis =>
        facultyFilter(thesis) &&
        fieldFilter(thesis) &&
        specializationFilter(thesis) &&
        vacanciesFilter(thesis) &&
        cycleFilter(thesis) &&
        supervisorFilter(thesis)
      );
      setFilteredTheses(newFilteredTheses);
    }
  }
  
  const filtered = () => {
    if (selectedFacultyAbbr ||
      submittedFieldAbbr ||
      submittedSpecializationAbbr ||
      submittedMinVacancies != 0 ||
      submittedMaxVacancies != 5 ||
      submittedCycleName ||
      submittedSupervisors.length > 0) {
      return true
    }
    return false
  }

  // Wyszukiwanie
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [afterSearchTheses, setAfterSearchTheses] = useState<ThesisFront[]>(theses);

  useEffect(() => {
    const searchText = searchTerm.toLowerCase();
    const filteredList = filteredTheses.filter((thesis) => {
      return (
        thesis.namePL.toLowerCase().includes(searchText) ||
        thesis.nameEN.toLowerCase().includes(searchText) ||
        (thesis.supervisor.title.name + ' ' + thesis.supervisor.name + ' ' + thesis.supervisor.surname).toLowerCase().includes(searchText)
      );
    });
    setAfterSearchTheses(() => filteredList);

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

  }, [searchTerm, filteredTheses]);

  // Paginacja
  const [currentPage, setCurrentPage] = useState(1);
  const [inputValue, setInputValue] = useState(currentPage);
  const [itemsPerPage, setItemsPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
  const [chosenItemsPerPage, setChosenItemsPerPage] = useState(itemsPerPage);
  const indexOfLastItem = itemsPerPage === 'All' ? afterSearchTheses.length : currentPage * parseInt(itemsPerPage, 10);
  const indexOfFirstItem = itemsPerPage === 'All' ? 0 : indexOfLastItem - parseInt(itemsPerPage, 10);
  const currentTheses = afterSearchTheses.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = itemsPerPage === 'All' ? 1 : Math.ceil(afterSearchTheses.length / parseInt(itemsPerPage, 10));

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
          <label className="bold" htmlFor="supervisors">
            {t('general.people.supervisor')}:
          </label>
          <div className="supervisor-checkbox-list">
            {availableSupervisors.map((supervisor) => (
              <div key={supervisor.id} className="checkbox-item mb-2">
                <input
                  type="checkbox"
                  id={`supervisor-${supervisor.id}`}
                  value={supervisor.id}
                  checked={selectedSupervisors.includes(supervisor.id)}
                  onChange={() => {
                    const updatedSupervisors = selectedSupervisors.includes(supervisor.id)
                      ? selectedSupervisors.filter((id) => id !== supervisor.id)
                      : [...selectedSupervisors, supervisor.id];
                    setSelectedSupervisors(updatedSupervisors);
                  }}
                  className="custom-checkbox"
                />
                <label style={{ marginLeft: '5px' }} htmlFor={`supervisor-${supervisor.id}`}>
                  {`${supervisor.title.name} ${supervisor.name} ${supervisor.surname}`}
                </label>
              </div>
            ))}
          </div>
        </div>
        <hr className="my-4" />
        <div className="mb-4">
          <label className="bold" htmlFor="vacancies">
            {t('general.management.vacancies')}:
          </label>
          <Slider
            range
            min={0}
            max={5}
            value={[selectedMinVacancies, selectedMaxVacancies]}
            onChange={(value: number | number[]) => {
              if (Array.isArray(value)) {
                setSelectedMinVacancies(value[0]);
                setSelectedMaxVacancies(value[1]);
              } else {
                setSelectedMinVacancies(value);
                setSelectedMaxVacancies(value);
              }
            }}
            marks={{ 0: '0', 1: '1', 2: '2', 3: '3', 4: '4', 5: '5' }}
            dots={false}
            dotStyle={{ display: 'none' }}
            className='mt-4 mb-5 custom-slider'
          />
        </div>
        <hr className="my-4" />
        <div className="mb-4">
          <label className="bold" htmlFor="cycle">
            {t('general.university.studyCycle')}:
          </label>
          <select
            id="cycle"
            name="cycle"
            value={selectedCycleName}
            onChange={(e) => {
              setSelectedCycleName(e.target.value);
            }}
            className="form-control"
          >
            <option value="">{t('general.management.choose')}</option>
            {availableCycles.map((cycle) => (
              <option key={cycle.id} value={cycle.name}>
                {cycle.name}
              </option>
            ))}
          </select>
        </div>
        <hr className="my-4" />
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
      {!loaded ? (
        <LoadingSpinner height="50vh" />
      ) : (<React.Fragment>
        {theses.length === 0 ? (
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
          {afterSearchTheses.length === 0 ? (
            <div className='info-no-data'>
              <p>{t('general.management.noSearchData')}</p>
            </div>
          ) : (
            <table className="custom-table">
              <thead>
                <tr>
                  <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                  <th style={{ width: '50%' }}>{t('general.university.thesis')}</th>
                  <th style={{ width: '15%' }}>{t('general.people.supervisor')}</th>
                  <th style={{ width: '12%', textAlign: 'center' }}>{t('general.university.studyCycle')}</th>
                  <th style={{ width: '10%', textAlign: 'center' }}>{t('thesis.occupiedSeats')}</th>
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
                    <td className="centered">{thesis.studyCycle?.name}</td>
                    <td className="centered">{thesis.status.name === "Closed" ? "-" : thesis.occupied + "/" + thesis.numPeople}</td>
                    <td>
                      <button
                        className="custom-button coverall"
                        onClick={() => { navigate(`/public-theses/${thesis.id}`, { state: { thesis } }) }}
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
}

export default ThesesTable;
