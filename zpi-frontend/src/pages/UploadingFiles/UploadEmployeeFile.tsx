import { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import {useNavigate} from "react-router-dom";
import { InvalidEmployeeData, ImportedEmployee } from '../../models/ImportedData';
import {useTranslation} from "react-i18next";
import api from '../../utils/api';
import { toast } from 'react-toastify';
import api_access from '../../utils/api_access';

function UplaodEmployeeFilePage() {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [buttonDisabled, setButtonDisabled] = useState(true);

  const [invalidJsonData, setInvalidJsonData] = useState<InvalidEmployeeData | null>(null);
  const [sentData, setSentData] = useState(false);
  const [recordsSaved, setRecordsSaved] = useState<number | null>(0);

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

  const onDrop = useCallback((acceptedFiles: File[]) => {
    const newFiles = acceptedFiles.filter(
      (file) => !selectedFiles.some((existingFile) => (existingFile.name === file.name))
    );
    if (newFiles.length != acceptedFiles.length){
      toast.error(t('uploadFiles.duplicatedFileError'));
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
    setRecordsSaved(0);
    setInvalidJsonData(null);

    selectedFiles.forEach((file) => {
      var size = +((file.size / (1024*1024)).toFixed(2))
      if (size > 5){
        const errorMessage = t('uploadFiles.tooBigFileError', {fileName: file.name, fileSize: size});
        toast.error(errorMessage);
        return;
      }
      const formData = new FormData();
      formData.append('file', file);

      api.post(api_access + 'file/employee', formData)
        .then((response) => {
          console.log('Przesłano plik:', response.data.message);
          const invalidData = JSON.parse(response.data.invalidData);
          const recordsSavedCount = invalidData.saved_records;

          const invalidDataWithFilename = {
            ...invalidData,
            database_repetitions: invalidData.database_repetitions
              ?.map((employee: ImportedEmployee) =>
               ({ ...employee, source_file_name: file.name })),
            invalid_indices: invalidData.invalid_indices
              ?.map((employee: ImportedEmployee) =>
                ({ ...employee, source_file_name: file.name })),
            invalid_academic_titles: invalidData.invalid_academic_titles
              ?.map((employee: ImportedEmployee) =>
                ({ ...employee, source_file_name: file.name })),
            invalid_surnames: invalidData.invalid_surnames
              ?.map((employee: ImportedEmployee) =>
                ({ ...employee, source_file_name: file.name })),
            invalid_names: invalidData.invalid_names
              ?.map((employee: ImportedEmployee) =>
                ({ ...employee, source_file_name: file.name })),
            invalid_units: invalidData.invalid_units
              ?.map((employee: ImportedEmployee) =>
              ({ ...employee, source_file_name: file.name })),
            invalid_subunits: invalidData.invalid_subunits
              ?.map((employee: ImportedEmployee) =>
                ({ ...employee, source_file_name: file.name })),
            invalid_positions: invalidData.invalid_positions
              ?.map((employee: ImportedEmployee) =>
                ({ ...employee, source_file_name: file.name })),
            invalid_phone_numbers: invalidData.invalid_phone_numbers
              ?.map((employee: ImportedEmployee) =>
                ({ ...employee, source_file_name: file.name })),
            invalid_emails: invalidData.invalid_emails
              ?.map((employee: ImportedEmployee) =>
                ({ ...employee, source_file_name: file.name })),
            invalid_data: invalidData.invalid_data
              ?.map((employee: ImportedEmployee) =>
                ({ ...employee, source_file_name: file.name })),
          }

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
            invalid_academic_titles: [
              ...(prevInvalidData?.invalid_academic_titles || []),
              ...(invalidDataWithFilename.invalid_academic_titles || []),
            ],
            invalid_surnames: [
              ...(prevInvalidData?.invalid_surnames || []),
              ...(invalidDataWithFilename.invalid_surnames || []),
            ],
            invalid_names: [
              ...(prevInvalidData?.invalid_names || []),
              ...(invalidDataWithFilename.invalid_names || []),
            ],
            invalid_units: [
              ...(prevInvalidData?.invalid_units || []),
              ...(invalidDataWithFilename.invalid_units || []),
            ],
            invalid_subunits: [
              ...(prevInvalidData?.invalid_subunits || []),
              ...(invalidDataWithFilename.invalid_subunits || []),
            ],
            invalid_positions: [
              ...(prevInvalidData?.invalid_positions || []),
              ...(invalidDataWithFilename.invalid_positions || []),
            ],
            invalid_phone_numbers: [
              ...(prevInvalidData?.invalid_phone_numbers || []),
              ...(invalidDataWithFilename.invalid_phone_numbers || []),
            ],
            invalid_emails: [
              ...(prevInvalidData?.invalid_emails || []),
              ...(invalidDataWithFilename.invalid_emails || []),
            ],
            invalid_data: [
              ...(prevInvalidData?.invalid_data || []),
              ...(invalidDataWithFilename.invalid_data || []),
            ],
          }));
          
          setRecordsSaved((prevRecords) => prevRecords + recordsSavedCount);
          setSentData(true);

          toast.success(t('uploadFiles.recordsSaved') + recordsSaved)
        })
        .catch((error) => {
          toast.error(t('uploadFiles.filesNotSentError'));
          setSentData(false);
          console.error('Nie udało się przesłać plików', error);
          if (error.response && (error.response.status === 401 ||  error.response.status === 403)) {
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
            <h2>{t('uploadFiles.attachEmployee')}</h2>
            <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
              &larr; {t('general.management.goBack')}
            </button>
          </div>

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
                        <i className="bi bi-trash"></i>
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
                            <th style={{ width: '33%' }}>E-mail</th>
                            <th style={{ width: '33%' }}>{t('uploadFiles.unit')}</th>
                            <th style={{ width: '33%' }}>{t('uploadFiles.subunit')}</th>
                          </tr>
                        </thead>
                        <tbody>
                          {item.data?.map((employee, index) => (
                            <tr key={`${employee.email}-${keyCounter++}`}>
                              <td>{employee?.source_file_name}</td>
                              <td>{employee.email}</td>
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
