import { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import {useNavigate} from "react-router-dom";
import { ImportedStudent, InvalidStudentData } from '../../models/ImportedData';

import {useTranslation} from "react-i18next";
import api from '../../utils/api';

function UploadStudentFilePage() {
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

  const [invalidJsonData, setInvalidJsonData] = useState<InvalidStudentData | null>(null);

  const [recordsSaved, setRecordsSaved] = useState<number | null>(0);
  const [sentData, setSentData] = useState(false);

  const [databaseRepetitions, setDatabaseRepetitions] = useState(false);
  const [invalidIndicesOpen, setInvalidIndicesOpen] = useState(false);
  const [invalidSurnamesOpen, setInvalidSurnamesOpen] = useState(false);
  const [invalidNamesOpen, setInvalidNamesOpen] = useState(false);
  const [invalidProgramsOpen, setInvalidProgramsOpen] = useState(false);
  const [invalidCyclesOpen, setInvalidCyclesOpen] = useState(false);
  const [invalidStatusesOpen, setInvalidStatusesOpen] = useState(false);
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
      title: t('uploadFiles.wrongPrograms'),
      data: invalidJsonData?.invalid_programs,
      isOpen: invalidProgramsOpen,
      toggleOpen: () => setInvalidProgramsOpen(!invalidProgramsOpen)
    },
    {
      title: t('uploadFiles.wrongCycles'),
      data: invalidJsonData?.invalid_cycles,
      isOpen: invalidCyclesOpen,
      toggleOpen: () => setInvalidCyclesOpen(!invalidCyclesOpen)
    },
    {
      title: t('uploadFiles.wrongStatuses'),
      data: invalidJsonData?.invalid_statuses,
      isOpen: invalidStatusesOpen,
      toggleOpen: () => setInvalidStatusesOpen(!invalidStatusesOpen)
    },
    {
      title: t('uploadFiles.invalidData'),
      data: invalidJsonData?.invalid_data,
      isOpen: invalidDataOpen,
      toggleOpen: () => setInvalidDataOpen(!invalidDataOpen)
    },
  ]

  setTimeout(() => {
    setDuplicateErrorMessageVisible(false);
  }, 20000);

  setTimeout(() => {
    setUploadErrorMessageVisible(false);
  }, 20000);

  setTimeout(() => {
    setRecordsSavedOpen(false);
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

  const deleteFile = (event: React.MouseEvent<HTMLButtonElement>, fileToDelete: File) => {
    event.stopPropagation();
    const updatedFiles = selectedFiles.filter((file) => file !== fileToDelete);
    setSelectedFiles(updatedFiles);
    if (updatedFiles.length === 0) {
      setButtonDisabled(true);
    }
  };

  const { getRootProps, getInputProps } = useDropzone({ onDrop });

  const handleUpload = () => {
    setUploadError(null);
    setRecordsSaved(0);
    setInvalidJsonData(null);

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

      api.post('http://localhost:8080/file/student', formData)
        .then((response) => {
          console.log('Przesłano plik:', response.data.message);
          const invalidData = JSON.parse(response.data.invalidData);
          const recordsSavedCount = invalidData.saved_records;

          const invalidDataWithFilename = {
            ...invalidData,
            database_repetitions: invalidData.database_repetitions
              ?.map((student: ImportedStudent) =>
               ({ ...student, source_file_name: file.name })),
            invalid_indices: invalidData.invalid_indices
              ?.map((student: ImportedStudent) =>
               ({ ...student, source_file_name: file.name })),
            invalid_names: invalidData.invalid_names
              ?.map((student: ImportedStudent) =>
               ({ ...student, source_file_name: file.name })),
            invalid_surnames: invalidData.invalid_surnames
              ?.map((student: ImportedStudent) =>
               ({ ...student, source_file_name: file.name })),
            invalid_statuses: invalidData.invalid_statuses
              ?.map((student: ImportedStudent) =>
               ({ ...student, source_file_name: file.name })),
            invalid_programs: invalidData.invalid_programs
              ?.map((student: ImportedStudent) =>
               ({ ...student, source_file_name: file.name })),
            invalid_cycles: invalidData.invalid_cycles
              ?.map((student: ImportedStudent) =>
               ({ ...student, source_file_name: file.name })),
            invalid_data: invalidData.invalid_data
              ?.map((student: ImportedStudent) =>
               ({ ...student, source_file_name: file.name })),
          };

          console.log(invalidData);
          
          setInvalidJsonData((prevInvalidData) => ({
            ...prevInvalidData,
            database_repetitions: [
              ...(prevInvalidData?.database_repetitions || []),
              ...(invalidDataWithFilename.database_repetitions || []),
            ],
            invalid_indices: [
              ...(prevInvalidData?.invalid_indices || []),
              ...(invalidDataWithFilename.invalid_indices || []),
            ],
            invalid_names: [
              ...(prevInvalidData?.invalid_names || []),
              ...(invalidDataWithFilename.invalid_names || []),
            ],
            invalid_surnames: [
              ...(prevInvalidData?.invalid_surnames || []),
              ...(invalidDataWithFilename.invalid_surnames || []),
            ],
            invalid_statuses: [
              ...(prevInvalidData?.invalid_statuses || []),
              ...(invalidDataWithFilename.invalid_statuses || []),
            ],
            invalid_programs: [
              ...(prevInvalidData?.invalid_programs || []),
              ...(invalidDataWithFilename.invalid_programs || []),
            ],
            invalid_cycles: [
              ...(prevInvalidData?.invalid_cycles || []),
              ...(invalidDataWithFilename.invalid_cycles || []),
            ],
            invalid_data: [
              ...(prevInvalidData?.invalid_data || []),
              ...(invalidDataWithFilename.invalid_data || []),
            ],
          }));
          
          setRecordsSaved((prevRecords) => prevRecords + recordsSavedCount);
        
          setRecordsSavedOpen(true);
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
            <h2>{t('uploadFiles.attachStudent')}</h2>
            <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
              &larr; {t('general.management.goBack')}
            </button>
        </div>

        {duplicateFilesError && duplicateErrorMessageVisible && (
            <div className="alert alert-danger mt-3 mb-3" role="alert">
              {duplicateFilesError}
            </div>
        )}

        {uploadError && uploadErrorMessageVisible && (
          <div className="alert alert-danger mt-3 mb-3" role="alert">
            {uploadError}
          </div>
        )}

        {recordsSavedOpen && (
          <div className="alert alert-success mt-3 mb-3" role="alert">
            {t('uploadFiles.recordsSaved')} {recordsSaved}
          </div>
        )}

        <div
          className="container d-flex justify-content-center mt-4 mb-4"
        >
          <div
            {...getRootProps()}
            className="border p-4 rounded shadow-lg dropzone"
            style={{
              width: '90%',
              maxWidth: '100%',
              height: '60%',
              maxHeight: '100%',
              overflowX: 'hidden',
              overflowY: 'hidden',
              marginBottom: '10px',
              display: 'block',
              textAlign: 'center',
              }}
          >
            <input {...getInputProps()} />
            <p>{t('uploadFiles.instruction')}</p>
            <h3 className="bi bi-download"></h3>
            {selectedFiles.length > 0 && (
            <section style={{maxHeight: '40%', overflow: 'auto'}}>
              <h4>{t('uploadFiles.chosenFiles')}:</h4>
              <ul className="list-group mb-3 file-list" style={{ flexWrap: 'wrap', overflow: 'auto' }}>
                {selectedFiles.map((file, index) => (
                  <li 
                    key={`${index}-${keyCounter++}`} 
                    className="list-group-item d-flex justify-content-between align-items-center mb-2 mt-2 border file-entry">
                      <span style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {file.name}
                      </span>
                      <button
                        className="btn btn-danger btn-sm custom-pwr-button"
                        onClick={(event) => deleteFile(event, file)}
                      >
                        {t('general.management.delete')}
                      </button>
                  </li>
                ))}
              </ul>
            </section>
          )}
          </div>
        </div>

          <button onClick={handleUpload} disabled={buttonDisabled} className="custom-button">
              {t('uploadFiles.sendFiles')}
          </button>
        </div>

    {sentData && (
      <div
        className="container d-flex justify-content-center mt-5"
      >
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
          <ul className="list-group mb-3 mt-3" style={{ flexWrap: 'wrap', overflow: 'auto' }}>
            {invalidDataList.map((item, index) => (
              item.data && item.data.length > 0 ? (
                <li className="list-group-item mb-2 border" key={`${index}-${keyCounter++}`}>
                  <div>
                  <div onClick={item.toggleOpen}>
                    <div className="dropdown-toggle d-flex justify-content-between align-items-center">
                      <span>{item.title}: {item.data.length}</span>
                    </div>
                  </div>
                  <div className={`collapse ${item.isOpen ? 'show' : ''}`} style={{ height: '100%', maxHeight: '100%', overflowX: 'auto', overflowY: 'hidden'}}>
                    <table className="custom-table">
                      <thead>
                        <tr>
                          <th style={{ width: '32%' }}>{t('uploadFiles.fileName')}</th>
                          <th style={{ width: '4%' }}>{t('general.people.index')}</th>
                          <th style={{ width: '32%' }}>{t('general.people.surname')}</th>
                          <th style={{ width: '32%' }}>{t('general.people.name')}</th>
                        </tr>
                      </thead>
                      <tbody>
                        {item.data?.map((student, index) => (
                          <tr key={`${student.index}-${keyCounter++}`}>
                            <td>{student?.source_file_name}</td>
                            <td>{student.index}</td>
                            <td>{student.surname}</td>
                            <td>{student.name}</td>
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

export default UploadStudentFilePage;
