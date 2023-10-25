import * as React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import ReservationPage from './pages/Reservation';
import ThesesTable from './pages/Theses';
import ThesisDetails from './pages/ThesisDetails';
// @ts-ignore
import Navigation from './layout/Navigation';
import SingleReservationPage from './pages/SingleReservation';
import {AuthProvider} from "./auth/AuthProvider";
import AddThesisPage from './pages/AddThesis';
import UpdateThesisPage from './pages/UpdateThesis';
import Unauthorized from './pages/Unauthorized';

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
              <Route path='theses' element={<ThesesTable />} />
              <Route path='theses/:id' element={<ThesisDetails />} />
              <Route path='students' element={<HomePage />} />
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
            </Routes>
          </Navigation>
        </BrowserRouter>
      </AuthProvider>
  );
}