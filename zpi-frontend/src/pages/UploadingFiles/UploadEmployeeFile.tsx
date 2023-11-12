import { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import {useNavigate} from "react-router-dom";
import { InvalidEmployeeData } from '../../models/ImportedData';
import {useTranslation} from "react-i18next";
import axios from 'axios';
import Cookies from 'js-cookie';

function UplaodEmployeeFilePage() {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [buttonDisabled, setButtonDisabled] = useState(true);
  const [uploadError, setUploadError] = useState<string | null>(null);
  const [duplicateFilesError, setDuplicateFilesError] = useState<string | null>(null);
  const [duplicateErrorMessageVisible, setDuplicateErrorMessageVisible] = useState(false);
  const [uploadErrorMessageVisible, setUploadErrorMessageVisible] = useState(false);

  const [invalidJsonData, setInvalidJsonData] = useState<InvalidEmployeeData | null>(null);
  const [recordsSaved, setRecordsSaved] = useState<string | null>(null);
  const [sentData, setSentData] = useState(false);

  const [databaseRepetitions, setDatabaseRepetitions] = useState(false);
  const [invalidIndicesOpen, setInvalidIndicesOpen] = useState(false);
  const [invalidAcademicTitlesOpen, setInvalidAcademicTitlesOpen] = useState(false);
  const [invalidSurnamesOpen, setInvalidSurnamesOpen] = useState(false);
  const [invalidNamesOpen, setInvalidNamesOpen] = useState(false);
  const [invalidUnitsOpen, setInvalidUnitsOpen] = useState(false);
  const [invalidSubunitsOpen, setInvalidSubunitsOpen] = useState(false);
  const [invalidPositionsOpen, setInvalidPositionsOpen] = useState(false);
  const [invalidPhoneNumbersOpen, setInvalidPhoneNumbersOpen] = useState(false);
  const [invalidEmailsOpen, setInvalidEmailsOpen] = useState(false);
  const [invalidDataOpen, setInvalidDataOpen] = useState(false);
  const [recordsSavedOpen, setRecordsSavedOpen] = useState(false);

  const invalidDataList = [
    {
      title: t('uploadFiles.databaseRepetitions'),
      data: invalidJsonData?.database_repetitions,
      isOpen: databaseRepetitions,
      toggleOpen: () => setDatabaseRepetitions(!databaseRepetitions)
    },
    {
      title: t('uploadFiles.wrongIndexes'),
      data: invalidJsonData?.invalid_indices,
      isOpen: invalidIndicesOpen,
      toggleOpen: () => setInvalidIndicesOpen(!invalidIndicesOpen)
    },
    {
      title: t('uploadFiles.wrongAcademicTitles'),
      data: invalidJsonData?.invalid_academic_titles,
      isOpen: invalidAcademicTitlesOpen,
      toggleOpen: () => setInvalidAcademicTitlesOpen(!invalidAcademicTitlesOpen)
    },
    {
      title: t('uploadFiles.wrongSurnames'),
      data: invalidJsonData?.invalid_surnames,
      isOpen: invalidSurnamesOpen,
      toggleOpen: () => setInvalidSurnamesOpen(!invalidSurnamesOpen)
    },
    {
      title: t('uploadFiles.wrongNames'),
      data: invalidJsonData?.invalid_names,
      isOpen: invalidNamesOpen,
      toggleOpen: () => setInvalidNamesOpen(!invalidNamesOpen)
    },
    {
      title: t('uploadFiles.wrongUnits'),
      data: invalidJsonData?.invalid_units,
      isOpen: invalidUnitsOpen,
      toggleOpen: () => setInvalidUnitsOpen(!invalidUnitsOpen)
    },
    {
      title: t('uploadFiles.wrongSubunits'),
      data: invalidJsonData?.invalid_subunits,
      isOpen: invalidSubunitsOpen,
      toggleOpen: () => setInvalidSubunitsOpen(!invalidSubunitsOpen)
    },
    {
      title: t('uploadFiles.wrongPositions'),
      data: invalidJsonData?.invalid_positions,
      isOpen: invalidPositionsOpen,
      toggleOpen: () => setInvalidPositionsOpen(!invalidPositionsOpen)
    },
    {
      title: t('uploadFiles.wrongPhoneNumbers'),
      data: invalidJsonData?.invalid_phone_numbers,
      isOpen: invalidPhoneNumbersOpen,
      toggleOpen: () => setInvalidPhoneNumbersOpen(!invalidPhoneNumbersOpen)
    },
    {
      title: t('uploadFiles.wrongEmails'),
      data: invalidJsonData?.invalid_emails,
      isOpen: invalidEmailsOpen,
      toggleOpen: () => setInvalidEmailsOpen(!invalidEmailsOpen)
    },
    {
      title: t('uploadFiles.invalidData'),
      data: invalidJsonData?.invalid_data,
      isOpen: invalidDataOpen,
      toggleOpen: () => setInvalidDataOpen(!invalidDataOpen)
    },
  ];


  setTimeout(() => {
    setDuplicateErrorMessageVisible(false);
  }, 20000);

  setTimeout(() => {
    setUploadErrorMessageVisible(false);
  }, 20000);

  const onDrop = useCallback((acceptedFiles: File[]) => {
    setDuplicateFilesError(null);
    const newFiles = acceptedFiles.filter(
      (file) => !selectedFiles.some((existingFile) => (existingFile.name === file.name))
    );
    if (newFiles.length != acceptedFiles.length){
      setDuplicateFilesError(t('uploadFiles.duplicatedFileError'));
      setDuplicateErrorMessageVisible(true);
    }
    setSelectedFiles([...selectedFiles, ...newFiles]);
    setButtonDisabled(false);
  }, [selectedFiles]);

  const deleteFile = (fileToDelete: File) => {
    const updatedFiles = selectedFiles.filter((file) => file !== fileToDelete);
    setSelectedFiles(updatedFiles);
    if (updatedFiles.length === 0) {
      setButtonDisabled(true);
    }
  };

  const { getRootProps, getInputProps } = useDropzone({ onDrop });

  const handleUpload = () => {
    setUploadError(null);
    selectedFiles.forEach((file) => {
      var size = +((file.size / (1024*1024)).toFixed(2))
      if (size > 5){
        const errorMessage = t('uploadFiles.tooBigFileError', {fileName: file.name, size: size});
        console.log(errorMessage);
        setUploadError(errorMessage);
        setUploadErrorMessageVisible(true);
        return;
      }
      const formData = new FormData();
      formData.append('file', file);

      axios
        .post('http://localhost:8080/file/employee', formData, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            },
        })
        .then((response) => {
          console.log('Przesłano plik:', response.data.message);
          const invalidData = JSON.parse(response.data.invalidData);
          const recordsSavedCount = response.data.saved_records;

          setInvalidJsonData(invalidData);
          setRecordsSaved(recordsSavedCount);
          setSentData(true);
        })
        .catch((error) => {
          setUploadError(t('uploadFiles.filesNotSentError'));
          setUploadErrorMessageVisible(true);
          setSentData(false);
          console.error('Nie udało się przesłać plików', error);
          if (error.response.status === 401 || error.response.status === 403) {
            setAuth({ ...auth, reasonOfLogout: 'token_expired' });
            handleSignOut(navigate);
          }
        });
    });
    setSelectedFiles([]);
    setButtonDisabled(true);
  };

  let keyCounter = 0;

  return (
    <div className="container d-flex justify-content-center mt-5 mb-5">
      <div
        className="border p-4 rounded shadow-lg"
        style={{
          width: '80%',
          maxWidth: '100%',
          height: '70%',
          maxHeight: '100%',
          overflowX: 'hidden',
          overflowY: 'hidden',
          display: 'flex',
          flexDirection: 'column'
        }}>

        <div>
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h2>{t('uploadFiles.attach')}</h2>
            <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
              &larr; {t('general.management.goBack')}
            </button>
          </div>
          <div {...getRootProps()} className="dropzone">
            <input {...getInputProps()} />
            <p>{t('uploadFiles.instruction')}</p>
          </div>
          {duplicateFilesError && duplicateErrorMessageVisible && (
            <div className="alert alert-danger mt-3" role="alert">
              {duplicateFilesError}
            </div>
          )}
          {selectedFiles.length > 0 && (
            <section style={{maxHeight: '40%', overflow: 'auto'}}>
              <h4>{t('uploadFiles.chosenFiles')}:</h4>
              <ul className="list-group mb-3" style={{ flexWrap: 'wrap', overflow: 'auto' }}>
                {selectedFiles.map((file, index) => (
                  <li key={`${index}-${keyCounter++}`} className="list-group-item d-flex justify-content-between align-items-center mb-2 border">
                    <span style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {file.name}
                    </span>
                    <button
                      className="btn btn-danger btn-sm custom-pwr-button"
                      onClick={() => deleteFile(file)}
                    >
                        {t('general.management.delete')}
                    </button>
                  </li>
                ))}
              </ul>
            </section>
          )}

          {uploadError && uploadErrorMessageVisible && (
            <div className="alert alert-danger" role="alert">
              {uploadError}
            </div>
          )}

          {recordsSavedOpen && (
            <div className="alert alert-success" role="alert">
              {t('uploadFiles.recordsSaved')} {recordsSaved}
            </div>
          )}

          <button onClick={handleUpload} disabled={buttonDisabled} className="btn btn-primary mt-2 custom-pwr-button">
              {t('uploadFiles.sendFiles')}
          </button>
        </div>

      {sentData && (
        <div
          className="container d-flex justify-content-center mt-5"
        >
          <div>
          {recordsSaved && (
            <p>
              ssssssssss {recordsSaved}
            </p>
          )}
          </div>

        <div
          className="border p-4 rounded shadow-lg"
          style={{
            width: '90%',
            maxWidth: '100%',
            height: '60%',
            maxHeight: '100%',
            overflowX: 'hidden',
            overflowY: 'hidden',
            marginBottom: '10px',
            display: 'block'
            }}>
          <h4>{t('general.management.wrongData')}:</h4>
          <div style={{ overflow: 'auto', height: '100%', maxHeight: '100%' }}>
            <ul className="list-group">
              {invalidDataList.map((item, index) => (
                item.data && item.data.length > 0 ? (
                  <li className="list-group-item mb-2 border" key={`${index}-${keyCounter++}`}>
                    <div>
                    <div onClick={item.toggleOpen}>
                      <div className="d-flex justify-content-between align-items-center">
                        <span>{item.title}</span>
                        <span>{item.data.length}</span>
                      </div>
                    </div>
                    <div className={`collapse ${item.isOpen ? 'show' : ''}`} style={{ height: '100%', maxHeight: '100%', overflowX: 'auto', overflowY: 'hidden'}}>
                      <table className="custom-table">
                        <thead>
                          <tr>
                            <th style={{ width: '8%' }}>{t('general.title')}</th>
                            <th style={{ width: '23%' }}>{t('general.people.surname')}</th>
                            <th style={{ width: '23%' }}>{t('general.people.name')}</th>
                            <th style={{ width: '23%' }}>{t('uploadFiles.unit')}</th>
                            <th style={{ width: '23%' }}>{t('uploadFiles.subunit')}</th>
                          </tr>
                        </thead>
                        <tbody>
                          {item.data?.map((employee, index) => (
                            <tr key={`${employee.mail}-${keyCounter++}`}>
                              <td>{employee.mail}</td>
                              <td>{employee.surname}</td>
                              <td>{employee.name}</td>
                              <td>{employee.faculty}</td>
                              <td>{employee.department}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                    </div>
                  </li>
                ) : null
              ))}
            </ul>

          </div>
        </div>
      </div>
      )}
    </div>
  </div>

  );
}

export default UplaodEmployeeFilePage;
