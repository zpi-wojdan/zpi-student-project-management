import * as React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';

export interface IAppProps {
}

export default function App(props: IAppProps) {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<HomePage />}/>
        <Route path='login' element={<LoginPage />}/>
      </Routes>
    </BrowserRouter>
  );
}