import * as React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import ReservationPage from './pages/Reservation';
import ThesesTable from './pages/Theses';
import ThesisDetails from './pages/ThesisDetails';
// @ts-ignore
import Navigation from './layout/Navigation';
import { AuthProvider } from "./auth/AuthProvider";
import SingleReservationPage from './pages/SingleReservation';

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
            <Route path='my' element={<ReservationPage />} />
          </Routes>
        </Navigation>
      </BrowserRouter>
    </AuthProvider>
  );
}