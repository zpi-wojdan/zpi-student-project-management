import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import { Thesis } from '../models/Models';

const ThesisDetails: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const { id } = useParams<{ id: string }>();
  const thesis = location.state?.thesis as Thesis;


  return (
    <>
      <div className='row d-flex justify-content-between'>
        <button type="button" className="col-sm-2 btn btn-secondary m-3" onClick={() => navigate(-1)}>
          &larr; Powrót
        </button>
        <button type="button" className="col-sm-2 btn btn-primary m-3" onClick={() => navigate('/reservation', {state: {thesis : thesis}})}>
          Zarezerwuj
        </button>
      </div>
      <div className='thesis-details'>
        {thesis ? (
          <div>
            <p className="bold">Temat po polsku:</p>
            <p>{thesis.namePL}</p>
            <p className="bold">Temat po angielsku:</p>
            <p>{thesis.nameEN}</p>
            <p className="bold">Opis:</p>
            <p>{thesis.description}</p>
            <p><span className="bold">Promotor:</span> <span>{thesis.supervisor.title + " " + thesis.supervisor.name + " " + thesis.supervisor.surname}</span></p>
            <p><span className="bold">Wydział:</span> <span>{thesis.faculty}</span></p>
            <p><span className="bold">Kierunek:</span> <span>{thesis.field}</span></p>
            <p><span className="bold">Cykl kształcenia:</span> <span>{thesis.eduCycle}</span></p>
            <div>
              <p><span className="bold">Zapisani:</span> <span>{thesis.occupied + "/" + thesis.num_people}</span></p>
              {/* Tabela zapisanych */}
            </div>
          </div>
        ) : (
          <p>Loading... {id}</p>
        )}
      </div>
    </>
  );
};

export default ThesisDetails;
