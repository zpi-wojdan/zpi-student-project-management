import * as React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import ReservationPage from './pages/Reservation';
import ThesesTable from './pages/Theses';
import ThesisDetails from './pages/ThesisDetails';
import Navigation from './layout/Navigation'
import { Thesis } from './models/Models';
import {AuthProvider} from "./auth/AuthProvider";

export interface IAppProps {
}

const exampleThesis: Thesis = {
  thesis_id: 1,
  name_pl: "Praca Dyplomowa w Języku Polskim",
  name_ang: "Thesis in English",
  description: "This is an example description of a thesis.",
  num_people: 4,
  status: "In Progress",
  faculty: "Faculty of Computer Science",
  field: "Computer Science",
  eduCycle: "Master's",
  supervisor: "Dr. John Doe",
  leader: "Prof. Jane Smith"
};


export default function App(props: IAppProps) {

  return (
      <AuthProvider>
        <BrowserRouter>
          <Navigation>
            <Routes>
              <Route path='/' element={<HomePage />} />
              <Route path='login' element={<LoginPage />} />
              <Route path='reservation' element={<ReservationPage />} />
              <Route path='topics' element={<ReservationPage thesis={exampleThesis}/>} />
              <Route path='theses' element={<ThesesTable/>} />
              <Route path='theses/:id' element={<ThesisDetails/>} />
              <Route path='my' element={<ReservationPage />} />
            </Routes>
          </Navigation>
        </BrowserRouter>
      </AuthProvider>
  );
}