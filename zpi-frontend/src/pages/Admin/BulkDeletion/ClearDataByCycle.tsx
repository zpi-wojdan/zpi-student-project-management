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


const ClearDataByCycle: React.FC = () => {

    enum SelectedToBeCleared {
        NONE = 0,
        THESES = 1,
        STUDENTS = 2,
    }

    //  podstawa
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const { i18n, t } = useTranslation();
    const navigate = useNavigate();

    const [theses, setTheses] = useState<ThesisFront[]>([]);
    const [students, setStudents] = useState<Student[]>([]);

    const [selectedToClear, setSelectedToClear] = useState<SelectedToBeCleared>(SelectedToBeCleared.NONE);
    const [key, setKey] = useState(0);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  filtrowanie:
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    const [sidebarOpen, setSidebarOpen] = useState(false);

    //  tematów:
    const [filteredTheses, setFilteredTheses] = useState<ThesisFront[]>(theses);

    const [availableSupervisorsTheses, setAvailableSupervisorTheses] = useState<Employee[]>([]);
    const [selectedSupervisorsTheses, setSelectedSupervisorsTheses] = useState<number[]>([]);
    const [submittedSupervisorsTheses, setSubmittedSupervisorsTheses] = useState<number[]>([]);
    const [availableCyclesTheses, setAvailableCyclesTheses] = useState<StudyCycle[]>([]);
    const [selectedCycleNameTheses, setSelectedCycleNameTheses] = useState<string>("");
    const [submittedCycleNameTheses, setSubmittedCycleNameTheses] = useState<string>("");
    const [availableFacultiesTheses, setAvailableFacultiesTheses] = useState<Faculty[]>([]);
    const [selectedFacultyAbbrTheses, setSelectedFacultyAbbrTheses] = useState<string>("");
    const [submittedFacultyAbbrTheses, setSubmittedFacultyAbbrTheses] = useState<string>("");
    const [availableFieldsTheses, setAvailableFieldsTheses] = useState<StudyField[]>([]);
    const [selectedFieldAbbrTheses, setSelectedFieldAbbrTheses] = useState<string>("");
    const [submittedFieldAbbrTheses, setSubmittedFieldAbbrTheses] = useState<string>("");
    const [availableSpecializationsTheses, setAvailableSpecializationsTheses] = useState<Specialization[]>([]);
    const [selectedSpecializationAbbrTheses, setSelectedSpecializationAbbrTheses] = useState<string>("");
    const [submittedSpecializationAbbrTheses, setSubmittedSpecializationAbbrTheses] = useState<string>("");

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
    const [searchTermTheses, setSearchTermTheses] = useState<string>('');
    const [afterSearchTheses, setAfterSearchTheses] = useState<ThesisFront[]>(theses);

    const [searchTermStudents, setSearchTermStudents] = useState<string>('');
    const [afterSearchStudents, setAfterSearchStudents] = useState<Student[]>(students);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  paginacja
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //  ogólna
    const ITEMS_PER_PAGE = ['10', '25', '50', 'All'];
    const [currentITEMS_PER_PAGE, setCurrentITEMS_PER_PAGE] = useState(ITEMS_PER_PAGE);
    const [thesesLoaded, setThesesLoaded] = useState<boolean>(false);
    const [studentsLoaded, setStudentsLoaded] = useState<boolean>(false);

    //  tematy
    const [currentPageTheses, setCurrentPageTheses] = useState(1);
    const [inputValueTheses, setInputValueTheses] = useState(currentPageTheses);
    const [thesesPerPage, setThesesPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
    const [chosenThesesPerPage, setChosenThesesPerPage] = useState(thesesPerPage);
    const indexOfLastTheses = thesesPerPage === 'All' ? afterSearchTheses.length : currentPageTheses * parseInt(thesesPerPage, 10);
    const indexOfFirstTheses = thesesPerPage === 'All' ? 0 : indexOfLastTheses - parseInt(thesesPerPage, 10);
    const totalPagesTheses = thesesPerPage === 'All' ? 1 : Math.ceil(afterSearchTheses.length / parseInt(thesesPerPage, 10));
    
    //  studenci
    const [currentPageStudents, setCurrentPageStudents] = useState(1);
    const [inputValueStudents, setInputValueStudents] = useState(currentPageStudents);
    const [studentsPerPage, setStudentsPerPage] = useState((currentITEMS_PER_PAGE.length > 1) ? currentITEMS_PER_PAGE[1] : currentITEMS_PER_PAGE[0]);
    const [chosenStudentsPerPage, setChosenStudentsPerPage] = useState(studentsPerPage);
    const indexOfLastStudents = studentsPerPage === 'All' ? afterSearchTheses.length : currentPageStudents * parseInt(studentsPerPage, 10);
    const indexOfFirstStudents = studentsPerPage === 'All' ? 0 : indexOfLastStudents - parseInt(studentsPerPage, 10);
    const totalPagesStudents = studentsPerPage === 'All' ? 1 : Math.ceil(afterSearchStudents.length / parseInt(studentsPerPage, 10));

    const currentStudents = afterSearchStudents.slice(indexOfFirstStudents, indexOfLastStudents);
    const currentTheses = afterSearchTheses.slice(indexOfFirstTheses, indexOfLastTheses);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //  checkboxy - theses
    const [thesesFormIndexes, setThesesFormIndexes] = useState(new Set<number>());
    const [checkAllCheckboxTheses, setCheckAllCheckboxTheses] = useState(false);

    const [confirmClickedTheses, setConfirmClickedTheses] = useState(false);
    const [showDeleteConfirmationTheses, setShowDeleteConfirmationTheses] = useState(false);
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
        api.get(`http://localhost:8080/thesis`)
            .then((response) => {
                const thesisResponse = response.data.map((thesisDb: Thesis) => {
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
                setTheses(thesisResponse);
                setFilteredTheses(thesisResponse);
                setAfterSearchTheses(thesisResponse);
                setThesesLoaded(true);
            })
            .catch((error) => {
                if (error.response.status === 401 || error.response.status === 403) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });

            //  students:
            api.get(`http://localhost:8080/student`)
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
                if (error.response.status === 401 || error.response.status === 403) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });
    }, [key]);

    //  dane pomocnicze:
    useEffect(() => {

        //  pracownicy:
        api.get('http://localhost:8080/employee')
        .then((response) => {
            const supervisors = response.data
                .filter((employee: Employee) => employee.roles.some((role: Role) => role.name === 'supervisor'))
                .sort((a: Employee, b: Employee) => a.surname.localeCompare(b.surname));
            setAvailableSupervisorTheses(supervisors);
        })
        .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
            }
        });

        //  cykle nauczania:
        api.get('http://localhost:8080/studycycle')
            .then((response) => {
                const sortedCycles = response.data.sort((a: StudyCycle, b: StudyCycle) => {
                return a.name.localeCompare(b.name);
            });
            setAvailableCyclesTheses(sortedCycles);
            setAvailableCyclesStudents(sortedCycles);
            })
            .catch((error) => {
                console.error(error);
                if (error.response.status === 401 || error.response.status === 403) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });

        //  wydziały:
        api.get('http://localhost:8080/faculty')
            .then((response) => {
              const sortedFaculties = response.data.sort((a: Faculty, b: Faculty) => {
                return a.name.localeCompare(b.name);
              });
              setAvailableFacultiesTheses(sortedFaculties);
              setAvailableFacultiesStudents(sortedFaculties);
            })
            .catch((error) => {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
            });

        //  kierunki:
        api.get('http://localhost:8080/studyfield')
            .then((response) => {
                const sortedStudyFields = response.data.sort((a: StudyField, b: StudyField) => {
                    return a.name.localeCompare(b.name);
                });
                setAvailableFieldsTheses(sortedStudyFields);
                setAvailableFieldsStudents(sortedStudyFields);
            })
            .catch((error) => {
                console.error(error)
                if (error.response.status === 401 || error.response.status === 403) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });

        //  specjalizacje
        api.get('http://localhost:8080/specialization')
            .then((response) => {
                const sortedSpecializations = response.data.sort((a: Specialization, b: Specialization) => {
                    return a.name.localeCompare(b.name);
                });
                setAvailableSpecializationsTheses(sortedSpecializations);
                setAvailableSpecializationsStudents(sortedSpecializations);
            })
            .catch((error) => {
                console.error(error)
                if (error.response.status === 401 || error.response.status === 403) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });

    },[]);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  filtrowanie tematów:
    useEffect(() => {
        if (thesesLoaded && selectedToClear === SelectedToBeCleared.THESES)
          handleFiltrationTheses(false);
    }, [thesesLoaded, selectedToClear]);

    const handleSubmitFiltersTheses = (toogle: boolean) => {

        setSubmittedFacultyAbbrTheses(selectedFacultyAbbrTheses)
        setSubmittedFieldAbbrTheses(selectedFieldAbbrTheses)
        setSubmittedSpecializationAbbrTheses(selectedSpecializationAbbrTheses)
        setSubmittedCycleNameTheses(selectedCycleNameTheses)
        setSubmittedSupervisorsTheses(selectedSupervisorsTheses)
        localStorage.setItem('approverFilterFaculty', selectedFacultyAbbrTheses);
        localStorage.setItem('approverFilterField', selectedFieldAbbrTheses);
        localStorage.setItem('approverFilterSpecialization', selectedSpecializationAbbrTheses);
        localStorage.setItem('approverFilterCycle', selectedCycleNameTheses);
        localStorage.setItem('approverFilterSupervisors', JSON.stringify(selectedSupervisorsTheses));

        if (toogle){
            handleToggleSidebarTheses()
        }
    };

    const handleToggleSidebarTheses = () => {

        if (!sidebarOpen && selectedToClear === SelectedToBeCleared.THESES) {
            setSelectedFacultyAbbrTheses(submittedFacultyAbbrTheses)
            setSelectedFieldAbbrTheses(submittedFieldAbbrTheses)
            setSelectedSpecializationAbbrTheses(submittedSpecializationAbbrTheses)
            setSelectedCycleNameTheses(submittedCycleNameTheses)
            setSelectedSupervisorsTheses(submittedSupervisorsTheses)
        }
        if (selectedToClear === SelectedToBeCleared.THESES){
            setSidebarOpen(!sidebarOpen);
        }
    };

    const handleDeleteFiltersTheses = () => {
        setSelectedCycleNameTheses("");
        setSelectedFacultyAbbrTheses("");
        setSelectedFieldAbbrTheses("");
        setSelectedSpecializationAbbrTheses("");
        setSelectedSupervisorsTheses([]);
    
        localStorage.removeItem('approverFilterFaculty');
        localStorage.removeItem('approverFilterField');
        localStorage.removeItem('approverFilterSpecialization');
        localStorage.removeItem('approverFilterCycle');
        localStorage.removeItem('approverFilterSupervisors');
    
        setSubmittedCycleNameTheses("");
        setSubmittedFacultyAbbrTheses("");
        setSubmittedFieldAbbrTheses("");
        setSubmittedSpecializationAbbrTheses("");
        setSubmittedSupervisorsTheses([]);
    
        setFilteredTheses(theses);
    };

    const handleFiltrationTheses = (toggle: boolean) => {

        if (toggle) {
          handleSubmitFiltersTheses(true)
    
          const facultyFilter = selectedFacultyAbbrTheses ? (thesis: ThesisFront) => thesis.programs.some(p => p.faculty.abbreviation === selectedFacultyAbbrTheses) : () => true;
          const fieldFilter = selectedFieldAbbrTheses ? (thesis: ThesisFront) => thesis.programs.some(p => p.studyField ? p.studyField.abbreviation === selectedFieldAbbrTheses : p.specialization.studyField.abbreviation === selectedFieldAbbrTheses) : () => true;
          const specializationFilter = selectedSpecializationAbbrTheses ? (thesis: ThesisFront) => thesis.programs.some(p => p.specialization ? p.specialization.abbreviation === selectedSpecializationAbbrTheses : false) : () => true;
          const cycleFilter = selectedCycleNameTheses ? (thesis: ThesisFront) => thesis.studyCycle?.name === selectedCycleNameTheses : () => true;
          const supervisorFilter = selectedSupervisorsTheses.length ? (thesis: ThesisFront) => selectedSupervisorsTheses.includes(thesis.supervisor.id) : () => true;
    
          const newFilteredTheses = theses.filter(thesis =>
            facultyFilter(thesis) &&
            fieldFilter(thesis) &&
            specializationFilter(thesis) &&
            cycleFilter(thesis) &&
            supervisorFilter(thesis)
          );
          setFilteredTheses(newFilteredTheses);
        }
        else {
          const savedFacultyAbbr = localStorage.getItem('approverFilterFaculty') || '';
          const savedFieldAbbr = localStorage.getItem('approverFilterField') || '';
          const savedSpecializationAbbr = localStorage.getItem('approverFilterSpecialization') || '';
          const savedCycleName = localStorage.getItem('approverFilterCycle') || '';
          const savedsupervisors = JSON.parse(localStorage.getItem('approverFilterSupervisors') || '[]');
    
          setSubmittedFacultyAbbrTheses(savedFacultyAbbr)
          setSubmittedFieldAbbrTheses(savedFieldAbbr)
          setSubmittedSpecializationAbbrTheses(savedSpecializationAbbr)
          setSubmittedCycleNameTheses(savedCycleName)
          setSubmittedSupervisorsTheses(savedsupervisors)
    
          const facultyFilter = savedFacultyAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.faculty.abbreviation === savedFacultyAbbr) : () => true;
          const fieldFilter = savedFieldAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.studyField ? p.studyField.abbreviation === savedFieldAbbr : p.specialization.studyField.abbreviation === selectedFieldAbbrTheses) : () => true;
          const specializationFilter = savedSpecializationAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.specialization ? p.specialization.abbreviation === savedSpecializationAbbr : false) : () => true;
          const cycleFilter = savedCycleName ? (thesis: ThesisFront) => thesis.studyCycle?.name === savedCycleName : () => true;
          const supervisorFilter = savedsupervisors.length ? (thesis: ThesisFront) => savedsupervisors.includes(thesis.supervisor.id) : () => true;
    
          const newFilteredTheses = theses.filter(thesis =>
            facultyFilter(thesis) &&
            fieldFilter(thesis) &&
            specializationFilter(thesis) &&
            cycleFilter(thesis) &&
            supervisorFilter(thesis)
          );
          setFilteredTheses(newFilteredTheses);
        }
    }

    const allowFilteringTheses = () => {
        if (selectedToClear === SelectedToBeCleared.THESES){
            return true
        }
        return false
    }
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  filtrowanie studentów:

    useEffect(() => {
        if (studentsLoaded && selectedToClear === SelectedToBeCleared.STUDENTS)
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
        if (selectedToClear === SelectedToBeCleared.STUDENTS){
            return true
        }
        return false
    }
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  wyszukiwanie:

    useEffect(() => {
        const searchText = searchTermTheses.toLowerCase();
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
    
        handlePageChangeTheses(1);
        setThesesPerPage((filteredItemsPerPage.includes(chosenThesesPerPage)) ? chosenThesesPerPage : ((filteredItemsPerPage.length > 1) ? filteredItemsPerPage[1] : filteredItemsPerPage[0]));
    
      }, [searchTermTheses, filteredTheses]);

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
    const handlePageChangeTheses = (newPage: number) => {
        if (!newPage || newPage < 1) {
          setCurrentPageTheses(1);
          setInputValueTheses(1);
        }
        else {
          if (newPage > totalPagesTheses) {
            setCurrentPageTheses(totalPagesTheses);
            setInputValueTheses(totalPagesTheses);
          }
          else {
            setCurrentPageTheses(newPage);
            setInputValueTheses(newPage);
          }
        }
      };

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
    //  checkboxy - tematy
    const checkCheckboxTheses = (thesisId: number) => {
        setThesesFormIndexes((prev) => {
          const newSet = new Set(prev);
          newSet.add(thesisId);
          return newSet;
        });
      }
    
      const uncheckCheckboxTheses = (thesisId: number) => {
        setThesesFormIndexes((prev) => {
            const newSet = new Set(prev);
            newSet.delete(thesisId);
            return newSet;
          });
      }
    
      const checkAllCheckboxesChangeTheses = () => {
        setCheckAllCheckboxTheses(!checkAllCheckboxTheses);
        if (checkAllCheckboxTheses){
          setThesesFormIndexes(new Set());
        }
        else{
          let thesisIds = new Set<number>();
          currentTheses.map((thesis, _) => {
            thesisIds.add(thesis.id)
          });
          setThesesFormIndexes(thesisIds);
        }
      }
    
      const handleConfirmClickTheses = () => {
        setShowDeleteConfirmationTheses(true);
        setConfirmClickedTheses(true);
      };
    
      const handleConfirmAcceptTheses = () => {
        const isValid = validateTheses();
        if (isValid){
          api.put(`http://localhost:8080/thesis/bulk`, Array.from(thesesFormIndexes))
            .then(() => {
              setKey(k => k+1);
              setThesesFormIndexes(new Set());
              toast.success(t("thesis.deleteSuccesfulBulk"));
            })
            .catch((error) => {
              console.log("Error", error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("thesis.deleteErrorBulk"));
            });
        }
        else{
          toast.error(t("thesis.deleteErrorBulk")); 
        }
        setShowDeleteConfirmationTheses(false);
      }
    
      const handleConfirmCancelTheses = () => {
        setShowDeleteConfirmationTheses(false);
      }
    
      const validateTheses = () => {
        let allIdsArePresent = true;
        const indexes = theses.map(t => t.id);
        for (var index of Array.from(thesesFormIndexes.values())) {
          if (!indexes.includes(index)){
            allIdsArePresent = false;
            break;
          }
        }
    
        return allIdsArePresent !== false;
      }

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  checkboxy - studenci

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
          currentStudents.map((stud, _) => {
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
          api.put(`http://localhost:8080/student/bulk/cycle/${cycleId}`, Array.from(studentsFormIndexes))
            .then(() => {
              setKey(k => k+1);
              setStudentsFormIndexes(new Set());
              toast.success(t("student.deleteSuccesfulBulk"));
            })
            .catch((error) => {
              console.log("Error", error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              toast.error(t("student.deleteErrorBulk"));
            });
        }
        else{
          toast.error(t("student.deleteErrorBulk")); 
        }
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
        setThesesFormIndexes(new Set<number>());
        setCheckAllCheckboxTheses(false);
        setConfirmClickedTheses(false);
        setShowDeleteConfirmationTheses(false)

        setStudentsFormIndexes(new Set<number>());
        setCheckAllCheckboxStudents(false);
        setConfirmClickedStudents(false);
        setShowAcceptConfirmationStudents(false)
    }

    const chooseTheses = () => {
        if (selectedToClear !== SelectedToBeCleared.THESES){
            setSearchTermStudents('');
            setSearchTermTheses('');

            resetCheckboxesToDefault();          

            handleDeleteFiltersStudents();
            handleDeleteFiltersTheses();
            
            setSelectedToClear(SelectedToBeCleared.THESES);
        }
        else{
            setSelectedToClear(SelectedToBeCleared.NONE);
        }
    }
    const chooseStudents = () => {
        if (selectedToClear !== SelectedToBeCleared.STUDENTS){
            setSearchTermStudents('');
            setSearchTermTheses('');

            resetCheckboxesToDefault();          

            handleDeleteFiltersStudents();
            handleDeleteFiltersTheses();

            setSelectedToClear(SelectedToBeCleared.STUDENTS);
        }
        else{
            setSelectedToClear(SelectedToBeCleared.NONE);
        }
    }

    return (
        <div className='page-margin'>

            {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
            {/* sidebar */}
            
            {selectedToClear === SelectedToBeCleared.THESES ? (
                <>
                
                <div className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
                <button className={`bold custom-button ${allowFilteringTheses() ? '' : 'another-color'} sidebar-button ${sidebarOpen ? 'open' : ''}`} onClick={() => handleToggleSidebarTheses()}>
                    {t('general.management.filtration')} {sidebarOpen ? '◀' : '▶'}
                </button>
                <h3 className='bold my-4' style={{ textAlign: 'center' }}>{t('general.management.filtration')}</h3>
                <div className="mb-4">
                <label className="bold" htmlFor="supervisors">
                    {t('general.people.supervisor')}:
                </label>
                <div className="supervisor-checkbox-list">
                    {availableSupervisorsTheses.map((supervisor) => (
                    <div key={supervisor.id} className="checkbox-item mb-2">
                        <input
                        type="checkbox"
                        id={`supervisor-${supervisor.id}`}
                        value={supervisor.id}
                        checked={selectedSupervisorsTheses.includes(supervisor.id)}
                        onChange={() => {
                            const updatedSupervisors = selectedSupervisorsTheses.includes(supervisor.id)
                            ? selectedSupervisorsTheses.filter((id) => id !== supervisor.id)
                            : [...selectedSupervisorsTheses, supervisor.id];
                            setSelectedSupervisorsTheses(updatedSupervisors);
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
                        value={selectedCycleNameTheses}
                        onChange={(e) => {
                        setSelectedCycleNameTheses(e.target.value);
                        }}
                        className="form-control"
                    >
                        <option value="">{t('general.management.choose')}</option>
                        {availableCyclesTheses.map((cycle) => (
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
                    value={selectedFacultyAbbrTheses}
                    onChange={(e) => {
                    setSelectedFacultyAbbrTheses(e.target.value);
                    setSelectedFieldAbbrTheses("")
                    setSelectedSpecializationAbbrTheses("")
                    }}
                    className="form-control"
                >
                    <option value="">{t('general.management.choose')}</option>
                    {availableFacultiesTheses.map((faculty) => (
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
                    value={selectedFieldAbbrTheses}
                    onChange={(e) => {
                    setSelectedFieldAbbrTheses(e.target.value);
                    setSelectedSpecializationAbbrTheses("")
                    }}
                    className="form-control"
                    disabled={selectedFacultyAbbrTheses === ""}
                >
                    <option value={""}>{t('general.management.choose')}</option>
                    {selectedFacultyAbbrTheses !== "" &&
                    availableFieldsTheses
                        .filter((fi) => fi.faculty.abbreviation === selectedFacultyAbbrTheses)
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
                    value={selectedSpecializationAbbrTheses}
                    onChange={(e) => {
                    setSelectedSpecializationAbbrTheses(e.target.value);
                    }}
                    className="form-control"
                    disabled={selectedFieldAbbrTheses === ""}
                >
                    <option value={""}>{t('general.management.choose')}</option>
                    {selectedFieldAbbrTheses !== "" &&
                    availableSpecializationsTheses
                        .filter((s) => s.studyField.abbreviation === selectedFieldAbbrTheses)
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
                    onClick={() => { handleDeleteFiltersTheses() }}>
                    {t('general.management.filterClear')}
                </button>
                <button className="custom-button" onClick={() => handleFiltrationTheses(true)}>
                    {t('general.management.filter')}
                </button>
                </div>
            </div>
                
                </>
            ) : selectedToClear === SelectedToBeCleared.STUDENTS ? (
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
            ) : null}

            {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
            
            {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
            {/* wspólne */}
            {(!thesesLoaded || !studentsLoaded) ? (
                <div className='info-no-data'>
                    <p>{t('general.management.load')}</p>
                </div>
            ) : (<React.Fragment>
                {theses.length === 0 ? (
                <div className='info-no-data'>
                    <p>{t('general.management.noData')}</p>
                </div>
                ) : (<React.Fragment>
                    
                <div className="d-flex justify-content-begin align-items-center">
                    <button 
                        className={`custom-button ${selectedToClear === SelectedToBeCleared.THESES ? '' : 'another-color'}`}
                        onClick={chooseTheses}
                    >
                        {t('general.university.theses')}
                    </button>
                    {selectedToClear === SelectedToBeCleared.THESES && (
                        <>
                        
                        <button
                            type="button"
                            className="custom-button"
                            onClick={() => handleConfirmClickTheses()}
                            disabled={thesesFormIndexes.size === 0}
                            >
                            {t('general.management.deleteSelected')}
                        </button>

                        {showDeleteConfirmationTheses && (
                        <tr>
                            <td colSpan={5}>
                                <ChoiceConfirmation
                                    isOpen={showDeleteConfirmationTheses}
                                    onClose={handleConfirmCancelTheses}
                                    onConfirm={handleConfirmAcceptTheses}
                                    onCancel={handleConfirmCancelTheses}
                                    questionText={t('thesis.acceptDeletionBulk')}
                                />
                            </td>
                        </tr>
                        )}

                        </>
                    )}
                    <button 
                        className={`custom-button ${selectedToClear === SelectedToBeCleared.STUDENTS ? '' : 'another-color'}`}
                        onClick={chooseStudents}
                    >
                        {t('general.people.students')}
                    </button>
                    {selectedToClear === SelectedToBeCleared.STUDENTS && (
                        <>
                        <button
                            type="button"
                            className="custom-button"
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
                                    questionText={t('student.acceptDeletionBulk')}
                                />
                            </td>
                        </tr>
                        )}

                        </>
                    )}

                </div>
                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}

                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
                {/* search bar i paginacja górna */}
                <div className='d-flex justify-content-between align-items-center'>
                    {selectedToClear === SelectedToBeCleared.THESES ? (
                        <>
                            <SearchBar
                                searchTerm={searchTermTheses}
                                setSearchTerm={setSearchTermTheses}
                                placeholder={t('general.management.search')}
                            />
                            {currentITEMS_PER_PAGE.length > 1 && (
                            <div className="d-flex justify-content-between">
                                <div className="d-flex align-items-center">
                                <label style={{ marginRight: '10px' }}>{t('general.management.view')}:</label>
                                <select
                                    value={thesesPerPage}
                                    onChange={(e) => {
                                        setThesesPerPage(e.target.value);
                                        setChosenThesesPerPage(e.target.value);
                                        handlePageChangeTheses(1);
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
                                    {thesesPerPage !== 'All' && (
                                        <div className="pagination">
                                            <button
                                                onClick={() => handlePageChangeTheses(currentPageTheses - 1)}
                                                disabled={currentPageTheses === 1}
                                                className='custom-button'
                                            >
                                                &lt;
                                            </button>

                                            <input
                                                type="number"
                                                value={inputValueTheses}
                                                onChange={(e) => {
                                                    const newPage = parseInt(e.target.value, 10);
                                                    setInputValueTheses(newPage);
                                                }}
                                                onKeyDown={(e) => {
                                                    if (e.key === 'Enter') {
                                                        handlePageChangeTheses(inputValueTheses);
                                                    }
                                                }}
                                                onBlur={() => {
                                                    handlePageChangeTheses(inputValueTheses);
                                                }}
                                                className='text'
                                            />

                                            <span className='text'> z {totalPagesTheses}</span>
                                            <button
                                                onClick={() => handlePageChangeTheses(currentPageTheses + 1)}
                                                disabled={currentPageTheses === totalPagesTheses}
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
                    ) : selectedToClear === SelectedToBeCleared.STUDENTS ? (
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

                                    <span className='text'> z {totalPagesStudents}</span>
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
                    ) : null}
                </div>
                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}

                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
                {/* theses */}                    
                {selectedToClear === SelectedToBeCleared.THESES ? (
                    <>
                        {afterSearchTheses.length === 0 ? (
                            <div style={{ textAlign: 'center', marginTop: '40px' }}>
                                <p style={{ fontSize: '1.5em' }}>{t('general.management.noSearchData')}</p>
                            </div>
                        ) : (
                            <table className="custom-table" key={`theses-table-${key}`}>
                                <thead>
                                    <tr>
                                        <th style={{ width: '3%', textAlign: 'center' }}>
                                            <div style={{fontSize: '0.75em'}}>{t('general.management.selectAll')}</div>
                                            <input
                                                type='checkbox'
                                                className='custom-checkbox'
                                                checked={checkAllCheckboxTheses}
                                                onChange={checkAllCheckboxesChangeTheses}
                                            />
                                        </th>
                                        <th style={{ width: '3%', textAlign: 'center' }}>#</th>
                                        <th style={{ width: '60%' }}>{t('general.university.thesis')}</th>
                                        <th style={{ width: '15%' }}>{t('general.people.supervisor')}</th>
                                        <th style={{ width: '9%', textAlign: 'center' }}>{t('general.management.details')}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {currentTheses.map((thesis, index) => (
                                        <tr key={thesis.id}>
                                            <td>
                                                <div style={{ textAlign: 'center' }}>
                                                <input
                                                    type="checkbox"
                                                    className='custom-checkbox'
                                                    checked={thesesFormIndexes.has(thesis.id)}
                                                    onChange={(e) => {
                                                    if (e.target.checked) {
                                                        checkCheckboxTheses(thesis.id);
                                                    } else {
                                                        uncheckCheckboxTheses(thesis.id);
                                                    }
                                                    }}
                                                    style={{ transform: 'scale(1.25)' }}
                                                />
                                                </div>
                                            </td>
                                            <td className="centered">{indexOfFirstTheses + index + 1}</td>
                                            <td>
                                                {i18n.language === 'pl' ? (
                                                    thesis.namePL
                                                ) : (
                                                    thesis.nameEN
                                                )}
                                            </td>
                                            <td>{thesis.supervisor.title.name + " " + thesis.supervisor.name + " " + thesis.supervisor.surname}</td>
                                            <td>
                                                <button
                                                    className="custom-button coverall"
                                                    onClick={() => { navigate(`/manage/${thesis.id}`) }}
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
                    // students
                ) : selectedToClear === SelectedToBeCleared.STUDENTS ? (
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
                                                        style={{ transform: 'scale(1.25)' }}
                                                    />
                                                </div>
                                            </td>
                                            <td className="centered">{indexOfFirstTheses + index + 1}</td>
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
                    // - - - - - - - - - - - - - - - - - - - - - - - - - - -
                ) : (
                    <div className='info-no-data'>
                        <p>{t('general.clearData.choice')}</p>
                    </div>
                )}
                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}

                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
                {/* dolna paginacja */}
                {selectedToClear === SelectedToBeCleared.THESES ? (
                    <>
                        {(currentITEMS_PER_PAGE.length > 1 && thesesPerPage !== 'All') && (
                            <div className="pagination">
                                <button
                                    onClick={() => handlePageChangeTheses(currentPageTheses - 1)}
                                    disabled={currentPageTheses === 1}
                                    className='custom-button'
                                >
                                    &lt;
                                </button>
            
                                <input
                                    type="number"
                                    value={inputValueTheses}
                                    onChange={(e) => {
                                        const newPage = parseInt(e.target.value, 10);
                                        setInputValueTheses(newPage);
                                    }}
                                    onKeyDown={(e) => {
                                    if (e.key === 'Enter') {
                                        handlePageChangeTheses(inputValueTheses);
                                    }
                                    }}
                                    onBlur={() => {
                                        handlePageChangeTheses(inputValueTheses);
                                    }}
                                    className='text'
                                />
            
                                <span className='text'> z {totalPagesTheses}</span>
                                <button
                                    onClick={() => handlePageChangeTheses(currentPageTheses + 1)}
                                    disabled={currentPageTheses === totalPagesTheses}
                                    className='custom-button'
                                >
                                    &gt;
                                </button>
                            </div>
                        )}  
                    </>
                ) : selectedToClear === SelectedToBeCleared.STUDENTS ? (
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
            
                                <span className='text'> z {totalPagesStudents}</span>
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
                ) : null}
                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
                </React.Fragment>)}
            </React.Fragment>)}
        </div>
    )

}
export default ClearDataByCycle;