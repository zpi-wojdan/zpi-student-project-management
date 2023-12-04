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

//  TODO: 
//      >   podział paginacji między theses a students
//      >   podział filtracji między theses a students
//      >   dodanie checkboxów do tabel i przycisku usuwania a'la ApproveList.tsx

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

    const [availableSupervisors, setAvailableSupervisor] = useState<Employee[]>([]);
    const [selectedSupervisors, setSelectedSupervisors] = useState<number[]>([]);
    const [submittedSupervisors, setSubmittedSupervisors] = useState<number[]>([]);
    const [availableCycles, setAvailableCycles] = useState<StudyCycle[]>([]);
    const [selectedCycleName, setSelectedCycleName] = useState<string>("");
    const [submittedCycleName, setSubmittedCycleName] = useState<string>("");
        
    //  studentów:
    const [filteredStudents, setFilteredStudents] = useState<Student[]>(students);
    
    //  część wspólna:
    const [availableFaculties, setAvailableFaculties] = useState<Faculty[]>([]);
    const [selectedFacultyAbbr, setSelectedFacultyAbbr] = useState<string>("");
    const [submittedFacultyAbbr, setSubmittedFacultyAbbr] = useState<string>("");
    const [availableFields, setAvailableFields] = useState<StudyField[]>([]);
    const [selectedFieldAbbr, setSelectedFieldAbbr] = useState<string>("");
    const [submittedFieldAbbr, setSubmittedFieldAbbr] = useState<string>("");
    const [availableSpecializations, setAvailableSpecializations] = useState<Specialization[]>([]);
    const [selectedSpecializationAbbr, setSelectedSpecializationAbbr] = useState<string>("");
    const [submittedSpecializationAbbr, setSubmittedSpecializationAbbr] = useState<string>("");
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  wyszukiwanie:
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    const [searchTerm, setSearchTerm] = useState<string>('');
    const [afterSearchTheses, setAfterSearchTheses] = useState<ThesisFront[]>(theses);
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

    const currentStudents = afterSearchStudents.slice(indexOfFirstTheses, indexOfLastTheses);
    const currentTheses = afterSearchTheses.slice(indexOfFirstTheses, indexOfLastTheses);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //  checkboxy - theses
    const [thesesFormIndexes, setThesesFormIndexes] = useState(new Set<number>());
    const [checkedRowsTheses, setCheckedRowsTheses] = useState(new Set());
    const [checkAllCheckboxTheses, setCheckAllCheckboxTheses] = useState(false);

    const [confirmClickedTheses, setConfirmClickedTheses] = useState(false);
    const [rejectClickedTheses, setRejectClickedTheses] = useState(false);
    const [showAcceptConfirmationTheses, setShowAcceptConfirmationTheses] = useState(false);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //  checkboxy - students
    const [studentsFormIndexes, setStudentsFormIndexes] = useState(new Set<number>());
    const [checkedRowsStudents, setCheckedRowsStudents] = useState(new Set());
    const [checkAllCheckboxStudents, setCheckAllCheckboxStudents] = useState(false);

    const [confirmClickedStudents, setConfirmClickedStudents] = useState(false);
    const [rejectClickedStudents, setRejectClickedStudents] = useState(false);
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
            setAvailableSupervisor(supervisors);
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
            setAvailableCycles(sortedCycles);
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
              setAvailableFaculties(sortedFaculties);
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
                setAvailableFields(sortedStudyFields);
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
                setAvailableSpecializations(sortedSpecializations);
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
    //  filtrowanie:
    // useEffect(() => {
    //     if (loaded)
    //       handleFiltration(false);
    //   }, [loaded]);

    const handleSubmitFilters = (toogle: boolean) => {

        setSubmittedFacultyAbbr(selectedFacultyAbbr)
        setSubmittedFieldAbbr(selectedFieldAbbr)
        setSubmittedSpecializationAbbr(selectedSpecializationAbbr)
        setSubmittedCycleName(selectedCycleName)
        setSubmittedSupervisors(selectedSupervisors)
        localStorage.setItem('approverFilterFaculty', selectedFacultyAbbr);
        localStorage.setItem('approverFilterField', selectedFieldAbbr);
        localStorage.setItem('approverFilterSpecialization', selectedSpecializationAbbr);
        localStorage.setItem('approverFilterCycle', selectedCycleName);
        localStorage.setItem('approverFilterSupervisors', JSON.stringify(selectedSupervisors));

        if (toogle){
            handleToggleSidebar()
        }
    };


    const handleToggleSidebar = () => {

        if (!sidebarOpen && selectedToClear !== SelectedToBeCleared.NONE) {
            setSelectedFacultyAbbr(submittedFacultyAbbr)
            setSelectedFieldAbbr(submittedFieldAbbr)
            setSelectedSpecializationAbbr(submittedSpecializationAbbr)
            setSelectedCycleName(submittedCycleName)
            setSelectedSupervisors(submittedSupervisors)
        }
        if (selectedToClear !== SelectedToBeCleared.NONE){
            setSidebarOpen(!sidebarOpen);
        }
    };

    const handleDeleteFilters = () => {
        setSelectedCycleName("");
        setSelectedFacultyAbbr("");
        setSelectedFieldAbbr("");
        setSelectedSpecializationAbbr("");
        setSelectedSupervisors([]);
    
        localStorage.removeItem('approverFilterFaculty');
        localStorage.removeItem('approverFilterField');
        localStorage.removeItem('approverFilterSpecialization');
        localStorage.removeItem('approverFilterCycle');
        localStorage.removeItem('approverFilterSupervisors');
    
        setSubmittedCycleName("");
        setSubmittedFacultyAbbr("");
        setSubmittedFieldAbbr("");
        setSubmittedSpecializationAbbr("");
        setSubmittedSupervisors([]);
    
        setFilteredTheses(theses);
    };

    const handleFiltration = (toggle: boolean) => {

        if (toggle) {
          handleSubmitFilters(true)
    
          const facultyFilter = selectedFacultyAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.faculty.abbreviation === selectedFacultyAbbr) : () => true;
          const fieldFilter = selectedFieldAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.studyField ? p.studyField.abbreviation === selectedFieldAbbr : p.specialization.studyField.abbreviation === selectedFieldAbbr) : () => true;
          const specializationFilter = selectedSpecializationAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.specialization ? p.specialization.abbreviation === selectedSpecializationAbbr : false) : () => true;
          const cycleFilter = selectedCycleName ? (thesis: ThesisFront) => thesis.studyCycle?.name === selectedCycleName : () => true;
          const supervisorFilter = selectedSupervisors.length ? (thesis: ThesisFront) => selectedSupervisors.includes(thesis.supervisor.id) : () => true;
    
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
    
          setSubmittedFacultyAbbr(savedFacultyAbbr)
          setSubmittedFieldAbbr(savedFieldAbbr)
          setSubmittedSpecializationAbbr(savedSpecializationAbbr)
          setSubmittedCycleName(savedCycleName)
          setSubmittedSupervisors(savedsupervisors)
    
          const facultyFilter = savedFacultyAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.faculty.abbreviation === savedFacultyAbbr) : () => true;
          const fieldFilter = savedFieldAbbr ? (thesis: ThesisFront) => thesis.programs.some(p => p.studyField ? p.studyField.abbreviation === savedFieldAbbr : p.specialization.studyField.abbreviation === selectedFieldAbbr) : () => true;
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

    const allowFiltering = () => {
        if (selectedToClear !== SelectedToBeCleared.NONE){
            return true
        }
          return false
    }
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  wyszukiwanie:

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
        setThesesPerPage((filteredItemsPerPage.includes(chosenThesesPerPage)) ? chosenThesesPerPage : ((filteredItemsPerPage.length > 1) ? filteredItemsPerPage[1] : filteredItemsPerPage[0]));
    
      }, [searchTerm, filteredTheses]);
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  paginacja:
    const handlePageChange = (newPage: number) => {
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
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  checkboxy - tematy
    const checkCheckboxTheses = (rowId: number, thesisId: number) => {
        setThesesFormIndexes((prev) => {
          const newSet = new Set(prev);
          newSet.add(thesisId);
          return newSet;
        });
        setCheckedRowsTheses((prev) => {
          const newSet = new Set(prev);
          newSet.add(rowId);
          return newSet;
        });
      }
    
      const uncheckCheckboxTheses = (rowId: number, thesisId: number) => {
        setThesesFormIndexes((prev) => {
            const newSet = new Set(prev);
            newSet.delete(thesisId);
            return newSet;
          });
          setCheckedRowsTheses((prev) => {
            const newSet = new Set(prev);
            newSet.delete(rowId);
            return newSet;
          });
      }
    
      const checkAllCheckboxesChangeTheses = () => {
        setCheckAllCheckboxTheses(!checkAllCheckboxTheses);
        if (checkAllCheckboxTheses){
          setCheckedRowsTheses(new Set());
          setThesesFormIndexes(new Set());
        }
        else{
          let thesisIds = new Set<number>();
          let rowIds = new Set<number>();
          currentTheses.map((thesis, index) => {
            thesisIds.add(thesis.id)
            rowIds.add(index)
          });
          setThesesFormIndexes(thesisIds);
          setCheckedRowsTheses(rowIds);
        }
      }
    
      const handleConfirmClickTheses = () => {
        setShowAcceptConfirmationTheses(true);
        setConfirmClickedTheses(true);
        setRejectClickedTheses(false);
      };
    
      const handleConfirmAcceptTheses = () => {
        const [isValid, statName] = validateTheses();
        if (isValid){
          api.put(`http://localhost:8080/thesis/bulk/${statName}`, Array.from(thesesFormIndexes))
            .then(() => {
              setKey(k => k+1);
              setThesesFormIndexes(new Set());
              setCheckedRowsTheses(new Set());
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
        setShowAcceptConfirmationTheses(false);
      }
    
      const handleConfirmCancelTheses = () => {
        setShowAcceptConfirmationTheses(false);
      }
    
      const validateTheses = () => {
        let name: string;
        if (confirmClickedTheses && !rejectClickedTheses) {
          name = "Approved";
        }
        else if (!confirmClickedTheses && rejectClickedTheses) {
          name = "Rejected";
        }
        else {
          name = "";
        }
        
        let allIdsArePresent = true;
        const indexes = theses.map(t => t.id);
        for (var index of Array.from(thesesFormIndexes.values())) {
          if (!indexes.includes(index)){
            allIdsArePresent = false;
            break;
          }
        }
    
        const isValid = (name !== "") && (allIdsArePresent !== false);
        return [isValid, name];
      }

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //  checkboxy - studenci

    const checkCheckboxStudents = (rowId: number, studentId: number) => {
        setStudentsFormIndexes((prev) => {
          const newSet = new Set(prev);
          newSet.add(studentId);
          return newSet;
        });
        setCheckedRowsStudents((prev) => {
          const newSet = new Set(prev);
          newSet.add(rowId);
          return newSet;
        });
      }
    
      const uncheckCheckboxStudents = (rowId: number, studentId: number) => {
        setStudentsFormIndexes((prev) => {
            const newSet = new Set(prev);
            newSet.delete(studentId);
            return newSet;
          });
          setCheckedRowsStudents((prev) => {
            const newSet = new Set(prev);
            newSet.delete(rowId);
            return newSet;
          });
      }
    
      const checkAllCheckboxesChangeStudents = () => {
        setCheckAllCheckboxStudents(!checkAllCheckboxStudents);
        if (checkAllCheckboxStudents){
          setCheckedRowsStudents(new Set());
          setStudentsFormIndexes(new Set());
        }
        else{
          let studentIds = new Set<number>();
          let rowIds = new Set<number>();
          currentStudents.map((stud, index) => {
            studentIds.add(stud.id)
            rowIds.add(index);
          })
          setStudentsFormIndexes(studentIds);
          setCheckedRowsStudents(rowIds);
        }
        
      }
    
      const handleConfirmClickStudents = () => {
        setShowAcceptConfirmationStudents(true);
        setConfirmClickedStudents(true);
        setRejectClickedStudents(false);
      };
    
      const handleConfirmAcceptStudents = () => {
        const [isValid, statName] = validateStudents();
        if (isValid){
            //  CHANGE THIS - - - - - - - - - - - - - - - - - - 
          api.put(`http://localhost:8080/thesis/bulk/${statName}`, Array.from(thesesFormIndexes))
            .then(() => {
              setKey(k => k+1);
              setStudentsFormIndexes(new Set());
              setCheckedRowsStudents(new Set());
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
        let name: string;
        if (confirmClickedTheses && !rejectClickedTheses) {
          name = "Approved";
        }
        else if (!confirmClickedTheses && rejectClickedTheses) {
          name = "Rejected";
        }
        else {
          name = "";
        }
        
        let allIdsArePresent = true;
        const indexes = students.map(t => t.id);
        for (var index of Array.from(studentsFormIndexes.values())) {
          if (!indexes.includes(index)){
            allIdsArePresent = false;
            break;
          }
        }
    
        const isValid = (name !== "") && (allIdsArePresent !== false);
        return [isValid, name];
      }

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    const resetCheckboxesToDefault = () => {
        setThesesFormIndexes(new Set<number>());
        setCheckedRowsTheses(new Set<number>());
        setCheckAllCheckboxTheses(false);
        setConfirmClickedTheses(false);
        setRejectClickedTheses(false);
        setShowAcceptConfirmationTheses(false)

        setStudentsFormIndexes(new Set<number>());
        setCheckedRowsStudents(new Set<number>());
        setCheckAllCheckboxStudents(false);
        setConfirmClickedStudents(false);
        setRejectClickedStudents(false);
        setShowAcceptConfirmationStudents(false)
    }

    const chooseTheses = () => {
        if (selectedToClear !== SelectedToBeCleared.THESES){
            resetCheckboxesToDefault()
            setSelectedToClear(SelectedToBeCleared.THESES);
        }
        else{
            setSelectedToClear(SelectedToBeCleared.NONE);
        }
    }
    const chooseStudents = () => {
        if (selectedToClear !== SelectedToBeCleared.STUDENTS){
            resetCheckboxesToDefault()            
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
            <div className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
                <button className={`bold custom-button ${allowFiltering() ? '' : 'another-color'} sidebar-button ${sidebarOpen ? 'open' : ''}`} onClick={() => handleToggleSidebar()}>
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
                            disabled={checkedRowsTheses.size === 0}
                            >
                            {t('general.management.deleteSelected')}
                        </button>

                        {showAcceptConfirmationTheses && (
                        <tr>
                            <td colSpan={5}>
                                <ChoiceConfirmation
                                    isOpen={showAcceptConfirmationTheses}
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
                            disabled={checkedRowsStudents.size === 0}
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
                    {selectedToClear !== SelectedToBeCleared.NONE && (
                        <>
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
                                    value={thesesPerPage}
                                    onChange={(e) => {
                                    setThesesPerPage(e.target.value);
                                    setChosenThesesPerPage(e.target.value);
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
                                {thesesPerPage !== 'All' && (
                                    <div className="pagination">
                                    <button
                                        onClick={() => handlePageChange(currentPageTheses - 1)}
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
                                            handlePageChange(inputValueTheses);
                                        }
                                        }}
                                        onBlur={() => {
                                        handlePageChange(inputValueTheses);
                                        }}
                                        className='text'
                                    />

                                    <span className='text'> z {totalPagesTheses}</span>
                                    <button
                                        onClick={() => handlePageChange(currentPageTheses + 1)}
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
                    )}
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
                                                    checked={checkedRowsTheses.has(index)}
                                                    onChange={(e) => {
                                                    if (e.target.checked) {
                                                        checkCheckboxTheses(index, thesis.id);
                                                    } else {
                                                        uncheckCheckboxTheses(index, thesis.id);
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
                                                        checked={checkedRowsStudents.has(index)}
                                                        onChange={(e) => {
                                                        if (e.target.checked) {
                                                            checkCheckboxStudents(index, student.id);
                                                        } else {
                                                            uncheckCheckboxStudents(index, student.id);
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
                {selectedToClear !== SelectedToBeCleared.NONE && (
                    <>
                        {(currentITEMS_PER_PAGE.length > 1 && thesesPerPage !== 'All' && selectedToClear === SelectedToBeCleared.THESES) ? (
                        <>
                            <div className="pagination">
                                <button
                                    onClick={() => handlePageChange(currentPageTheses - 1)}
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
                                        handlePageChange(inputValueTheses);
                                    }
                                    }}
                                    onBlur={() => {
                                        handlePageChange(inputValueTheses);
                                    }}
                                    className='text'
                                />
            
                                <span className='text'> z {totalPagesTheses}</span>
                                <button
                                    onClick={() => handlePageChange(currentPageTheses + 1)}
                                    disabled={currentPageTheses === totalPagesTheses}
                                    className='custom-button'
                                >
                                    &gt;
                                </button>
                            </div>  
                        </>
                    ) : (currentITEMS_PER_PAGE.length > 1 && thesesPerPage !== 'All' && selectedToClear === SelectedToBeCleared.STUDENTS) ? (
                        <>
                            <div className="pagination">
                                <button
                                    onClick={() => handlePageChange(currentPageTheses - 1)}
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
                                        handlePageChange(inputValueTheses);
                                    }
                                    }}
                                    onBlur={() => {
                                        handlePageChange(inputValueTheses);
                                    }}
                                    className='text'
                                />
            
                                <span className='text'> z {totalPagesTheses}</span>
                                <button
                                    onClick={() => handlePageChange(currentPageTheses + 1)}
                                    disabled={currentPageTheses === totalPagesTheses}
                                    className='custom-button'
                                >
                                    &gt;
                                </button>
                            </div>  
                        </>
                    ) : null}
                    </>
                )}
                {/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */}
                </React.Fragment>)}
            </React.Fragment>)}
        </div>
    )

}
export default ClearDataByCycle;