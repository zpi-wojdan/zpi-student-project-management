import * as React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import ReservationPage from './pages/Reservation';
import ThesesTable from './pages/Theses/Theses';
import ThesisDetails from './pages/Theses/ThesisDetails';
// @ts-ignore
import Navigation from './layout/Navigation';
import {AuthProvider} from "./auth/AuthProvider";
import { Thesis } from './models/Models';
import AddThesisPage from './pages/Theses/AddThesis';
import UpdateThesisPage from './pages/Theses/UpdateThesis';
import Unauthorized from './pages/Unauthorized';
import UploadStudentFilePage from './pages/UploadingFiles/UploadStudentsFile';
import UplaodEmployeeFilePage from './pages/UploadingFiles/UploadEmployeeFile';

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
              <Route path='theses' element={<ThesesTable />} />
              <Route path='theses/:id' element={<ThesisDetails />} />
              <Route path='my' element={<ReservationPage />} />
              <Route path='addthesis' element={<AddThesisPage role={'admin'} mail={'john.doe@pwr.edu.pl'} />} />
              <Route path='updatethesis/:thesisId' element={<UpdateThesisPage role={'employee'} mail={'john.doe@pwr.edu.pl'} />} />
              <Route path="/unauthorized" element={<Unauthorized />} />
              <Route path="/file/student" element={<UploadStudentFilePage />} />
              <Route path="/file/employee" element={<UplaodEmployeeFilePage />} />
            </Routes>
          </Navigation>
        </BrowserRouter>
      </AuthProvider>
  );
}