import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { Thesis, ThesisFront } from "../../../models/thesis/Thesis";
import { Student } from "../../../models/user/Student";
import handleSignOut from "../../../auth/Logout";
import api from "../../../utils/api";
import { StudyField } from "../../../models/university/StudyField";
import { Specialization } from "../../../models/university/Specialization";
import { Faculty } from "../../../models/university/Faculty";
import { Employee } from "../../../models/user/Employee";
import { StudyCycle } from "../../../models/university/StudyCycle";
import { Role } from "../../../models/user/Role";
import React from "react";
import SearchBar from "../../../components/SearchBar";
import useAuth from "../../../auth/useAuth";
import { toast } from "react-toastify";
import ChoiceConfirmation from "../../../components/ChoiceConfirmation";
import { Alert, Spinner } from "react-bootstrap";
import api_access from "../../../utils/api_access";
import LoadingSpinner from "../../../components/LoadingSpinner";


const ClearDataByCycle: React.FC = () => {

    enum ClearingMode {
        NONE = 0,
        DELETE_THESES = 1,
        STUDENTS = 2,
        ARCHIVE_THESES = 3
    }

    //  podstawa
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const { i18n, t } = useTranslation();
    const navigate = useNavigate();

    const [theses, setTheses] = useState<ThesisFront[]>([]);
    const [closedTheses, setClosedTheses] = useState<ThesisFront[]>([]);
    const [students, setStudents] = useState<Student[]>([]);

    const [selectedToClear, setSelectedToClear] = useState<ClearingMode>(ClearingMode.STUDENTS);
    const [key, setKey] = useState(0);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  filtrowanie:
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    const [sidebarOpen, setSidebarOpen] = useState(false);

    //  usuwanych tematów:
    const [filteredThesesDeleting, setFilteredThesesDeleting] = useState<ThesisFront[]>(theses);

    const [availableSupervisorsThesesDeleting, setAvailableSupervisorThesesDeleting] = useState<Employee[]>([]);
    const [selectedSupervisorsThesesDeleting, setSelectedSupervisorsThesesDeleting] = useState<number[]>([]);
    const [submittedSupervisorsThesesDeleting, setSubmittedSupervisorsThesesDeleting] = useState<number[]>([]);
    const [availableCyclesThesesDeleting, setAvailableCyclesThesesDeleting] = useState<StudyCycle[]>([]);
    const [selectedCycleNameThesesDeleting, setSelectedCycleNameThesesDeleting] = useState<string>("");
    const [submittedCycleNameThesesDeleting, setSubmittedCycleNameThesesDeleting] = useState<string>("");
    const [availableFacultiesThesesDeleting, setAvailableFacultiesThesesDeleting] = useState<Faculty[]>([]);
    const [selectedFacultyAbbrThesesDeleting, setSelectedFacultyAbbrThesesDeleting] = useState<string>("");
    const [submittedFacultyAbbrThesesDeleting, setSubmittedFacultyAbbrThesesDeleting] = useState<string>("");
    const [availableFieldsThesesDeleting, setAvailableFieldsThesesDeleting] = useState<StudyField[]>([]);
    const [selectedFieldAbbrThesesDeleting, setSelectedFieldAbbrThesesDeleting] = useState<string>("");
    const [submittedFieldAbbrThesesDeleting, setSubmittedFieldAbbrThesesDeleting] = useState<string>("");
    const [availableSpecializationsThesesDeleting, setAvailableSpecializationsThesesDeleting] = useState<Specialization[]>([]);
    const [selectedSpecializationAbbrThesesDeleting, setSelectedSpecializationAbbrThesesDeleting] = useState<string>("");
    const [submittedSpecializationAbbrThesesDeleting, setSubmittedSpecializationAbbrThesesDeleting] = useState<string>("");

    //  archiwizowanie tematów
    const [filteredThesesArchive, setFilteredThesesArchive] = useState<ThesisFront[]>(theses);

    const [availableSupervisorsThesesArchive, setAvailableSupervisorThesesArchive] = useState<Employee[]>([]);
    const [selectedSupervisorsThesesArchive, setSelectedSupervisorsThesesArchive] = useState<number[]>([]);
    const [submittedSupervisorsThesesArchive, setSubmittedSupervisorsThesesArchive] = useState<number[]>([]);
    const [availableCyclesThesesArchive, setAvailableCyclesThesesArchive] = useState<StudyCycle[]>([]);
    const [selectedCycleNameThesesArchive, setSelectedCycleNameThesesArchive] = useState<string>("");
    const [submittedCycleNameThesesArchive, setSubmittedCycleNameThesesArchive] = useState<string>("");
    const [availableFacultiesThesesArchive, setAvailableFacultiesThesesArchive] = useState<Faculty[]>([]);
    const [selectedFacultyAbbrThesesArchive, setSelectedFacultyAbbrThesesArchive] = useState<string>("");
    const [submittedFacultyAbbrThesesArchive, setSubmittedFacultyAbbrThesesArchive] = useState<string>("");
    const [availableFieldsThesesArchive, setAvailableFieldsThesesArchive] = useState<StudyField[]>([]);
    const [selectedFieldAbbrThesesArchive, setSelectedFieldAbbrThesesArchive] = useState<string>("");
    const [submittedFieldAbbrThesesArchive, setSubmittedFieldAbbrThesesArchive] = useState<string>("");
    const [availableSpecializationsThesesArchive, setAvailableSpecializationsThesesArchive] = useState<Specialization[]>([]);
    const [selectedSpecializationAbbrThesesArchive, setSelectedSpecializationAbbrThesesArchive] = useState<string>("");
    const [submittedSpecializationAbbrThesesArchive, setSubmittedSpecializationAbbrThesesArchive] = useState<string>("");
    const availableStatuses: { [key: string]: string } = {
        "Pending approval": t('status.pending'),
        "Rejected": t('status.rejected'),
        "Approved": t('status.approved'),
        "Assigned": t('status.assigned')
    };
    const [selectedStatusesNameThesesArchive, setSelectedStatusesNameThesesArchive] = useState<string>("");
    const [submittedStatusesNameThesesArchive, setSubmittedStatusesNameThesesArchive] = useState<string>("");

    //  studentów:
    const [filteredStudents, setFilteredStudents] = useState<Student[]>(students);

    const [availableFacultiesStudents, setAvailableFacultiesStudents] = useState<Faculty[]>([]);
    const [selectedFacultyAbbrStudents, setSelectedFacultyAbbrStudents] = useState<string>("");
    const [submittedFacultyAbbrStudents, setSubmittedFacultyAbbrStudents] = useState<string>("");
    const [availableFieldsStudents, setAvailableFieldsStudents] = useState<StudyField[]>([]);
    const [selectedFieldAbbrStudents, setSelectedFieldAbbrStudents] = useState<string>("");
    const [submittedFieldAbbrStudents, setSubmittedFieldAbbrStudents] = useState<string>("");
    const [availableSpecializationsStudents, setAvailableSpecializationsStudents] = useState<Specialization[]>([]);
    const [selectedSpecializationAbbrStudents, setSelectedSpecializationAbbrStudents] = useState<string>("");
    const [submittedSpecializationAbbrStudents, setSubmittedSpecializationAbbrStudents] = useState<string>("");
    const [availableCyclesStudents, setAvailableCyclesStudents] = useState<StudyCycle[]>([]);
    const [selectedCycleNameStudents, setSelectedCycleNameStudents] = useState<string>("");
    const [submittedCycleNameStudents, setSubmittedCycleNameStudents] = useState<string>("");
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  wyszukiwanie:
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    const [searchTermThesesDeleting, setSearchTermThesesDeleting] = useState<string>('');
    const [afterSearchThesesDeleting, setAfterSearchThesesDeleting] = useState<ThesisFront[]>(theses);

    const [searchTermThesesArchive, setSearchTermThesesArchive] = useState<string>('');
    const [afterSearchThesesArchive, setAfterSearchThesesArchive] = useState<ThesisFront[]>(theses);

    const [searchTermStudents, setSearchTermStudents] = useState<string>('');
    const [afterSearchStudents, setAfterSearchStudents] = useState<Student[]>(students);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  paginacja
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //  ogólna
    const ITEMS_PER_PAGE = ['10', '25', '50', 'All'];
    const [currentITEMS_PER_PAGE, setCurrentITEMS_PER_PAGE] = useState(ITEMS_PER_PAGE);
    const [thesesDeletingLoaded, setThesesDeletingLoaded] = useState<boolean>(false);
    const [thesesArchiveLoaded, setThesesArchiveLoaded] = useState<boolean>(false);
    const [studentsLoaded, setStudentsLoaded] = useState<boolean>(false);

    //  usuwanie tematów
    const [currentPageThesesDeleting, setCurrentPageThesesDeleting] = useState(1);
    const [inputValueThesesDeleting, setInputValueThesesDeleting] = useState(currentPageThesesDeleting);
    const [thesesDeletingPerPage, setThesesDeletingPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
    const [chosenThesesDeletingPerPage, setChosenThesesDeletingPerPage] = useState(thesesDeletingPerPage);
    const indexOfLastThesesDeleting = thesesDeletingPerPage === 'All' ? afterSearchThesesDeleting.length : currentPageThesesDeleting * parseInt(thesesDeletingPerPage, 10);
    const indexOfFirstThesesDeleting = thesesDeletingPerPage === 'All' ? 0 : indexOfLastThesesDeleting - parseInt(thesesDeletingPerPage, 10);
    const totalPagesThesesDeleting = thesesDeletingPerPage === 'All' ? 1 : Math.ceil(afterSearchThesesDeleting.length / parseInt(thesesDeletingPerPage, 10));
    
    //  archiwizacja tematów
    const [currentPageThesesArchive, setCurrentPageThesesArchive] = useState(1);
    const [inputValueThesesArchive, setInputValueThesesArchive] = useState(currentPageThesesArchive);
    const [thesesArchivePerPage, setThesesArchivePerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
    const [chosenThesesArchivePerPage, setChosenThesesArchivePerPage] = useState(thesesArchivePerPage);
    const indexOfLastThesesArchive = thesesArchivePerPage === 'All' ? afterSearchThesesArchive.length : currentPageThesesArchive * parseInt(thesesArchivePerPage, 10);
    const indexOfFirstThesesArchive = thesesArchivePerPage === 'All' ? 0 : indexOfLastThesesArchive - parseInt(thesesArchivePerPage, 10);
    const totalPagesThesesArchive = thesesArchivePerPage === 'All' ? 1 : Math.ceil(afterSearchThesesArchive.length / parseInt(thesesArchivePerPage, 10));

    //  studenci
    const [currentPageStudents, setCurrentPageStudents] = useState(1);
    const [inputValueStudents, setInputValueStudents] = useState(currentPageStudents);
    const [studentsPerPage, setStudentsPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
    const [chosenStudentsPerPage, setChosenStudentsPerPage] = useState(studentsPerPage);
    const indexOfLastStudents = studentsPerPage === 'All' ? afterSearchStudents.length : currentPageStudents * parseInt(studentsPerPage, 10);
    const indexOfFirstStudents = studentsPerPage === 'All' ? 0 : indexOfLastStudents - parseInt(studentsPerPage, 10);
    const totalPagesStudents = studentsPerPage === 'All' ? 1 : Math.ceil(afterSearchStudents.length / parseInt(studentsPerPage, 10));

    const currentStudents = afterSearchStudents.slice(indexOfFirstStudents, indexOfLastStudents);
    const currentThesesDeleting = afterSearchThesesDeleting.slice(indexOfFirstThesesDeleting, indexOfLastThesesDeleting);
    const currentThesesArchive = afterSearchThesesArchive.slice(indexOfFirstThesesArchive, indexOfLastThesesArchive);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //  checkboxy - usuwanie tematów
    const [thesesDeletingFormIndexes, setThesesDeletingFormIndexes] = useState(new Set<number>());
    const [checkAllCheckboxThesesDeleting, setCheckAllCheckboxThesesDeleting] = useState(false);

    const [confirmClickedThesesDeleting, setConfirmClickedThesesDeleting] = useState(false);
    const [showDeleteConfirmationThesesDeleting, setShowDeleteConfirmationThesesDeleting] = useState(false);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  checkboxy - archiwizacja tematów
    const [thesesArchiveFormIndexes, setThesesArchiveFormIndexes] = useState(new Set<number>());
    const [checkAllCheckboxThesesArchive, setCheckAllCheckboxThesesArchive] = useState(false);
    
    const [confirmClickedThesesArchive, setConfirmClickedThesesArchive] = useState(false);
    const [showDeleteConfirmationThesesArchive, setShowDeleteConfirmationThesesArchive] = useState(false);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //  checkboxy - students
    const [studentsFormIndexes, setStudentsFormIndexes] = useState(new Set<number>());
    const [checkAllCheckboxStudents, setCheckAllCheckboxStudents] = useState(false);

    const [confirmClickedStudents, setConfirmClickedStudents] = useState(false);
    const [showAcceptConfirmationStudents, setShowAcceptConfirmationStudents] = useState(false);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  podstawa - główne dane do tabel:
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    useEffect(() => {
        //  theses:
        api.get(api_access + 'thesis/status/exclude/Draft')
            .then((response) => {
                const thesesResponse: ThesisFront[] = response.data.map((thesisDb: Thesis) => {
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
                const closed = thesesResponse.filter(t => t.status.name === 'Closed');
                const notClosed = thesesResponse.filter(t => t.status.name !== 'Closed' && t.status.name !== 'Draft');

                setTheses(notClosed);
                setClosedTheses(closed);
                
                setFilteredThesesDeleting(closed);
                setAfterSearchThesesDeleting(closed);
                setThesesDeletingLoaded(true);
                
                setFilteredThesesArchive(notClosed);
                setAfterSearchThesesArchive(notClosed);
                setThesesArchiveLoaded(true);
            })
            .catch((error) => {
                if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });

            //  students:
            api.get(api_access + 'student')
            .then((response) => {
                const studentResponse = response.data.map((studentDb: Student) => {
                    const stud: Student = {
                        id: studentDb.id,
                        mail: studentDb.mail,
                        name: studentDb.name,
                        surname: studentDb.surname,
                        index: studentDb.index,
                        status: studentDb.status,
                        role: studentDb.role,
                        studentProgramCycles: studentDb.studentProgramCycles
                    }
                    return stud;
                });
                setStudents(studentResponse);
                setFilteredStudents(studentResponse);
                setAfterSearchStudents(studentResponse);
                setStudentsLoaded(true);
            })
            .catch((error) => {
                if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });
    }, [key]);

    //  dane pomocnicze:
    useEffect(() => {

        //  pracownicy:
        api.get(api_access + 'employee')
        .then((response) => {
            const supervisors = response.data
                .filter((employee: Employee) => employee.roles.some((role: Role) => role.name === 'supervisor'))
                .sort((a: Employee, b: Employee) => a.surname.localeCompare(b.surname));
            setAvailableSupervisorThesesDeleting(supervisors);
            setAvailableSupervisorThesesArchive(supervisors);
        })
        .catch((error) => {
            if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
            }
        });

        //  cykle nauczania:
        api.get(api_access + 'studycycle')
            .then((response) => {
                const sortedCycles = response.data.sort((a: StudyCycle, b: StudyCycle) => {
                return a.name.localeCompare(b.name);
            });
            setAvailableCyclesThesesDeleting(sortedCycles);
            setAvailableCyclesThesesArchive(sortedCycles);
            setAvailableCyclesStudents(sortedCycles);
            })
            .catch((error) => {
                if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });

        //  wydziały:
        api.get(api_access + 'faculty')
            .then((response) => {
              const sortedFaculties = response.data.sort((a: Faculty, b: Faculty) => {
                return a.name.localeCompare(b.name);
              });
              setAvailableFacultiesThesesDeleting(sortedFaculties);
              setAvailableFacultiesThesesArchive(sortedFaculties);
              setAvailableFacultiesStudents(sortedFaculties);
            })
            .catch((error) => {
              if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
            });

        //  kierunki:
        api.get(api_access + 'studyfield')
            .then((response) => {
                const sortedStudyFields = response.data.sort((a: StudyField, b: StudyField) => {
                    return a.name.localeCompare(b.name);
                });
                setAvailableFieldsThesesDeleting(sortedStudyFields);
                setAvailableFieldsThesesArchive(sortedStudyFields);
                setAvailableFieldsStudents(sortedStudyFields);
            })
            .catch((error) => {
                if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });

        //  specjalizacje
        api.get(api_access + 'specialization')
            .then((response) => {
                const sortedSpecializations = response.data.sort((a: Specialization, b: Specialization) => {
                    return a.name.localeCompare(b.name);
                });
                setAvailableSpecializationsThesesDeleting(sortedSpecializations);
                setAvailableSpecializationsThesesArchive(sortedSpecializations);
                setAvailableSpecializationsStudents(sortedSpecializations);
            })
            .catch((error) => {
                if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });

    },[]);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  filtrowanie usuwania tematów:
    useEffect(() => {
        if (thesesDeletingLoaded && selectedToClear === ClearingMode.DELETE_THESES)
          handleFiltrationThesesDeleting(false);
    }, [thesesDeletingLoaded, selectedToClear]);

    const handleSubmitFiltersThesesDeleting = (toogle: boolean) => {

        setSubmittedFacultyAbbrThesesDeleting(selectedFacultyAbbrThesesDeleting)
        setSubmittedFieldAbbrThesesDeleting(selectedFieldAbbrThesesDeleting)
        setSubmittedSpecializationAbbrThesesDeleting(selectedSpecializationAbbrThesesDeleting)
        setSubmittedCycleNameThesesDeleting(selectedCycleNameThesesDeleting)
        setSubmittedSupervisorsThesesDeleting(selectedSupervisorsThesesDeleting)
        localStorage.setItem('thesesDeletingFilterFaculty', selectedFacultyAbbrThesesDeleting);
        localStorage.setItem('thesesDeletingFilterField', selectedFieldAbbrThesesDeleting);
        localStorage.setItem('thesesDeletingFilterSpecialization', selectedSpecializationAbbrThesesDeleting);
        localStorage.setItem('thesesDeletingFilterCycle', selectedCycleNameThesesDeleting);
        localStorage.setItem('thesesDeletingFilterSupervisors', JSON.stringify(selectedSupervisorsThesesDeleting));

        if (toogle){
            handleToggleSidebarThesesDeleting()
        }
    };

    const handleToggleSidebarThesesDeleting = () => {

        if (!sidebarOpen && selectedToClear === ClearingMode.DELETE_THESES) {
            setSelectedFacultyAbbrThesesDeleting(submittedFacultyAbbrThesesDeleting)
            setSelectedFieldAbbrThesesDeleting(submittedFieldAbbrThesesDeleting)
            setSelectedSpecializationAbbrThesesDeleting(submittedSpecializationAbbrThesesDeleting)
            setSelectedCycleNameThesesDeleting(submittedCycleNameThesesDeleting)
            setSelectedSupervisorsThesesDeleting(submittedSupervisorsThesesDeleting)
        }
        if (selectedToClear === ClearingMode.DELETE_THESES){
            setSidebarOpen(!sidebarOpen);
        }
    };

    const handleDeleteFiltersThesesDeleting = () => {
        setSelectedCycleNameThesesDeleting("");
        setSelectedFacultyAbbrThesesDeleting("");
        setSelectedFieldAbbrThesesDeleting("");
        setSelectedSpecializationAbbrThesesDeleting("");
        setSelectedSupervisorsThesesDeleting([]);
    
        localStorage.removeItem('thesesDeletingFilterFaculty');
        localStorage.removeItem('thesesDeletingFilterField');
        localStorage.removeItem('thesesDeletingFilterSpecialization');
        localStorage.removeItem('thesesDeletingFilterCycle');
        localStorage.removeItem('thesesDeletingFilterSupervisors');
    
        setSubmittedCycleNameThesesDeleting("");
        setSubmittedFacultyAbbrThesesDeleting("");
        setSubmittedFieldAbbrThesesDeleting("");
        setSubmittedSpecializationAbbrThesesDeleting("");
        setSubmittedSupervisorsThesesDeleting([]);
    
        setFilteredThesesDeleting(closedTheses);
    };

    const handleFiltrationThesesDeleting = (toggle: boolean) => {

        if (toggle) {
          handleSubmitFiltersThesesDeleting(true)
    
          const facultyFilter = selectedFacultyAbbrThesesDeleting ? (thesis: ThesisFront) => thesis.programs.some(p => p.faculty.abbreviation === selectedFacultyAbbrThesesDeleting) : () => true;
          const fieldFilter = selectedFieldAbbrThesesDeleting ? (thesis: ThesisFront) => thesis.programs.some(p => p.studyField ? p.studyField.abbreviation === selectedFieldAbbrThesesDeleting : p.specialization.studyField.abbreviation === selectedFieldAbbrThesesDeleting) : () => true;
          const specializationFilter = selectedSpecializationAbbrThesesDeleting ? (thesis: ThesisFront) => thesis.programs.some(p => p.specialization ? p.specialization.abbreviation === selectedSpecializationAbbrThesesDeleting : false) : () => true;
          const cycleFilter = selectedCycleNameThesesDeleting ? (thesis: ThesisFront) => thesis.studyCycle?.name === selectedCycleNameThesesDeleting : () => true;
          const supervisorFilter = selectedSupervisorsThesesDeleting.length ? (thesis: ThesisFront) => selectedSupervisorsThesesDeleting.includes(thesis.supervisor.id) : () => true;
    
          const newFilteredTheses = closedTheses.filter(thesis =>
            facultyFilter(thesis) &&
            fieldFilter(thesis) &&
            specializationFilter(thesis) &&
            cycleFilter(thesis) &&
            supervisorFilter(thesis)
          );
          setFilteredThesesDeleting(newFilteredTheses);
        }
        else {
          const savedFacultyAbbr = localStorage.getItem('thesesDeletingFilterFaculty') || '';
          const savedFieldAbbr = localStorage.getItem('thesesDeletingFilterField') || '';
          const savedSpecializationAbbr = localStorage.getItem('thesesDeletingFilterSpecialization') || '';
          const savedCycleName = localStorage.getItem('thesesDeletingFilterCycle') || '';
          const savedsupervisors = JSON.parse(localStorage.getItem('thesesDeletingFilterSupervisors') || '[]');
    
          setSubmittedFacultyAbbrThesesDeleting(savedFacultyAbbr)
          setSubmittedFieldAbbrThesesDeleting(savedFieldAbbr)
          setSubmittedSpecializationAbbrThesesDeleting(savedSpecializationAbbr)
          setSubmittedCycleNameThesesDeleting(savedCycleName)
          setSubmittedSupervisorsThesesDeleting(savedsupervisors)
    
          const facultyFilter = savedFacultyAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.faculty.abbreviation === savedFacultyAbbr) : () => true;
          const fieldFilter = savedFieldAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.studyField ? p.studyField.abbreviation === savedFieldAbbr : p.specialization.studyField.abbreviation === selectedFieldAbbrThesesDeleting) : () => true;
          const specializationFilter = savedSpecializationAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.specialization ? p.specialization.abbreviation === savedSpecializationAbbr : false) : () => true;
          const cycleFilter = savedCycleName ? (thesis: ThesisFront) => thesis.studyCycle?.name === savedCycleName : () => true;
          const supervisorFilter = savedsupervisors.length ? (thesis: ThesisFront) => savedsupervisors.includes(thesis.supervisor.id) : () => true;
    
          const newFilteredTheses = closedTheses.filter(thesis =>
            facultyFilter(thesis) &&
            fieldFilter(thesis) &&
            specializationFilter(thesis) &&
            cycleFilter(thesis) &&
            supervisorFilter(thesis)
          );
          setFilteredThesesDeleting(newFilteredTheses);
        }
    }

    const allowFilteringThesesDeleting = () => {
        if (selectedToClear === ClearingMode.DELETE_THESES && (
            selectedFacultyAbbrThesesDeleting ||
            submittedFieldAbbrThesesDeleting ||
            submittedSpecializationAbbrThesesDeleting ||
            submittedCycleNameThesesDeleting ||
            submittedSupervisorsThesesDeleting.length > 0
        )){
            return true
        }
        return false
    }
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

//  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  filtrowanie archiwizowania tematów:
    useEffect(() => {
        if (thesesArchiveLoaded && selectedToClear === ClearingMode.ARCHIVE_THESES)
          handleFiltrationThesesArchive(false);
    }, [thesesArchiveLoaded, selectedToClear]);

    const handleSubmitFiltersThesesArchive = (toogle: boolean) => {

        setSubmittedFacultyAbbrThesesArchive(selectedFacultyAbbrThesesArchive)
        setSubmittedFieldAbbrThesesArchive(selectedFieldAbbrThesesArchive)
        setSubmittedSpecializationAbbrThesesArchive(selectedSpecializationAbbrThesesArchive)
        setSubmittedCycleNameThesesArchive(selectedCycleNameThesesArchive)
        setSubmittedSupervisorsThesesArchive(selectedSupervisorsThesesArchive)
        localStorage.setItem('thesesArchiveFilterFaculty', selectedFacultyAbbrThesesArchive);
        localStorage.setItem('thesesArchiveFilterField', selectedFieldAbbrThesesArchive);
        localStorage.setItem('thesesArchiveFilterSpecialization', selectedSpecializationAbbrThesesArchive);
        localStorage.setItem('thesesArchiveFilterCycle', selectedCycleNameThesesArchive);
        localStorage.setItem('thesesArchiveFilterSupervisors', JSON.stringify(selectedSupervisorsThesesArchive));
        localStorage.setItem('thesesArchiveFilterStatuses', JSON.stringify(selectedStatusesNameThesesArchive));

        if (toogle){
            handleToggleSidebarThesesArchive()
        }
    };

    const handleToggleSidebarThesesArchive = () => {
        if (!sidebarOpen && selectedToClear === ClearingMode.ARCHIVE_THESES) {
            setSelectedFacultyAbbrThesesArchive(submittedFacultyAbbrThesesArchive)
            setSelectedFieldAbbrThesesArchive(submittedFieldAbbrThesesArchive)
            setSelectedSpecializationAbbrThesesArchive(submittedSpecializationAbbrThesesArchive)
            setSelectedCycleNameThesesArchive(submittedCycleNameThesesArchive)
            setSelectedSupervisorsThesesArchive(submittedSupervisorsThesesArchive)
            setSelectedStatusesNameThesesArchive(submittedStatusesNameThesesArchive);
        }
        if (selectedToClear === ClearingMode.ARCHIVE_THESES){
            setSidebarOpen(!sidebarOpen);
        }
    };

    const handleDeleteFiltersThesesArchive = () => {
        setSelectedCycleNameThesesArchive("");
        setSelectedFacultyAbbrThesesArchive("");
        setSelectedFieldAbbrThesesArchive("");
        setSelectedSpecializationAbbrThesesArchive("");
        setSelectedSupervisorsThesesArchive([]);
        setSelectedStatusesNameThesesArchive("");
    
        localStorage.removeItem('thesesArchiveFilterFaculty');
        localStorage.removeItem('thesesArchiveFilterField');
        localStorage.removeItem('thesesArchiveFilterSpecialization');
        localStorage.removeItem('thesesArchiveFilterCycle');
        localStorage.removeItem('thesesArchiveFilterSupervisors');
        localStorage.removeItem('thesesArchiveFilterStatuses');
    
        setSubmittedCycleNameThesesArchive("");
        setSubmittedFacultyAbbrThesesArchive("");
        setSubmittedFieldAbbrThesesArchive("");
        setSubmittedSpecializationAbbrThesesArchive("");
        setSubmittedSupervisorsThesesArchive([]);
        setSubmittedStatusesNameThesesArchive("");
    
        setFilteredThesesArchive(theses);
    };

    const handleFiltrationThesesArchive = (toggle: boolean) => {

        if (toggle) {
          handleSubmitFiltersThesesArchive(true)
    
          const facultyFilter = selectedFacultyAbbrThesesArchive ? (thesis: ThesisFront) => thesis.programs.some(p => p.faculty.abbreviation === selectedFacultyAbbrThesesArchive) : () => true;
          const fieldFilter = selectedFieldAbbrThesesArchive ? (thesis: ThesisFront) => thesis.programs.some(p => p.studyField ? p.studyField.abbreviation === selectedFieldAbbrThesesArchive : p.specialization.studyField.abbreviation === selectedFieldAbbrThesesArchive) : () => true;
          const specializationFilter = selectedSpecializationAbbrThesesArchive ? (thesis: ThesisFront) => thesis.programs.some(p => p.specialization ? p.specialization.abbreviation === selectedSpecializationAbbrThesesArchive : false) : () => true;
          const cycleFilter = selectedCycleNameThesesArchive ? (thesis: ThesisFront) => thesis.studyCycle?.name === selectedCycleNameThesesArchive : () => true;
          const supervisorFilter = selectedSupervisorsThesesArchive.length ? (thesis: ThesisFront) => selectedSupervisorsThesesArchive.includes(thesis.supervisor.id) : () => true;
          const statusFilter = selectedStatusesNameThesesArchive ? (thesis: ThesisFront) => thesis.status.name === selectedStatusesNameThesesArchive : () => true;

          const newFilteredTheses = theses.filter(thesis =>
            facultyFilter(thesis) &&
            fieldFilter(thesis) &&
            specializationFilter(thesis) &&
            cycleFilter(thesis) &&
            supervisorFilter(thesis) &&
            statusFilter(thesis)
          );
          setFilteredThesesArchive(newFilteredTheses);
        }
        else {
          const savedFacultyAbbr = localStorage.getItem('thesesArchiveFilterFaculty') || '';
          const savedFieldAbbr = localStorage.getItem('thesesArchiveFilterField') || '';
          const savedSpecializationAbbr = localStorage.getItem('thesesArchiveFilterSpecialization') || '';
          const savedCycleName = localStorage.getItem('thesesArchiveFilterCycle') || '';
          const savedSupervisors = JSON.parse(localStorage.getItem('thesesArchiveFilterSupervisors') || '[]');
          const savedStatusName = localStorage.getItem('thesesArchiveFilterStatuses') || '';

          setSubmittedFacultyAbbrThesesArchive(savedFacultyAbbr);
          setSubmittedFieldAbbrThesesArchive(savedFieldAbbr);
          setSubmittedSpecializationAbbrThesesArchive(savedSpecializationAbbr);
          setSubmittedCycleNameThesesArchive(savedCycleName);
          setSubmittedSupervisorsThesesArchive(savedSupervisors);
          setSubmittedStatusesNameThesesArchive(savedStatusName);
    
          const facultyFilter = savedFacultyAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.faculty.abbreviation === savedFacultyAbbr) : () => true;
          const fieldFilter = savedFieldAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.studyField ? p.studyField.abbreviation === savedFieldAbbr : p.specialization.studyField.abbreviation === selectedFieldAbbrThesesArchive) : () => true;
          const specializationFilter = savedSpecializationAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.specialization ? p.specialization.abbreviation === savedSpecializationAbbr : false) : () => true;
          const cycleFilter = savedCycleName ? (thesis: ThesisFront) => thesis.studyCycle?.name === savedCycleName : () => true;
          const supervisorFilter = savedSupervisors.length ? (thesis: ThesisFront) => savedSupervisors.includes(thesis.supervisor.id) : () => true;
          const statusFilter = selectedStatusesNameThesesArchive ? (thesis: ThesisFront) => thesis.status.name === selectedStatusesNameThesesArchive : () => true;

          const newFilteredTheses = theses.filter(thesis =>
            facultyFilter(thesis) &&
            fieldFilter(thesis) &&
            specializationFilter(thesis) &&
            cycleFilter(thesis) &&
            supervisorFilter(thesis) &&
            statusFilter(thesis)
          );
          setFilteredThesesArchive(newFilteredTheses);
        }
    }

    const allowFilteringThesesArchive = () => {
        if (selectedToClear === ClearingMode.ARCHIVE_THESES && (
            selectedFacultyAbbrThesesArchive ||
            submittedFieldAbbrThesesArchive ||
            submittedSpecializationAbbrThesesArchive ||
            submittedCycleNameThesesArchive ||
            submittedSupervisorsThesesArchive.length > 0 ||
            submittedStatusesNameThesesArchive
        )){
            return true
        }
        return false
    }
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  filtrowanie studentów:

    useEffect(() => {
        if (studentsLoaded && selectedToClear === ClearingMode.STUDENTS)
          handleFiltrationStudents(false);
    }, [studentsLoaded, selectedToClear]);

    const handleSubmitFiltersStudents = (toogle: boolean) => {

        setSubmittedFacultyAbbrStudents(selectedFacultyAbbrStudents)
        setSubmittedFieldAbbrStudents(selectedFieldAbbrStudents)
        setSubmittedSpecializationAbbrStudents(selectedSpecializationAbbrStudents)

        setSubmittedCycleNameStudents(selectedCycleNameStudents);

        localStorage.setItem('studentFilterFaculty', selectedFacultyAbbrStudents);
        localStorage.setItem('studentFilterField', selectedFieldAbbrStudents);
        localStorage.setItem('studentFilterSpecialization', selectedSpecializationAbbrStudents);

        localStorage.setItem('studentFilterCycle', selectedCycleNameStudents);
    
        if (toogle)
          handleToggleSidebarStudents()
      };
    
      const handleToggleSidebarStudents = () => {
    
        if (!sidebarOpen) {
          setSelectedFacultyAbbrStudents(submittedFacultyAbbrStudents)
          setSelectedFieldAbbrStudents(submittedFieldAbbrStudents)
          setSelectedSpecializationAbbrStudents(submittedSpecializationAbbrStudents)

          setSelectedCycleNameStudents(submittedCycleNameStudents);
        }
        setSidebarOpen(!sidebarOpen);
      };
    
      const handleDeleteFiltersStudents = () => {
    
        setSelectedFacultyAbbrStudents("");
        setSelectedFieldAbbrStudents("");
        setSelectedSpecializationAbbrStudents("");

        setSelectedCycleNameStudents("");
    
        localStorage.removeItem('studentFilterFaculty');
        localStorage.removeItem('studentFilterField');
        localStorage.removeItem('studentFilterSpecialization');

        localStorage.removeItem('studentFilterCycle');
    
        setSubmittedFacultyAbbrStudents("");
        setSubmittedFieldAbbrStudents("");
        setSubmittedSpecializationAbbrStudents("");

        setSubmittedCycleNameStudents("");
    
        setFilteredStudents(students);
      };
    
      const handleFiltrationStudents = (toggle: boolean) => {
    
        if (toggle) {
          handleSubmitFiltersStudents(true)
    
          const facultyFilter = selectedFacultyAbbrStudents ? (student: Student) => student.studentProgramCycles.some(sp => sp.program.faculty.abbreviation === selectedFacultyAbbrStudents) : () => true;
          const fieldFilter = selectedFieldAbbrStudents ? (student: Student) => student.studentProgramCycles.some(sp => sp.program.studyField ? sp.program.studyField.abbreviation === selectedFieldAbbrStudents : sp.program.specialization.studyField.abbreviation === selectedFieldAbbrStudents) : () => true;
          const specializationFilter = selectedSpecializationAbbrStudents ? (student: Student) => student.studentProgramCycles.some(sp => sp.program.specialization ? sp.program.specialization.abbreviation === selectedSpecializationAbbrStudents : false) : () => true;

          const cycleFilter = selectedCycleNameStudents ? (stud: Student) => stud.studentProgramCycles.some(spc => spc.cycle ? spc.cycle.name === selectedCycleNameStudents : false) : () => true;

          const newFilteredStudents = students.filter(student =>
            facultyFilter(student) &&
            fieldFilter(student) &&
            specializationFilter(student) &&
            cycleFilter(student)
          );
          setFilteredStudents(newFilteredStudents);
        }
        else {
          const savedFacultyAbbr = localStorage.getItem('studentFilterFaculty') || '';
          const savedFieldAbbr = localStorage.getItem('studentFilterField') || '';
          const savedSpecializationAbbr = localStorage.getItem('studentFilterSpecialization') || '';

          const savedCycleName = localStorage.getItem('studentFilterCycle') || '';
    
          setSubmittedFacultyAbbrStudents(savedFacultyAbbr);
          setSubmittedFieldAbbrStudents(savedFieldAbbr);
          setSubmittedSpecializationAbbrStudents(savedSpecializationAbbr);

          setSubmittedCycleNameStudents(savedCycleName);
    
          const facultyFilter = savedFacultyAbbr ? (student: Student) => student.studentProgramCycles.some(sp => sp.program.faculty.abbreviation === savedFacultyAbbr) : () => true;
          const fieldFilter = savedFieldAbbr ? (student: Student) => student.studentProgramCycles.some(sp => sp.program.studyField ? sp.program.studyField.abbreviation === savedFieldAbbr : sp.program.specialization.studyField.abbreviation === selectedFieldAbbrStudents) : () => true;
          const specializationFilter = savedSpecializationAbbr ? (student: Student) => student.studentProgramCycles.some(sp => sp.program.specialization ? sp.program.specialization.abbreviation === savedSpecializationAbbr : false) : () => true;

          const cycleFilter = selectedCycleNameStudents ? (stud: Student) => stud.studentProgramCycles.some(spc => spc.cycle ? spc.cycle.name === selectedCycleNameStudents : false) : () => true;

          const newFilteredStudents = students.filter(student =>
            facultyFilter(student) &&
            fieldFilter(student) &&
            specializationFilter(student) &&
            cycleFilter(student)
          );
          setFilteredStudents(newFilteredStudents);
        }
      }

      const allowFilteringStudents = () => {
        if (selectedToClear === ClearingMode.STUDENTS && (
            submittedCycleNameStudents ||
            submittedFacultyAbbrStudents ||
            submittedFieldAbbrStudents ||
            submittedSpecializationAbbrStudents
        )){
            return true
        }
        return false
    }
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  wyszukiwanie:

    //  usuwanie tematów
    useEffect(() => {
        const searchText = searchTermThesesDeleting.toLowerCase();
        const filteredList = filteredThesesDeleting.filter((thesis) => {
          return (
            thesis.namePL.toLowerCase().includes(searchText) ||
            thesis.nameEN.toLowerCase().includes(searchText) ||
            (thesis.supervisor.title.name + ' ' + thesis.supervisor.name + ' ' + thesis.supervisor.surname).toLowerCase().includes(searchText)
          );
        });
        setAfterSearchThesesDeleting(() => filteredList);
    
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
    
        handlePageChangeThesesDeleting(1);
        setThesesDeletingPerPage((filteredItemsPerPage.includes(chosenThesesDeletingPerPage)) ? chosenThesesDeletingPerPage : ((filteredItemsPerPage.length > 1) ? filteredItemsPerPage[1] : filteredItemsPerPage[0]));
    
      }, [searchTermThesesDeleting, filteredThesesDeleting]);

      //    archiwizowanie tematów:
      useEffect(() => {
        const searchText = searchTermThesesArchive.toLowerCase();
        const filteredList = filteredThesesArchive.filter((thesis) => {
          return (
            thesis.namePL.toLowerCase().includes(searchText) ||
            thesis.nameEN.toLowerCase().includes(searchText) ||
            (thesis.supervisor.title.name + ' ' + thesis.supervisor.name + ' ' + thesis.supervisor.surname).toLowerCase().includes(searchText)
          );
        });
        setAfterSearchThesesArchive(() => filteredList);
    
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
    
        handlePageChangeThesesArchive(1);
        setThesesArchivePerPage((filteredItemsPerPage.includes(chosenThesesArchivePerPage)) ? chosenThesesArchivePerPage : ((filteredItemsPerPage.length > 1) ? filteredItemsPerPage[1] : filteredItemsPerPage[0]));
    
      }, [searchTermThesesArchive, filteredThesesArchive]);

      //    usuwanie studentów
      useEffect(() => {
        const searchText = searchTermStudents.toLowerCase();
        const filteredList = filteredStudents.filter((stud) => {
            return (
                stud.index.toLowerCase().includes(searchText) ||
                stud.name.toLowerCase().includes(searchText) ||
                stud.surname.toLowerCase().includes(searchText)
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
    
        handlePageChangeStudents(1);
        setStudentsPerPage((filteredItemsPerPage.includes(chosenStudentsPerPage)) ? chosenStudentsPerPage : ((filteredItemsPerPage.length > 1) ? filteredItemsPerPage[1] : filteredItemsPerPage[0]));
    
      }, [searchTermStudents, filteredStudents]);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  paginacja:

    //  usuwanie tematów
    const handlePageChangeThesesDeleting = (newPage: number) => {
        if (!newPage || newPage < 1) {
          setCurrentPageThesesDeleting(1);
          setInputValueThesesDeleting(1);
        }
        else {
          if (newPage > totalPagesThesesDeleting) {
            setCurrentPageThesesDeleting(totalPagesThesesDeleting);
            setInputValueThesesDeleting(totalPagesThesesDeleting);
          }
          else {
            setCurrentPageThesesDeleting(newPage);
            setInputValueThesesDeleting(newPage);
          }
        }
      };

    //  archiwizacja tematów
    const handlePageChangeThesesArchive = (newPage: number) => {
        if (!newPage || newPage < 1) {
          setCurrentPageThesesArchive(1);
          setInputValueThesesArchive(1);
        }
        else {
          if (newPage > totalPagesThesesArchive) {
            setCurrentPageThesesArchive(totalPagesThesesArchive);
            setInputValueThesesArchive(totalPagesThesesArchive);
          }
          else {
            setCurrentPageThesesArchive(newPage);
            setInputValueThesesArchive(newPage);
          }
        }
      };

    //  usuwanie studentów
    const handlePageChangeStudents = (newPage: number) => {
        if (!newPage || newPage < 1) {
          setCurrentPageStudents(1);
          setInputValueStudents(1);
        }
        else {
          if (newPage > totalPagesStudents) {
            setCurrentPageStudents(totalPagesStudents);
            setInputValueStudents(totalPagesStudents);
          }
          else {
            setCurrentPageStudents(newPage);
            setInputValueStudents(newPage);
          }
        }
      };
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  checkboxy - usuwanie tematów
    const checkCheckboxThesesDeleting = (thesisId: number) => {
        setThesesDeletingFormIndexes((prev) => {
          const newSet = new Set(prev);
          newSet.add(thesisId);
          return newSet;
        });
      }
    
      const uncheckCheckboxThesesDeleting = (thesisId: number) => {
        setThesesDeletingFormIndexes((prev) => {
            const newSet = new Set(prev);
            newSet.delete(thesisId);
            return newSet;
          });
      }
    
      const checkAllCheckboxesChangeThesesDeleting = () => {
        setCheckAllCheckboxThesesDeleting(!checkAllCheckboxThesesDeleting);
        if (checkAllCheckboxThesesDeleting){
          setThesesDeletingFormIndexes(new Set());
        }
        else{
          let thesisIds = new Set<number>();
          afterSearchThesesDeleting.map((thesis, _) => {
            thesisIds.add(thesis.id)
          });
          setThesesDeletingFormIndexes(thesisIds);
        }
      }
    
      const handleConfirmClickThesesDeleting = () => {
        setShowDeleteConfirmationThesesDeleting(true);
        setConfirmClickedThesesDeleting(true);
      };
    
      const handleConfirmAcceptThesesDeleting = () => {
        const isValid = validateThesesDeleting();
        if (isValid){
          api.put(api_access + 'thesis/bulk', Array.from(thesesDeletingFormIndexes))
            .then(() => {
              setKey(k => k+1);
              setThesesDeletingFormIndexes(new Set());
              toast.success(t("thesis.deleteSuccesfulBulk"));
            })
            .catch((error) => {
              if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("thesis.deleteErrorBulk"));
            });
        }
        else{
          toast.error(t("thesis.deleteErrorBulk")); 
        }
        setShowDeleteConfirmationThesesDeleting(false);
        handleDeleteFiltersThesesDeleting();
      }
    
      const handleConfirmCancelThesesDeleting = () => {
        setShowDeleteConfirmationThesesDeleting(false);
      }
    
      const validateThesesDeleting = () => {
        let allIdsArePresent = true;
        const indexes = closedTheses.map(t => t.id);
        for (var index of Array.from(thesesDeletingFormIndexes.values())) {
          if (!indexes.includes(index)){
            allIdsArePresent = false;
            break;
          }
        }
        return allIdsArePresent !== false;
      }

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  checkboxy - usuwanie tematów
    const checkCheckboxThesesArchive = (thesisId: number) => {
        setThesesArchiveFormIndexes((prev) => {
          const newSet = new Set(prev);
          newSet.add(thesisId);
          return newSet;
        });
      }
    
      const uncheckCheckboxThesesArchive = (thesisId: number) => {
        setThesesArchiveFormIndexes((prev) => {
            const newSet = new Set(prev);
            newSet.delete(thesisId);
            return newSet;
          });
      }
    
      const checkAllCheckboxesChangeThesesArchive = () => {
        setCheckAllCheckboxThesesArchive(!checkAllCheckboxThesesArchive);
        if (checkAllCheckboxThesesArchive){
          setThesesArchiveFormIndexes(new Set());
        }
        else{
          let thesisIds = new Set<number>();
          afterSearchThesesArchive.map((thesis, _) => {
            thesisIds.add(thesis.id)
          });
          setThesesArchiveFormIndexes(thesisIds);
        }
      }
    
      const handleConfirmClickThesesArchive = () => {
        setShowDeleteConfirmationThesesArchive(true);
        setConfirmClickedThesesArchive(true);
      };
    
      const handleConfirmAcceptThesesArchive = () => {
        const [isValid, statName] = validateThesesArchive();
        if (isValid){
          api.put(api_access + `thesis/bulk/${statName}`, Array.from(thesesArchiveFormIndexes))
            .then(() => {
              setKey(k => k+1);
              setThesesArchiveFormIndexes(new Set());
              toast.success(t("thesis.archiveSuccesfulBulk"));
            })
            .catch((error) => {
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("thesis.archiveErrorBulk"));
            });
        }
        else{
          toast.error(t("thesis.archiveErrorBulk")); 
        }
        handleDeleteFiltersThesesArchive();
        setShowDeleteConfirmationThesesArchive(false);
      }
    
      const handleConfirmCancelThesesArchive = () => {
        setShowDeleteConfirmationThesesArchive(false);
      }
    
      const validateThesesArchive = () => {
        let allIdsArePresent = true;
        const indexes = theses.map(t => t.id);
        for (var index of Array.from(thesesArchiveFormIndexes.values())) {
          if (!indexes.includes(index)){
            allIdsArePresent = false;
            break;
          }
        }
        return [(allIdsArePresent !== false), "Closed"];
      }

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  checkboxy - studenci

    //  czyść zaznaczone checkboxy na zmianie cyklu
    useEffect(() => {
        setStudentsFormIndexes(new Set<number>());
    }, [submittedCycleNameStudents]);

    const checkCheckboxStudents = (studentId: number) => {
        setStudentsFormIndexes((prev) => {
          const newSet = new Set(prev);
          newSet.add(studentId);
          return newSet;
        });
      }
    
      const uncheckCheckboxStudents = (studentId: number) => {
        setStudentsFormIndexes((prev) => {
            const newSet = new Set(prev);
            newSet.delete(studentId);
            return newSet;
          });
      }
    
      const checkAllCheckboxesChangeStudents = () => {
        setCheckAllCheckboxStudents(!checkAllCheckboxStudents);
        if (checkAllCheckboxStudents){
          setStudentsFormIndexes(new Set());
        }
        else{
          let studentIds = new Set<number>();
          afterSearchStudents.map((stud, _) => {
            studentIds.add(stud.id)
          })
          setStudentsFormIndexes(studentIds);
        }
        
      }
    
      const handleConfirmClickStudents = () => {
        setShowAcceptConfirmationStudents(true);
        setConfirmClickedStudents(true);
      };
    
      const handleConfirmAcceptStudents = () => {
        const [isValid, cycleId] = validateStudents();
        if (isValid){
          api.put(api_access + `student/bulk/cycle/${cycleId}`, Array.from(studentsFormIndexes))
            .then(() => {
              setKey(k => k+1);
              setStudentsFormIndexes(new Set());
              toast.success(t("student.deleteSuccesfulBulk"));
            })
            .catch((error) => {
              if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("student.deleteErrorBulk"));
            });
        }
        else{
          toast.error(t("student.deleteErrorBulk")); 
        }
        handleDeleteFiltersStudents();
        setShowAcceptConfirmationStudents(false);
      }
    
      const handleConfirmCancelStudents = () => {
        setShowAcceptConfirmationStudents(false);
      }
    
      const validateStudents = () => {
        const name = selectedCycleNameStudents;
        const id = availableCyclesStudents.find(cyc => cyc.name === name)?.id;
        
        let allIdsArePresent = true;
        const indexes = students.map(t => t.id);
        for (var index of Array.from(studentsFormIndexes.values())) {
          if (!indexes.includes(index)){
            allIdsArePresent = false;
            break;
          }
        }
        
        const isValid = (id !== undefined) && (allIdsArePresent !== false);
        return [isValid, id];
      }

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    const resetCheckboxesToDefault = () => {
        setThesesDeletingFormIndexes(new Set<number>());
        setCheckAllCheckboxThesesDeleting(false);
        setConfirmClickedThesesDeleting(false);
        setShowDeleteConfirmationThesesDeleting(false);

        setThesesArchiveFormIndexes(new Set<number>());
        setCheckAllCheckboxThesesArchive(false);
        setConfirmClickedThesesArchive(false);
        setShowDeleteConfirmationThesesArchive(false);

        setStudentsFormIndexes(new Set<number>());
        setCheckAllCheckboxStudents(false);
        setConfirmClickedStudents(false);
        setShowAcceptConfirmationStudents(false)
    }

    const chooseThesesDeleting = () => {
        if (selectedToClear !== ClearingMode.DELETE_THESES){
            setSearchTermStudents('');
            setSearchTermThesesDeleting('');
            setSearchTermThesesArchive('');

            resetCheckboxesToDefault();          

            handleDeleteFiltersStudents();
            handleDeleteFiltersThesesDeleting();
            handleDeleteFiltersThesesArchive();
            
            setSelectedToClear(ClearingMode.DELETE_THESES);
            setSidebarOpen(false);
        }
    }

    const chooseThesesArchive = () => {
        if (selectedToClear !== ClearingMode.ARCHIVE_THESES){
            setSearchTermStudents('');
            setSearchTermThesesDeleting('');
            setSearchTermThesesArchive('');

            resetCheckboxesToDefault();          

            handleDeleteFiltersStudents();
            handleDeleteFiltersThesesDeleting();
            handleDeleteFiltersThesesArchive();
            
            setSelectedToClear(ClearingMode.ARCHIVE_THESES);
            setSidebarOpen(false);
        }
    }

    const chooseStudents = () => {
        if (selectedToClear !== ClearingMode.STUDENTS){
            setSearchTermStudents('');
            setSearchTermThesesDeleting('');
            setSearchTermThesesArchive('');

            resetCheckboxesToDefault();          

            handleDeleteFiltersStudents();
            handleDeleteFiltersThesesDeleting();
            handleDeleteFiltersThesesArchive();

            setSelectedToClear(ClearingMode.STUDENTS);
            setSidebarOpen(false);
        }
    }

    const statusLabels: { [key: string]: string } = {
        "Pending approval": t('status.pending'),
        "Rejected": t('status.rejected'),
        "Approved": t('status.approved'),
        "Assigned": t('status.assigned'),
    }

    return (
        <div className='page-margin'>

            {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
            {/* sidebar - usuwanie tematów */}
            
            {selectedToClear === ClearingMode.DELETE_THESES ? (
                <>
                
                <div className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
                <button className={`bold custom-button ${allowFilteringThesesDeleting() ? '' : 'another-color'} sidebar-button ${sidebarOpen ? 'open' : ''}`} onClick={() => handleToggleSidebarThesesDeleting()}>
                    {t('general.management.filtration')} {sidebarOpen ? '◀' : '▶'}
                </button>
                <h3 className='bold my-4' style={{ textAlign: 'center' }}>{t('general.management.filtration')}</h3>
                <div className="mb-4">
                <label className="bold" htmlFor="supervisors">
                    {t('general.people.supervisor')}:
                </label>
                <div className="supervisor-checkbox-list">
                    {availableSupervisorsThesesDeleting.map((supervisor) => (
                    <div key={supervisor.id} className="checkbox-item mb-2">
                        <input
                        type="checkbox"
                        id={`supervisor-${supervisor.id}`}
                        value={supervisor.id}
                        checked={selectedSupervisorsThesesDeleting.includes(supervisor.id)}
                        onChange={() => {
                            const updatedSupervisors = selectedSupervisorsThesesDeleting.includes(supervisor.id)
                            ? selectedSupervisorsThesesDeleting.filter((id) => id !== supervisor.id)
                            : [...selectedSupervisorsThesesDeleting, supervisor.id];
                            setSelectedSupervisorsThesesDeleting(updatedSupervisors);
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
                    <label className="bold" htmlFor="cycle">
                        {t('general.university.studyCycle')}:
                    </label>
                    <select
                        id="cycle"
                        name="cycle"
                        value={selectedCycleNameThesesDeleting}
                        onChange={(e) => {
                        setSelectedCycleNameThesesDeleting(e.target.value);
                        }}
                        className="form-control"
                    >
                        <option value="">{t('general.management.choose')}</option>
                        {availableCyclesThesesDeleting.map((cycle) => (
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
                    value={selectedFacultyAbbrThesesDeleting}
                    onChange={(e) => {
                    setSelectedFacultyAbbrThesesDeleting(e.target.value);
                    setSelectedFieldAbbrThesesDeleting("")
                    setSelectedSpecializationAbbrThesesDeleting("")
                    }}
                    className="form-control"
                >
                    <option value="">{t('general.management.choose')}</option>
                    {availableFacultiesThesesDeleting.map((faculty) => (
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
                    value={selectedFieldAbbrThesesDeleting}
                    onChange={(e) => {
                    setSelectedFieldAbbrThesesDeleting(e.target.value);
                    setSelectedSpecializationAbbrThesesDeleting("")
                    }}
                    className="form-control"
                    disabled={selectedFacultyAbbrThesesDeleting === ""}
                >
                    <option value={""}>{t('general.management.choose')}</option>
                    {selectedFacultyAbbrThesesDeleting !== "" &&
                    availableFieldsThesesDeleting
                        .filter((fi) => fi.faculty.abbreviation === selectedFacultyAbbrThesesDeleting)
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
                    value={selectedSpecializationAbbrThesesDeleting}
                    onChange={(e) => {
                    setSelectedSpecializationAbbrThesesDeleting(e.target.value);
                    }}
                    className="form-control"
                    disabled={selectedFieldAbbrThesesDeleting === ""}
                >
                    <option value={""}>{t('general.management.choose')}</option>
                    {selectedFieldAbbrThesesDeleting !== "" &&
                    availableSpecializationsThesesDeleting
                        .filter((s) => s.studyField.abbreviation === selectedFieldAbbrThesesDeleting)
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
                    onClick={() => { handleDeleteFiltersThesesDeleting() }}>
                    {t('general.management.filterClear')}
                </button>
                <button className="custom-button" onClick={() => handleFiltrationThesesDeleting(true)}>
                    {t('general.management.filter')}
                </button>
                </div>
            </div>
                
                </>
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
            // sidebar - studenci
            ) : selectedToClear === ClearingMode.STUDENTS ? (
                <>
                
                <div className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
                    <button className={`bold custom-button ${allowFilteringStudents() ? '' : 'another-color'} sidebar-button ${sidebarOpen ? 'open' : ''}`} onClick={() => handleToggleSidebarStudents()}>
                    {t('general.management.filtration')} {sidebarOpen ? '◀' : '▶'}
                    </button>
                    <h3 className='bold my-4' style={{ textAlign: 'center' }}>{t('general.management.filtration')}</h3>
                    <div className="mb-4">

                    <div className="mb-4">
                        <label className="bold" htmlFor="cycle">
                            {t('general.university.studyCycle')}:
                        </label>
                        <select
                            id="cycle"
                            name="cycle"
                            value={selectedCycleNameStudents}
                            onChange={(e) => {
                                const cycle = e.target.value;
                                setSelectedCycleNameStudents(cycle);
                            }}
                            className="form-control"
                        >
                            <option value="">{t('general.management.choose')}</option>
                            {availableCyclesStudents.map((cycle) => (
                            <option key={cycle.id} value={cycle.name}>
                                {cycle.name}
                            </option>
                            ))}
                        </select>
                    </div>

                    <hr className="my-4" />

                    <label className="bold" htmlFor="facultyStud">
                        {t('general.university.faculty')}:
                    </label>
                    <select
                        id="facultyStud"
                        name="facultyStud"
                        value={selectedFacultyAbbrStudents}
                        onChange={(e) => {
                        setSelectedFacultyAbbrStudents(e.target.value);
                        setSelectedFieldAbbrStudents("")
                        setSelectedSpecializationAbbrStudents("")
                        }}
                        className="form-control"
                    >
                        <option value="">{t('general.management.choose')}</option>
                        {availableFacultiesStudents.map((faculty) => (
                        <option key={faculty.abbreviation} value={faculty.abbreviation}>
                            {faculty.name}
                        </option>
                        ))}
                    </select>
                    </div>
                    <div className="mb-4">
                    <label className="bold" htmlFor="studyFieldStud">
                        {t('general.university.field')}:
                    </label>
                    <select
                        id="studyFieldStud"
                        name="studyFieldStud"
                        value={selectedFieldAbbrStudents}
                        onChange={(e) => {
                            setSelectedFieldAbbrStudents(e.target.value);
                            setSelectedSpecializationAbbrStudents("")
                        }}
                        className="form-control"
                        disabled={selectedFacultyAbbrStudents === ""}
                    >
                        <option value={""}>{t('general.management.choose')}</option>
                        {selectedFacultyAbbrStudents !== "" &&
                        availableFieldsStudents
                            .filter((fi) => fi.faculty.abbreviation === selectedFacultyAbbrStudents)
                            .map((field, fIndex) => (
                            <option key={fIndex} value={field.abbreviation}>
                                {field.name}
                            </option>
                            ))}
                    </select>
                    </div>
                    <div className="mb-4">
                    <label className="bold" htmlFor="specializationStud">
                        {t('general.university.specialization')}:
                    </label>
                    <select
                        id="specializationStud"
                        name="specializationStud"
                        value={selectedSpecializationAbbrStudents}
                        onChange={(e) => {
                            setSelectedSpecializationAbbrStudents(e.target.value);
                        }}
                        className="form-control"
                        disabled={selectedFieldAbbrStudents === ""}
                    >
                        <option value={""}>{t('general.management.choose')}</option>
                        {selectedFieldAbbrStudents !== "" &&
                        availableSpecializationsStudents
                            .filter((s) => s.studyField.abbreviation === selectedFieldAbbrStudents)
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
                        onClick={() => { handleDeleteFiltersStudents() }}>
                        {t('general.management.filterClear')}
                    </button>
                    <button className="custom-button" onClick={() => handleFiltrationStudents(true)}>
                        {t('general.management.filter')}
                    </button>
                    </div>
                </div>
                
                </>
            ) : selectedToClear === ClearingMode.ARCHIVE_THESES ? ( 
                <>
                
                    <div className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
                    <button className={`bold custom-button ${allowFilteringThesesArchive() ? '' : 'another-color'} sidebar-button ${sidebarOpen ? 'open' : ''}`} onClick={() => handleToggleSidebarThesesArchive()}>
                        {t('general.management.filtration')} {sidebarOpen ? '◀' : '▶'}
                    </button>
                    <h3 className='bold my-4' style={{ textAlign: 'center' }}>{t('general.management.filtration')}</h3>
                    <div className="mb-4">
                    <label className="bold" htmlFor="supervisors">
                        {t('general.people.supervisor')}:
                    </label>
                    <div className="supervisor-checkbox-list">
                        {availableSupervisorsThesesArchive.map((supervisor) => (
                        <div key={supervisor.id} className="checkbox-item mb-2">
                            <input
                            type="checkbox"
                            id={`supervisor-${supervisor.id}`}
                            value={supervisor.id}
                            checked={selectedSupervisorsThesesArchive.includes(supervisor.id)}
                            onChange={() => {
                                const updatedSupervisors = selectedSupervisorsThesesArchive.includes(supervisor.id)
                                ? selectedSupervisorsThesesArchive.filter((id) => id !== supervisor.id)
                                : [...selectedSupervisorsThesesArchive, supervisor.id];
                                setSelectedSupervisorsThesesArchive(updatedSupervisors);
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
                        <label className="bold" htmlFor="status">
                            {t('general.university.status')}:
                        </label>
                        <select
                            id="status"
                            name="status"
                            value={selectedStatusesNameThesesArchive}
                            onChange={(e) => {
                                setSelectedStatusesNameThesesArchive(e.target.value);
                            }}
                            className="form-control"
                        >
                            <option value="">{t('general.management.choose')}</option>
                            {Object.keys(availableStatuses).map((statusKey) => (
                            <option key={statusKey} value={statusKey}>
                                {availableStatuses[statusKey]}
                            </option>
                            ))}
                        </select>
                    </div>
                    <hr className="my-4" />
                    <div className="mb-4">
                        <label className="bold" htmlFor="cycle">
                            {t('general.university.studyCycle')}:
                        </label>
                        <select
                            id="cycle"
                            name="cycle"
                            value={selectedCycleNameThesesArchive}
                            onChange={(e) => {
                                setSelectedCycleNameThesesArchive(e.target.value);
                            }}
                            className="form-control"
                        >
                            <option value="">{t('general.management.choose')}</option>
                            {availableCyclesThesesArchive.map((cycle) => (
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
                        value={selectedFacultyAbbrThesesArchive}
                        onChange={(e) => {
                            setSelectedFacultyAbbrThesesArchive(e.target.value);
                            setSelectedFieldAbbrThesesArchive("")
                            setSelectedSpecializationAbbrThesesArchive("")
                        }}
                        className="form-control"
                    >
                        <option value="">{t('general.management.choose')}</option>
                        {availableFacultiesThesesArchive.map((faculty) => (
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
                        value={selectedFieldAbbrThesesArchive}
                        onChange={(e) => {
                            setSelectedFieldAbbrThesesArchive(e.target.value);
                            setSelectedSpecializationAbbrThesesArchive("");
                        }}
                        className="form-control"
                        disabled={selectedFacultyAbbrThesesArchive === ""}
                    >
                        <option value={""}>{t('general.management.choose')}</option>
                        {selectedFacultyAbbrThesesArchive !== "" &&
                        availableFieldsThesesArchive
                            .filter((fi) => fi.faculty.abbreviation === selectedFacultyAbbrThesesArchive)
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
                        value={selectedSpecializationAbbrThesesArchive}
                        onChange={(e) => {
                        setSelectedSpecializationAbbrThesesArchive(e.target.value);
                        }}
                        className="form-control"
                        disabled={selectedFieldAbbrThesesArchive === ""}
                    >
                        <option value={""}>{t('general.management.choose')}</option>
                        {selectedFieldAbbrThesesArchive !== "" &&
                        availableSpecializationsThesesArchive
                            .filter((s) => s.studyField.abbreviation === selectedFieldAbbrThesesArchive)
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
                        onClick={() => { handleDeleteFiltersThesesArchive() }}>
                        {t('general.management.filterClear')}
                    </button>
                    <button className="custom-button" onClick={() => handleFiltrationThesesArchive(true)}>
                        {t('general.management.filter')}
                    </button>
                    </div>
                </div>
                
                </>
             ) : null}

            {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
            
            {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
            {/* wspólne */}
            {(!thesesDeletingLoaded || !studentsLoaded || !thesesArchiveLoaded) ? (
                <div className='info-no-data'>
                    <LoadingSpinner height="50vh" />
                </div>
            ) : (<React.Fragment>
                
                    
                <div className="d-flex justify-content-begin align-items-center">
                    <button 
                        className={`custom-button ${selectedToClear === ClearingMode.STUDENTS ? '' : 'another-color'}`}
                        onClick={chooseStudents}
                    >
                        {t('general.clearData.clearStudents')}
                    </button>

                    <button 
                        className={`custom-button ${selectedToClear === ClearingMode.ARCHIVE_THESES ? '' : 'another-color'}`}
                        onClick={chooseThesesArchive}
                    >
                        {t('general.clearData.archiveTheses')}
                    </button>

                    <button 
                        className={`custom-button ${selectedToClear === ClearingMode.DELETE_THESES ? '' : 'another-color'}`}
                        onClick={chooseThesesDeleting}
                    >
                        {t('general.clearData.clearTheses')}
                    </button>
                </div>
                <div className="d-flex justify-content-begin align-items-center mt-3">

                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}

                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
                {/* Przyciski usuwające i ich potwierdzanie */}
                {(selectedToClear === ClearingMode.DELETE_THESES) && closedTheses.length !== 0 ? (
                        <>
                            <button
                                type="button"
                                className={`custom-button ${thesesDeletingFormIndexes.size === 0 ? 'another-color' : ''}`}
                                onClick={() => handleConfirmClickThesesDeleting()}
                                disabled={thesesDeletingFormIndexes.size === 0}
                                >
                                {t('general.management.deleteSelected')}
                            </button>

                            {showDeleteConfirmationThesesDeleting && (
                            <tr>
                                <td colSpan={5}>
                                    <ChoiceConfirmation
                                        isOpen={showDeleteConfirmationThesesDeleting}
                                        onClose={handleConfirmCancelThesesDeleting}
                                        onConfirm={handleConfirmAcceptThesesDeleting}
                                        onCancel={handleConfirmCancelThesesDeleting}
                                        questionText={t('thesis.acceptDeletionBulk', { idCount: thesesDeletingFormIndexes.size })}
                                    />
                                </td>
                            </tr>
                            )}
                        </>
                    ) : selectedToClear === ClearingMode.STUDENTS && students.length !== 0 ? (
                            <>
                                {submittedCycleNameStudents === "" ? (
                                    <Alert variant="warning" className="m-0">
                                        {t('student.filterCycles')}
                                    </Alert>
                                ) : (
                                    <>      
                                    <button
                                        type="button"
                                        className={`custom-button ${studentsFormIndexes.size === 0 ? 'another-color' : ''}`}
                                        onClick={() => handleConfirmClickStudents()}
                                        disabled={studentsFormIndexes.size === 0}
                                    >
                                        {t('general.management.deleteSelected')}
                                    </button>
    
                                    {showAcceptConfirmationStudents && (
                                    <tr>
                                        <td colSpan={5}>
                                            <ChoiceConfirmation
                                                isOpen={showAcceptConfirmationStudents}
                                                onClose={handleConfirmCancelStudents}
                                                onConfirm={handleConfirmAcceptStudents}
                                                onCancel={handleConfirmCancelStudents}
                                                questionText={t('student.acceptDeletionBulk', { idCount: studentsFormIndexes.size })}
                                            />
                                        </td>
                                    </tr>
                                    )}
                                    </>
                                )}
                            </>
                    ) : selectedToClear === ClearingMode.ARCHIVE_THESES && theses.length !== 0 ? (
                        <>
                            <button
                                type="button"
                                className={`custom-button ${thesesArchiveFormIndexes.size === 0 ? 'another-color' : ''}`}
                                onClick={() => handleConfirmClickThesesArchive()}
                                disabled={thesesArchiveFormIndexes.size === 0}
                                >
                                {t('general.management.archiveSelected')}
                            </button>

                            {showDeleteConfirmationThesesArchive && (
                            <tr>
                                <td colSpan={5}>
                                    <ChoiceConfirmation
                                        isOpen={showDeleteConfirmationThesesArchive}
                                        onClose={handleConfirmCancelThesesArchive}
                                        onConfirm={handleConfirmAcceptThesesArchive}
                                        onCancel={handleConfirmCancelThesesArchive}
                                        questionText={t('thesis.acceptArchiveBulk', { idCount: thesesArchiveFormIndexes.size })}
                                    />
                                </td>
                            </tr>
                            )}
                        </>
                    ) : null}
                    </div>
                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}

                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
                {/* search bar i paginacja górna */}
                <div className='d-flex justify-content-between align-items-center'>
                    {selectedToClear === ClearingMode.DELETE_THESES && closedTheses.length !== 0 ? (
                        <>
                            <SearchBar
                                searchTerm={searchTermThesesDeleting}
                                setSearchTerm={setSearchTermThesesDeleting}
                                placeholder={t('general.management.search')}
                            />
                            {currentITEMS_PER_PAGE.length > 1 && (
                            <div className="d-flex justify-content-between">
                                <div className="d-flex align-items-center">
                                <label style={{ marginRight: '10px' }}>{t('general.management.view')}:</label>
                                <select
                                    value={thesesDeletingPerPage}
                                    onChange={(e) => {
                                        setThesesDeletingPerPage(e.target.value);
                                        setChosenThesesDeletingPerPage(e.target.value);
                                        handlePageChangeThesesDeleting(1);
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
                                    {thesesDeletingPerPage !== 'All' && (
                                        <div className="pagination">
                                            <button
                                                onClick={() => handlePageChangeThesesDeleting(currentPageThesesDeleting - 1)}
                                                disabled={currentPageThesesDeleting === 1}
                                                className='custom-button'
                                            >
                                                &lt;
                                            </button>

                                            <input
                                                type="number"
                                                value={inputValueThesesDeleting}
                                                onChange={(e) => {
                                                    const newPage = parseInt(e.target.value, 10);
                                                    setInputValueThesesDeleting(newPage);
                                                }}
                                                onKeyDown={(e) => {
                                                    if (e.key === 'Enter') {
                                                        handlePageChangeThesesDeleting(inputValueThesesDeleting);
                                                    }
                                                }}
                                                onBlur={() => {
                                                    handlePageChangeThesesDeleting(inputValueThesesDeleting);
                                                }}
                                                className='text'
                                            />

                                            <span className='text'> {t('general.pagination')} {totalPagesThesesDeleting}</span>
                                            <button
                                                onClick={() => handlePageChangeThesesDeleting(currentPageThesesDeleting + 1)}
                                                disabled={currentPageThesesDeleting === totalPagesThesesDeleting}
                                                className='custom-button'
                                            >
                                                &gt;
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </div>
                            )}
                        </>    
                    ) : selectedToClear === ClearingMode.STUDENTS && students.length !== 0 ? (
                        <>
                            <SearchBar
                                searchTerm={searchTermStudents}
                                setSearchTerm={setSearchTermStudents}
                                placeholder={t('general.management.search')}
                            />
                            {currentITEMS_PER_PAGE.length > 1 && (
                            <div className="d-flex justify-content-between">
                                <div className="d-flex align-items-center">
                                <label style={{ marginRight: '10px' }}>{t('general.management.view')}:</label>
                                <select
                                    value={studentsPerPage}
                                    onChange={(e) => {
                                    setStudentsPerPage(e.target.value);
                                    setChosenStudentsPerPage(e.target.value);
                                    handlePageChangeStudents(1);
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
                                {studentsPerPage !== 'All' && (
                                    <div className="pagination">
                                    <button
                                        onClick={() => handlePageChangeStudents(currentPageStudents - 1)}
                                        disabled={currentPageStudents === 1}
                                        className='custom-button'
                                    >
                                        &lt;
                                    </button>

                                    <input
                                        type="number"
                                        value={inputValueStudents}
                                        onChange={(e) => {
                                        const newPage = parseInt(e.target.value, 10);
                                            setInputValueStudents(newPage);
                                        }}
                                        onKeyDown={(e) => {
                                        if (e.key === 'Enter') {
                                            handlePageChangeStudents(inputValueStudents);
                                        }
                                        }}
                                        onBlur={() => {
                                            handlePageChangeStudents(inputValueStudents);
                                        }}
                                        className='text'
                                    />

                                    <span className='text'> {t('general.pagination')} {totalPagesStudents}</span>
                                    <button
                                        onClick={() => handlePageChangeStudents(currentPageStudents + 1)}
                                        disabled={currentPageStudents === totalPagesStudents}
                                        className='custom-button'
                                    >
                                        &gt;
                                    </button>
                                    </div>
                                )}
                                </div>
                            </div>
                            )}
                        </>    
                    ) : selectedToClear === ClearingMode.ARCHIVE_THESES && theses.length !== 0 ? (
                        <>
                            <SearchBar
                                searchTerm={searchTermThesesArchive}
                                setSearchTerm={setSearchTermThesesArchive}
                                placeholder={t('general.management.search')}
                            />
                            {currentITEMS_PER_PAGE.length > 1 && (
                            <div className="d-flex justify-content-between">
                                <div className="d-flex align-items-center">
                                <label style={{ marginRight: '10px' }}>{t('general.management.view')}:</label>
                                <select
                                    value={thesesArchivePerPage}
                                    onChange={(e) => {
                                        setThesesArchivePerPage(e.target.value);
                                        setChosenThesesArchivePerPage(e.target.value);
                                        handlePageChangeThesesArchive(1);
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
                                    {thesesArchivePerPage !== 'All' && (
                                        <div className="pagination">
                                            <button
                                                onClick={() => handlePageChangeThesesArchive(currentPageThesesArchive - 1)}
                                                disabled={currentPageThesesArchive === 1}
                                                className='custom-button'
                                            >
                                                &lt;
                                            </button>

                                            <input
                                                type="number"
                                                value={inputValueThesesArchive}
                                                onChange={(e) => {
                                                    const newPage = parseInt(e.target.value, 10);
                                                    setInputValueThesesArchive(newPage);
                                                }}
                                                onKeyDown={(e) => {
                                                    if (e.key === 'Enter') {
                                                        handlePageChangeThesesArchive(inputValueThesesArchive);
                                                    }
                                                }}
                                                onBlur={() => {
                                                    handlePageChangeThesesArchive(inputValueThesesArchive);
                                                }}
                                                className='text'
                                            />

                                            <span className='text'> {t('general.pagination')} {totalPagesThesesArchive}</span>
                                            <button
                                                onClick={() => handlePageChangeThesesArchive(currentPageThesesArchive + 1)}
                                                disabled={currentPageThesesArchive === totalPagesThesesArchive}
                                                className='custom-button'
                                            >
                                                &gt;
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </div>
                            )}
                        </>    
                    ) : null}
                </div>
                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}

                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
                {/* usuwanie tematów */}                    
                {selectedToClear === ClearingMode.DELETE_THESES && closedTheses.length !== 0 ? (
                    <>
                        {afterSearchThesesDeleting.length === 0 ? (
                            <div style={{ textAlign: 'center', marginTop: '40px' }}>
                                <p style={{ fontSize: '1.5em' }}>{t('general.management.noSearchData')}</p>
                            </div>
                        ) : (
                            <table className="custom-table" key={`theses-deleting-table-${key}`}>
                                <thead>
                                    <tr>
                                        <th style={{ width: '3%', textAlign: 'center' }}>
                                            <div style={{fontSize: '0.75em'}}>{t('general.management.selectAll')}</div>
                                            <input
                                                type='checkbox'
                                                className='custom-checkbox'
                                                checked={checkAllCheckboxThesesDeleting}
                                                onChange={checkAllCheckboxesChangeThesesDeleting}
                                            />
                                        </th>
                                        <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                                        <th style={{ width: '60%' }}>{t('general.university.thesis')}</th>
                                        <th style={{ width: '14%' }}>{t('general.people.supervisor')}</th>
                                        <th style={{ width: '10%' }}>{t('general.university.status')}</th>
                                        <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.details')}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {currentThesesDeleting.map((thesis, index) => (
                                        <tr key={thesis.id}>
                                            <td>
                                                <div style={{ textAlign: 'center' }}>
                                                <input
                                                    type="checkbox"
                                                    className='custom-checkbox'
                                                    checked={thesesDeletingFormIndexes.has(thesis.id)}
                                                    onChange={(e) => {
                                                    if (e.target.checked) {
                                                        checkCheckboxThesesDeleting(thesis.id);
                                                    } else {
                                                        uncheckCheckboxThesesDeleting(thesis.id);
                                                    }
                                                    }}
                                                    style={{ transform: 'scale(1.25)' }}
                                                />
                                                </div>
                                            </td>
                                            <td className="centered">{indexOfFirstThesesDeleting + index + 1}</td>
                                            <td>
                                                {i18n.language === 'pl' ? (
                                                    thesis.namePL
                                                ) : (
                                                    thesis.nameEN
                                                )}
                                            </td>
                                            <td>{thesis.supervisor.title.name + " " + thesis.supervisor.name + " " + thesis.supervisor.surname}</td>
                                            <td>{statusLabels[thesis.status.name] || thesis.status.name}</td>
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
                        )}
                    </>
                    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
                    // usuwanie studentów
                ) : selectedToClear === ClearingMode.STUDENTS && students.length !== 0 ? (
                    <>
                        {afterSearchStudents.length === 0 ? (
                            <div style={{ textAlign: 'center', marginTop: '40px' }}>
                                <p style={{ fontSize: '1.5em' }}>{t('general.management.noSearchData')}</p>
                            </div>
                        ) : (
                            <table className="custom-table" key={`students-table-${key}`}>
                                <thead>
                                    <tr>
                                        <th style={{ width: '3%', textAlign: 'center' }}>
                                            <div style={{fontSize: '0.75em'}}>{t('general.management.selectAll')}</div>
                                            <input
                                                type='checkbox'
                                                className='custom-checkbox'
                                                checked={checkAllCheckboxStudents}
                                                onChange={checkAllCheckboxesChangeStudents}
                                                disabled={submittedCycleNameStudents === ""}
                                            />
                                        </th>
                                        <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                                        <th style={{ width: '14%' }}>{t('general.people.index')}</th>
                                        <th style={{ width: '35%' }}>{t('general.people.name')}</th>
                                        <th style={{ width: '35%' }}>{t('general.people.surname')}</th>
                                        <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.details')}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {currentStudents.map((student, index) => (
                                        <tr key={student.mail}>
                                            <td>
                                                <div style={{ textAlign: 'center' }}>
                                                    <input
                                                        type="checkbox"
                                                        className='custom-checkbox'
                                                        checked={studentsFormIndexes.has(student.id)}
                                                        onChange={(e) => {
                                                        if (e.target.checked) {
                                                            checkCheckboxStudents(student.id);
                                                        } else {
                                                            uncheckCheckboxStudents(student.id);
                                                        }
                                                        }}
                                                        disabled={submittedCycleNameStudents === ""}
                                                        style={{ transform: 'scale(1.25)' }}
                                                    />
                                                </div>
                                            </td>
                                            <td className="centered">{indexOfFirstStudents + index + 1}</td>
                                            <td>{student.index}</td>
                                            <td>{student.name}</td>
                                            <td>{student.surname}</td>
                                            <td>
                                                <button
                                                    className="custom-button coverall"
                                                    onClick={() => { navigate(`/students/${student.id}`) }}
                                                >
                                                    <i className="bi bi-arrow-right"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        )}
                    </>
                    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
                    // archiwizowanie tematów
                ) : selectedToClear === ClearingMode.ARCHIVE_THESES && theses.length !== 0 ? (
                    <>
                        {afterSearchThesesArchive.length === 0 ? (
                            <div style={{ textAlign: 'center', marginTop: '40px' }}>
                                <p style={{ fontSize: '1.5em' }}>{t('general.management.noSearchData')}</p>
                            </div>
                        ) : (
                            <table className="custom-table" key={`theses-archive-table-${key}`}>
                                <thead>
                                    <tr>
                                        <th style={{ width: '3%', textAlign: 'center' }}>
                                            <div style={{fontSize: '0.75em'}}>{t('general.management.selectAll')}</div>
                                            <input
                                                type='checkbox'
                                                className='custom-checkbox'
                                                checked={checkAllCheckboxThesesArchive}
                                                onChange={checkAllCheckboxesChangeThesesArchive}
                                            />
                                        </th>
                                        <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                                        <th style={{ width: '60%' }}>{t('general.university.thesis')}</th>
                                        <th style={{ width: '14%' }}>{t('general.people.supervisor')}</th>
                                        <th style={{ width: '10%' }}>{t('general.university.status')}</th>
                                        <th style={{ width: '10%', textAlign: 'center' }}>{t('general.management.details')}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {currentThesesArchive.map((thesis, index) => (
                                        <tr key={thesis.id}>
                                            <td>
                                                <div style={{ textAlign: 'center' }}>
                                                <input
                                                    type="checkbox"
                                                    className='custom-checkbox'
                                                    checked={thesesArchiveFormIndexes.has(thesis.id)}
                                                    onChange={(e) => {
                                                    if (e.target.checked) {
                                                        checkCheckboxThesesArchive(thesis.id);
                                                    } else {
                                                        uncheckCheckboxThesesArchive(thesis.id);
                                                    }
                                                    }}
                                                    style={{ transform: 'scale(1.25)' }}
                                                />
                                                </div>
                                            </td>
                                            <td className="centered">{indexOfFirstThesesArchive + index + 1}</td>
                                            <td>
                                                {i18n.language === 'pl' ? (
                                                    thesis.namePL
                                                ) : (
                                                    thesis.nameEN
                                                )}
                                            </td>
                                            <td>{thesis.supervisor.title.name + " " + thesis.supervisor.name + " " + thesis.supervisor.surname}</td>
                                            <td>{statusLabels[thesis.status.name] || thesis.status.name}</td>
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
                        )}
                    </>
                ) : (
                    <div className='info-no-data'>
                        <p>{t('general.management.noData')}</p>
                    </div>
                )}
                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}

                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
                {/* dolna paginacja */}
                {selectedToClear === ClearingMode.DELETE_THESES ? (
                    <>
                        {(currentITEMS_PER_PAGE.length > 1 && thesesDeletingPerPage !== 'All') && (
                            <div className="pagination">
                                <button
                                    onClick={() => handlePageChangeThesesDeleting(currentPageThesesDeleting - 1)}
                                    disabled={currentPageThesesDeleting === 1}
                                    className='custom-button'
                                >
                                    &lt;
                                </button>
            
                                <input
                                    type="number"
                                    value={inputValueThesesDeleting}
                                    onChange={(e) => {
                                        const newPage = parseInt(e.target.value, 10);
                                        setInputValueThesesDeleting(newPage);
                                    }}
                                    onKeyDown={(e) => {
                                    if (e.key === 'Enter') {
                                        handlePageChangeThesesDeleting(inputValueThesesDeleting);
                                    }
                                    }}
                                    onBlur={() => {
                                        handlePageChangeThesesDeleting(inputValueThesesDeleting);
                                    }}
                                    className='text'
                                />
            
                                <span className='text'> {t('general.pagination')} {totalPagesThesesDeleting}</span>
                                <button
                                    onClick={() => handlePageChangeThesesDeleting(currentPageThesesDeleting + 1)}
                                    disabled={currentPageThesesDeleting === totalPagesThesesDeleting}
                                    className='custom-button'
                                >
                                    &gt;
                                </button>
                            </div>
                        )}  
                    </>
                ) : selectedToClear === ClearingMode.STUDENTS ? (
                    <>
                        {(currentITEMS_PER_PAGE.length > 1 && studentsPerPage !== 'All') && (
                            <div className="pagination">
                                <button
                                    onClick={() => handlePageChangeStudents(currentPageStudents - 1)}
                                    disabled={currentPageStudents === 1}
                                    className='custom-button'
                                >
                                    &lt;
                                </button>
            
                                <input
                                    type="number"
                                    value={inputValueStudents}
                                    onChange={(e) => {
                                        const newPage = parseInt(e.target.value, 10);
                                        setInputValueStudents(newPage);
                                    }}
                                    onKeyDown={(e) => {
                                    if (e.key === 'Enter') {
                                        handlePageChangeStudents(inputValueStudents);
                                    }
                                    }}
                                    onBlur={() => {
                                        handlePageChangeStudents(inputValueStudents);
                                    }}
                                    className='text'
                                />
            
                                <span className='text'> {t('general.pagination')} {totalPagesStudents}</span>
                                <button
                                    onClick={() => handlePageChangeStudents(currentPageStudents + 1)}
                                    disabled={currentPageStudents === totalPagesStudents}
                                    className='custom-button'
                                >
                                    &gt;
                                </button>
                            </div>
                        )}  
                    </>
                ) : selectedToClear === ClearingMode.ARCHIVE_THESES ? (
                    <>
                        {(currentITEMS_PER_PAGE.length > 1 && thesesArchivePerPage !== 'All') && (
                            <div className="pagination">
                                <button
                                    onClick={() => handlePageChangeThesesArchive(currentPageThesesArchive - 1)}
                                    disabled={currentPageThesesArchive === 1}
                                    className='custom-button'
                                >
                                    &lt;
                                </button>
            
                                <input
                                    type="number"
                                    value={inputValueThesesArchive}
                                    onChange={(e) => {
                                        const newPage = parseInt(e.target.value, 10);
                                        setInputValueThesesArchive(newPage);
                                    }}
                                    onKeyDown={(e) => {
                                    if (e.key === 'Enter') {
                                        handlePageChangeThesesArchive(inputValueThesesArchive);
                                    }
                                    }}
                                    onBlur={() => {
                                        handlePageChangeThesesArchive(inputValueThesesArchive);
                                    }}
                                    className='text'
                                />
            
                                <span className='text'> {t('general.pagination')} {totalPagesThesesArchive}</span>
                                <button
                                    onClick={() => handlePageChangeThesesArchive(currentPageThesesArchive + 1)}
                                    disabled={currentPageThesesArchive === totalPagesThesesArchive}
                                    className='custom-button'
                                >
                                    &gt;
                                </button>
                            </div>
                        )}  
                    </>
                ) : null}
                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
                </React.Fragment>)}

        </div>
    )

}
export default ClearDataByCycle;