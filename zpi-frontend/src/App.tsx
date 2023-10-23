import * as React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import ReservationPage from './pages/Reservation';
import ThesesTable from './pages/Theses';
import ThesisDetails from './pages/ThesisDetails';
import Navigation from './layout/Naviagation'
import { Thesis } from './models/Models';
import AddThesisPage from './pages/AddThesis';
import UpdateThesisPage from './pages/UpdateThesis';
import Unauthorized from './pages/Unauthorized';

export interface IAppProps {
}

export default function App(props: IAppProps) {
  return (
    <BrowserRouter>
      <Navigation>
        <Routes>
          <Route path='/*' element={<HomePage />} />
          <Route path='login' element={<LoginPage />} />
          <Route path='reservation' element={<ReservationPage />} />
          <Route path='theses' element={<ThesesTable />} />
          <Route path='theses/:id' element={<ThesisDetails />} />
          <Route path='my' element={<ReservationPage />} />
          <Route path='addthesis' element={<AddThesisPage role={'admin'} mail={'john.doe@pwr.edu.pl'} />} />
          <Route path='updatethesis/:thesisId' element={<UpdateThesisPage role={'employee'} mail={'john.doe@pwr.edu.pl'} />} />
          <Route path="/unauthorized" element={<Unauthorized />} />
        </Routes>
      </Navigation>
    </BrowserRouter>
  );
}