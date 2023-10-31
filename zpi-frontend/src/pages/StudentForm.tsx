import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import { StudentDTO } from '../models/Student';
import { Program } from '../models/Program';

const StudentForm: React.FC = () => {
  const [formData, setFormData] = useState<StudentDTO>({
    mail: '',
    name: '',
    surname: '',
    index: '',
    status: '',
    studentProgramCycles: [],
  });

  const navigate = useNavigate();
  const location = useLocation();
  const student = location.state?.student as StudentDTO;

  useEffect(() => {
    if (student) {
      setFormData(student);
    }
  }, [student]);

  const [availablePrograms, setAvailablePrograms] = useState<Program[]>([]);

  useEffect(() => {
    Axios.get('http://localhost:8080/program')
      .then((response) => {
        setAvailablePrograms(response.data);
      })
      .catch((error) => console.error(error));
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleProgramChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedProgramId = parseInt(e.target.value, 10);
    // Zaktualizuj dane studenta na podstawie wybranego programu
  };

  const createMailFromIndex = (index: string) => {
    return `${index}@student.pwr.edu.pl`;
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    //if (validateForm()) {
      if (formData.mail) {
        Axios.put(`http://localhost:8080/student/${formData.mail}`, formData)
          .then((response) => {
            console.log('Student zaktualizowany:', response.data);
          })
          .catch((error) => console.error(error));
      } else {
        formData.mail = createMailFromIndex(formData.index);
        Axios.post('http://localhost:8080/student', formData)
          .then((response) => {
            console.log('Nowy student dodany:', response.data);
          })
          .catch((error) => console.error(error));
      }
   // }
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="form">
        <div className='d-flex justify-content-begin  align-items-center mb-3'>
          <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
            &larr; Powrót
          </button>
          <button type="submit" className="custom-button">
            {formData.mail ? 'Zapisz' : 'Dodaj'}
          </button>
        </div>
        <div className="form-group">
          <label htmlFor="name">Name:</label>
          <input
            type="text"
            name="name"
            id="name"
            value={formData.name}
            onChange={handleInputChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="surname">Surname:</label>
          <input
            type="text"
            name="surname"
            id="surname"
            value={formData.surname}
            onChange={handleInputChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="index">Index:</label>
          <input
            type="text"
            name="index"
            id="index"
            value={formData.index}
            onChange={handleInputChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="status">Status:</label>
          <input
            type="text"
            name="status"
            id="status"
            value={formData.status}
            onChange={handleInputChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="program">Select a Program:</label>
          <select id="program" onChange={handleProgramChange}>
            <option value="">Select a Program</option>
            {availablePrograms.map((program) => (
              <option key={program.id} value={program.id}>
                {program.name}
              </option>
            ))}
          </select>
        </div>
      </form>
    </div>
  );
}

export default StudentForm;
