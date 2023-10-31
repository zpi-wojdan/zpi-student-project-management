import * as React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import ReservationPage from './pages/reservation/Reservation';
import ThesesTable from './pages/Theses/Theses';
import ThesisDetails from './pages/Theses/ThesisDetails';
// @ts-ignore
import StudentForm from './pages/StudentForm';
import Navigation from './layout/Navigation';
import SingleReservationPage from './pages/reservation/SingleReservation';
import {AuthProvider} from "./auth/AuthProvider";
import AddThesisPage from './pages/Theses/AddThesis';
import UpdateThesisPage from './pages/Theses/UpdateThesis';
import Unauthorized from './pages/Unauthorized';
import UploadStudentFilePage from './pages/UploadingFiles/UploadStudentsFile';
import UplaodEmployeeFilePage from './pages/UploadingFiles/UploadEmployeeFile';
import StudentList from './pages/Admin/Student/List';
import StudentDetails from './pages/Admin/Student/Details';
import SupervisorReservationPage from './pages/reservation/SupervisorReservation';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';


export interface IAppProps {
}

export default function App(props: IAppProps) {

  return (
      <AuthProvider>
        <BrowserRouter>
          <Navigation>
            <Routes>
              <Route path='/' element={<HomePage />} />
              <Route path='login' element={<LoginPage />} />
              <Route path='reservation' element={<ReservationPage />} />
              <Route path='single-reservation' element={<SingleReservationPage />} />
              <Route path='supervisor-reservation' element={<SupervisorReservationPage />} />
              <Route path='theses' element={<ThesesTable />} />
              <Route path='theses/:id' element={<ThesisDetails />} />
              <Route path='students' element={<StudentList />} />
              <Route path='students/:mail' element={<StudentDetails />} />
              <Route path='students/add' element={<StudentForm/>} />
              <Route path='students/edit/:mail' element={<StudentForm/>} />
              <Route path='employees' element={<HomePage />} />
              <Route path='faculties' element={<HomePage />} />
              <Route path='fields' element={<HomePage />} />
              <Route path='specializations' element={<HomePage />} />
              <Route path='programs' element={<HomePage />} />
              <Route path='cycles' element={<HomePage />} />
              <Route path='departments' element={<HomePage />} />
              <Route path='my' element={<ReservationPage />} />
              <Route path='addthesis' element={<AddThesisPage role={'admin'} mail={'john.doe@pwr.edu.pl'} />} />
              <Route path='updatethesis/:thesisId' element={<UpdateThesisPage role={'employee'} mail={'john.doe@pwr.edu.pl'} />} />
              <Route path="/unauthorized" element={<Unauthorized />} />
              <Route path="/file/student" element={<UploadStudentFilePage />} />
              <Route path="/file/employee" element={<UplaodEmployeeFilePage />} />
        </Routes>
          </Navigation>
        </BrowserRouter>
        <ToastContainer />
      </AuthProvider>
  );
}