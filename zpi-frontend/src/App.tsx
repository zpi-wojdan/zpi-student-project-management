import * as React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import ReservationPage from './pages/reservation/Reservation';
import ThesesTable from './pages/Theses/Theses';
import ThesisDetails from './pages/Theses/ThesisDetails';
// @ts-ignore
import Navigation from './layout/Navigation';
import SingleReservationPage from './pages/reservation/SingleReservation';
import {AuthProvider} from "./auth/AuthProvider";
import AddThesisPage from './pages/Theses/AddThesis';
import UpdateThesisPage from './pages/Theses/UpdateThesis';
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
import {Suspense} from "react";


export interface IAppProps {
}

export default function App(props: IAppProps) {

  return (
      <Suspense fallback="loading">
        <AuthProvider>
          <BrowserRouter>
            <Navigation>
              <Routes>
                <Route path='/' element={<HomePage />} />
                <Route path='login' element={<LoginPage />} />
                <Route path="/unauthorized" element={<Unauthorized />} />

                <Route element={<RequireAuth allowedRoles={['student']} />}>
                  <Route path='reservation' element={<ReservationPage />} />
                  <Route path='single-reservation' element={<SingleReservationPage />} />
                </Route>

                <Route element={<RequireAuth allowedRoles={['supervisor']} />}>
                  <Route path='supervisor-reservation' element={<SupervisorReservationPage />} />
                  <Route path='my' element={<ReservationPage />} />
                  <Route path='addthesis' element={<AddThesisPage role={'admin'} mail={'john.doe@pwr.edu.pl'} />} />
                  <Route path='updatethesis/:thesisId' element={<UpdateThesisPage role={'employee'}
                                                                                  mail={'john.doe@pwr.edu.pl'} />} />
                </Route>

                <Route element={<RequireAuth allowedRoles={['student', 'supervisor', 'approver', 'admin']} />}>
                  <Route path='theses' element={<ThesesTable />} />
                  <Route path='theses/:id' element={<ThesisDetails />} />
                </Route>

              <Route element={<RequireAuth allowedRoles={['admin']} />}>
                <Route path='students' element={<StudentList />} />
                <Route path='students/:mail' element={<StudentDetails />} />
                <Route path='students/add' element={<StudentForm/>} />
                <Route path='students/edit/:mail' element={<StudentForm/>} />
                <Route path='employees' element={<EmployeeList />} />
                <Route path='employees/:mail' element={<EmployeeDetails />} />
                <Route path='employees/add' element={<EmployeeForm/>} />
                <Route path='employees/edit/:mail' element={<EmployeeForm/>} />
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
                <Route path="/file/student" element={<UploadStudentFilePage />} />
                <Route path="/file/employee" element={<UplaodEmployeeFilePage />} />
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