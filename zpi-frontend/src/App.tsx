import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import ReservationPage from './pages/reservation/Reservation';
import ThesesTable from './pages/Theses/Theses';
import ThesesDetails from './pages/Theses/ThesesDetails';
// @ts-ignore
import Navigation from './layout/Navigation';
import SingleReservationPage from './pages/reservation/SingleReservation';
import {AuthProvider} from "./auth/AuthProvider";
import AddThesisPageAdmin from './pages/Theses/AddThesisAdmin';
import Unauthorized from './pages/Unauthorized';
import UploadStudentFilePage from './pages/UploadingFiles/UploadStudentsFile';
import UplaodEmployeeFilePage from './pages/UploadingFiles/UploadEmployeeFile';
// @ts-ignore
import StudentList from './pages/Admin/Student/List';
// @ts-ignore
import StudentDetails from './pages/Admin/Student/Details';
import StudentForm from './pages/Admin/Student/Form';
import FacultyList from './pages/Admin/Faculty/List';
import SupervisorReservationPage from './pages/reservation/SupervisorReservation';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import FacultyForm from './pages/Admin/Faculty/Form';
import EmployeeList from './pages/Admin/Employee/List';
import EmployeeDetails from './pages/Admin/Employee/Details';
import RequireAuth from "./auth/RequireAuth";
import Missing from "./pages/Missing";
import EmployeeForm from './pages/Admin/Employee/Form';
import SpecializationForm from './pages/Admin/Specialization/Form';
import SpecializationList from './pages/Admin/Specialization/List';
import ProgramForm from './pages/Admin/Program/Form';
import ProgramList from './pages/Admin/Program/List';
import StudyCycleList from './pages/Admin/Cycle/List';
import StudyCycleForm from './pages/Admin/Cycle/Form';
import DepartmentForm from './pages/Admin/Department/Form';
import DepartmentList from './pages/Admin/Department/List';
import StudyFieldForm from './pages/Admin/Field/Form';
import StudyFieldList from './pages/Admin/Field/List';
import React, {Suspense} from "react";
import ThesisList from './pages/Admin/Thesis/List';
import ThesisDetails from './pages/Admin/Thesis/Details';
import Reports from "./pages/Admin/Reports";
import DeadlineList from "./pages/Admin/Deadline/List";
import DeadlineForm from "./pages/Admin/Deadline/Form";
import AddThesisPageSupervisor from './pages/Theses/AddThesisSupervisor';
import ThesesSupervisor from './pages/Theses/ThesesSupervisor';
import ApproveDetails from './pages/Approver/ApproveDetails';
import ApproveList from './pages/Approver/ApproveList';
import LoadingSpinner from "./components/LoadingSpinner";
import TitleList from './pages/Admin/Title/List';
import TitleForm from "./pages/Admin/Title/Form";


export interface IAppProps {
}

export default function App(props: IAppProps) {

  return (
      <Suspense fallback={<LoadingSpinner height="90vh" />}>
        <AuthProvider>
          <BrowserRouter>
            <Navigation>
              <Routes>
                <Route path='/' element={<HomePage />} />
                <Route path='login' element={<LoginPage />} />
                <Route path="/unauthorized" element={<Unauthorized />} />

                <Route element={<RequireAuth allowedRoles={['student', 'admin']} />}>
                  <Route path='reservation' element={<ReservationPage admin={false}/>} />
                  <Route path='single-reservation' element={<SingleReservationPage />} />
                </Route>

                <Route element={<RequireAuth allowedRoles={['supervisor']} />}>
                  <Route path='my' element={<ThesesSupervisor />} />
                  <Route path='my/:id' element={<ThesesDetails />} />
                  <Route path='my/add' element={<AddThesisPageSupervisor />} />
                  <Route path='my/edit/:id' element={<AddThesisPageSupervisor />} />
                </Route>

                <Route element={<RequireAuth allowedRoles={['student', 'supervisor', 'approver', 'admin']} />}>
                  <Route path='public-theses' element={<ThesesTable />} />
                  <Route path='public-theses/:id' element={<ThesesDetails />} />
                </Route>

                <Route element={<RequireAuth allowedRoles={['approver']} />}>
                  <Route path='manage' element={<ApproveList />} />
                  <Route path='manage/:id' element={<ApproveDetails />} />
                </Route>

              <Route element={<RequireAuth allowedRoles={['admin']} />}>
                <Route path='students' element={<StudentList />} />
                <Route path='students/:id' element={<StudentDetails />} />
                <Route path='students/add' element={<StudentForm/>} />
                <Route path='students/edit/:id' element={<StudentForm/>} />
                <Route path='employees' element={<EmployeeList />} />
                <Route path='employees/:id' element={<EmployeeDetails />} />
                <Route path='employees/add' element={<EmployeeForm/>} />
                <Route path='employees/edit/:id' element={<EmployeeForm/>} />
                <Route path='theses' element={<ThesisList />} />
                <Route path='theses/:id' element={<ThesisDetails />} />
                <Route path='theses/add' element={<AddThesisPageAdmin/>} />
                <Route path='theses/edit/:id' element={<AddThesisPageAdmin/>} />
                <Route path='faculties' element={<FacultyList />} />
                <Route path='faculties/add' element={<FacultyForm />} />
                <Route path='faculties/edit/:abbr' element={<FacultyForm />} />
                <Route path='fields' element={<StudyFieldList />} />
                <Route path='fields/add' element={<StudyFieldForm />} />
                <Route path='fields/edit/:abbr' element={<StudyFieldForm />} />
                <Route path='specializations' element={<SpecializationList />} />
                <Route path='specializations/add' element={<SpecializationForm />} />
                <Route path='specializations/edit/:abbr' element={<SpecializationForm />} />
                <Route path='programs' element={<ProgramList />} />
                <Route path='programs/add' element={<ProgramForm />} />
                <Route path='programs/edit/:id' element={<ProgramForm />} />
                <Route path='cycles' element={<StudyCycleList />} />
                <Route path='cycles/add' element={<StudyCycleForm />} />
                <Route path='cycles/edit/:id' element={<StudyCycleForm />} />
                <Route path='departments' element={<DepartmentList />} />
                <Route path='departments/add' element={<DepartmentForm />} />
                <Route path='departments/edit/:id' element={<DepartmentForm />} />
                <Route path='deadlines' element={<DeadlineList />} />
                <Route path='deadlines/add' element={<DeadlineForm />} />
                <Route path='deadlines/edit/:id' element={<DeadlineForm />} />
                <Route path='titles' element={<TitleList />} />
                <Route path='titles/add' element={<TitleForm />} />
                <Route path='titles/edit/:id' element={<TitleForm />} />
                <Route path="/students/file" element={<UploadStudentFilePage />} />
                <Route path="/employees/file" element={<UplaodEmployeeFilePage />} />
                <Route path="/reports" element={<Reports />} />
                <Route path="/admin-reservation" element={<ReservationPage admin={true}/>} />
              </Route>
              <Route path="*" element={<Missing />} />
        </Routes>
          </Navigation>
        </BrowserRouter>
        <ToastContainer />
      </AuthProvider>
      </Suspense>
  );
}