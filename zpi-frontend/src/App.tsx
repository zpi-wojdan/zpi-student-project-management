import * as React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import ReservationPage from './pages/Reservation';
import Navigation from './layout/Naviagation'
import StudentTable, { Student } from './components/StudentsTable';

export interface IAppProps {
}

const students: Student[] = [
  { email: 'john.doe@example.com', name: 'John', surname: 'Doe', collegeIndex: '123456' },
  { email: 'jane.smith@example.com', name: 'Jane', surname: 'Smith', collegeIndex: '567890' },
]

export default function App(props: IAppProps) {
  return (
    <BrowserRouter>
      <Navigation>
        <Routes>
          <Route path='/' element={<HomePage />} />
          <Route path='login' element={<LoginPage />} />
          <Route path='reservation' element={<ReservationPage />} />
          <Route path='topics' element={<ReservationPage />} />
          <Route path='my' element={<StudentTable students={students}/>} />
        </Routes>
      </Navigation>
    </BrowserRouter>
  );
}