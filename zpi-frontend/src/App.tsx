import * as React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import ThesisTable from './pages/Theses';
import ReservationPage from './pages/Reservation';
import Navigation from './layout/Naviagation';


export interface IAppProps {
}

export default function App(props: IAppProps) {
  return (
    <BrowserRouter>
      <Navigation>
        <Routes>
          <Route path='/' element={<HomePage />} />
          <Route path='login' element={<LoginPage />} />
          <Route path='reservation' element={<ReservationPage />} />
          <Route path='topics' element={<ReservationPage />} />
          <Route path='theses' element={<ThesisTable />} />
          <Route path='my' element={<ReservationPage />} />
        </Routes>
      </Navigation>
    </BrowserRouter>
  );
}