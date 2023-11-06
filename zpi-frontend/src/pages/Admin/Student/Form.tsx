import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Axios from 'axios';
import { StudentDTO } from '../../../models/Student';
import { Program } from '../../../models/Program';
import { toast } from 'react-toastify';
import { StudyCycle } from '../../../models/StydyCycle';
import Cookies from 'js-cookie';
import { StudentProgramCycle, StudentProgramCycleDTO } from '../../../models/StudentProgramCycle';
import handleSignOut from "../../../auth/Logout";
import useAuth from "../../../auth/useAuth";

const StudentForm: React.FC = () => {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const [formData, setFormData] = useState<StudentDTO>({
    mail: '',
    name: '',
    surname: '',
    index: '',
    status: '',
    programsCycles: [{ cycleId: -1, programId: -1 }],
  });

  const navigate = useNavigate();
  const location = useLocation();
  const student = location.state?.student;
  const [errors, setErrors] = useState<Record<string, string>>({});
  const statusOptions = ['STU', 'Inny'];

  useEffect(() => {
    if (student) {
      setFormData((prevFormData) => {
        return {
          ...prevFormData,
          mail: student.mail,
          name: student.name,
          surname: student.surname,
          index: student.index,
          status: student.status,
          programsCycles: student.studentProgramCycles.map((programCycle:StudentProgramCycle) => ({
            cycleId: programCycle.cycle.id,
            programId: programCycle.program.id,
          })),
        };
      });
    }
  }, [student]);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (validateForm()) {
      if (student) {
        Axios.put(`http://localhost:8080/student/${formData.mail}`, formData, {
          headers: {
            'Authorization': `Bearer ${Cookies.get('google_token')}`
          }
        })
          .then(() => {
            navigate("/students");
            toast.success("Student został zaktualizowany");
          })
          .catch((error) => {
            console.error(error);
            if (error.response.status === 401 || error.response.status === 403) {
              setAuth({ ...auth, reasonOfLogout: 'token_expired' });
              handleSignOut(navigate);
            }
            navigate("/students");
            toast.error("Student nie został zaktualizowany");
          });
      } else {
        Axios.post('http://localhost:8080/student', formData, {
          headers: {
            'Authorization': `Bearer ${Cookies.get('google_token')}`
          }
        })
          .then(() => {
            navigate("/students");
            toast.success("Student został dodany");
          })
          .catch((error) => {
            if (error.response && error.response.status === 409) {
              const newErrors: Record<string, string> = {};
              newErrors.index = 'Podany indeks już istnieje!';
              setErrors(newErrors);
            } else {
              console.error(error);
              if (error.response.status === 401 || error.response.status === 403) {
                setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                handleSignOut(navigate);
              }
              navigate("/students");
              toast.error("Student nie został dodany");
            }
          })
      }
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    let isValid = true;
    const errorRequireText = 'Pole jest wymagane.';

    if (!formData.name) {
      newErrors.name = errorRequireText;
      isValid = false;
    }

    if (!formData.surname) {
      newErrors.surname = errorRequireText;
      isValid = false;
    }

    if (!formData.index) {
      newErrors.index = errorRequireText;
      isValid = false;
    } else if (!/^\d{6}$/.test(formData.index)) {
      newErrors.index = 'Indeks musi składać się z 6 cyfr.';
      isValid = false;
    }

    if (formData.programsCycles.some((programCycle) => programCycle.cycleId === -1 || programCycle.programId === -1)) {
      newErrors.studentProgramCycles = 'Wybierz cykl i program dla wszystkich wpisów.';
      isValid = false;
    }

    if (hasDuplicateProgramCycle(formData.programsCycles)) {
      newErrors.studentProgramCycles = 'Lista programów posiada duplikaty..';
      isValid = false;
    }

    if (!formData.status) {
      newErrors.status = errorRequireText;
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  const hasDuplicateProgramCycle = (programCycles: StudentProgramCycleDTO[]) => {
    const uniquePairs = new Set();
    for (const programCycle of programCycles) {
      const pair = JSON.stringify(programCycle);
      if (uniquePairs.has(pair)) {
        return true;
      }
      uniquePairs.add(pair);
    }
    return false;
  };

  const [availableCycles, setAvailableCycles] = useState<StudyCycle[]>([]);
  const [availablePrograms, setAvailablePrograms] = useState<Program[]>([]);

  useEffect(() => {
    Axios.get('http://localhost:8080/studycycle', {
      headers: {
        'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
    })
      .then((response) => {
        setAvailableCycles(response.data);
      })
      .catch((error) => 
      {
        console.error(error);
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  useEffect(() => {
    Axios.get('http://localhost:8080/program', {
      headers: {
        'Authorization': `Bearer ${Cookies.get('google_token')}`
      }
    })
      .then((response) => {
        setAvailablePrograms(response.data);
      })
      .catch((error) => {
        console.error(error)
        if (error.response.status === 401 || error.response.status === 403) {
          setAuth({ ...auth, reasonOfLogout: 'token_expired' });
          handleSignOut(navigate);
        }
      });
  }, []);

  const handleCycleChange = (index: number, selectedCycleId: number) => {
    const updatedStudentProgramCycles = [...formData.programsCycles];
    updatedStudentProgramCycles[index].cycleId = selectedCycleId;
    updatedStudentProgramCycles[index].programId = -1;

    setFormData({ ...formData, programsCycles: updatedStudentProgramCycles });
  };

  const handleProgramChange = (index: number, selectedProgramId: number) => {
    const updatedStudentProgramCycles = [...formData.programsCycles];
    updatedStudentProgramCycles[index].programId = selectedProgramId;

    setFormData({ ...formData, programsCycles: updatedStudentProgramCycles });
  };

  const handleAddNext = () => {
    const newStudentProgramCycles = [...formData.programsCycles];
    newStudentProgramCycles.push({ cycleId: -1, programId: -1 });
    setFormData({ ...formData, programsCycles: newStudentProgramCycles });
  };

  const handleRemove = (index: number) => {
    const updatedStudentProgramCycles = [...formData.programsCycles];
    updatedStudentProgramCycles.splice(index, 1);
    setFormData({ ...formData, programsCycles: updatedStudentProgramCycles });
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="form">
        <div className='d-flex justify-content-begin align-items-center mb-3'>
          <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
            &larr; Powrót
          </button>
          <button type="submit" className="custom-button">
            {student ? 'Zapisz' : 'Dodaj'}
          </button>
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="name">
            Imię:
          </label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className="form-control"
          />
          {errors.name && <div className="text-danger">{errors.name}</div>}
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="surname">
            Nazwisko:
          </label>
          <input
            type="text"
            id="surname"
            name="surname"
            value={formData.surname}
            onChange={(e) => setFormData({ ...formData, surname: e.target.value })}
            className="form-control"
          />
          {errors.surname && <div className="text-danger">{errors.surname}</div>}
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="index">
            Indeks:
          </label>
          <input
            type="text"
            id="index"
            name="index"
            value={formData.index}
            onChange={(e) => setFormData({ ...formData, index: e.target.value })}
            className="form-control"
          />
          {errors.index && <div className="text-danger">{errors.index}</div>}
        </div>
        <div className="mb-3">
          <label className="bold" htmlFor="status">
            Status:
          </label>
          <select
            id="status"
            name="status"
            value={formData.status}
            onChange={(e) => setFormData({ ...formData, status: e.target.value })}
            className="form-control"
          >
            <option value="">Wybierz</option>
            {statusOptions.map((status, index) => (
              <option key={index} value={status}>
                {status}
              </option>
            ))}
          </select>
          {errors.status && <div className="text-danger">{errors.status}</div>}
        </div>
        <div>
          <label className="bold">Programy:</label>
          <ul>
            {formData.programsCycles.map((programCycle, index) => (
              <li key={index}>
                <div className="mb-3">
                  <label className="bold" htmlFor={`cycle${index}`}>
                    Cykl:
                  </label>
                  <select
                    id={`cycle${index}`}
                    name={`cycle${index}`}
                    value={programCycle.cycleId > -1 ? programCycle.cycleId : ""}
                    onChange={(e) => {
                      const selectedCycleId = parseInt(e.target.value, 10);
                      handleCycleChange(index, selectedCycleId);
                    }}
                    className="form-control"
                  >
                    <option value={-1}>Wybierz</option>
                    {availableCycles.map((c, cIndex) => (
                      <option key={cIndex} value={c.id}>
                        {c.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="mb-3">
                  <label className="bold" htmlFor={`program${index}`}>
                    Program:
                  </label>
                  <select
                    id={`program${index}`}
                    name={`program${index}`}
                    value={programCycle.programId}
                    onChange={(e) => {
                      const selectedProgramId = parseInt(e.target.value, 10);
                      handleProgramChange(index, selectedProgramId);
                    }}
                    className="form-control"
                    disabled={programCycle.cycleId == -1}
                  >
                    <option value={-1}>Wybierz</option>
                    {programCycle.cycleId !== -1 &&
                      availablePrograms
                        .filter((p) => p.studyCycles.some((c) => c.id === programCycle.cycleId))
                        .map((program, pIndex) => (
                          <option key={pIndex} value={program.id}>
                            {program.name}
                          </option>
                        ))}
                  </select>
                </div>
                {formData.programsCycles.length > 1 && (
                  <button
                    type="button"
                    className="custom-button another-color"
                    onClick={() => handleRemove(index)}
                  >
                    Usuń
                  </button>
                )}
              </li>
            ))}
            {errors.studentProgramCycles && <div className="text-danger">{errors.studentProgramCycles}</div>}
            <li>
              <button
                type="button"
                className="custom-button"
                onClick={handleAddNext}
              >
                Dodaj następny
              </button>
            </li>
          </ul>
        </div>
      </form>
    </div>
  );
}

export default StudentForm;
