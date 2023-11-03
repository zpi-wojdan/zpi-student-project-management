import { useState, useCallback } from 'react';
import axios from 'axios';
import { useDropzone } from 'react-dropzone';
import Cookies from "js-cookie";
import handleSignOut from "../../auth/Logout";
import useAuth from "../../auth/useAuth";
import {useNavigate} from "react-router-dom";

function UplaodEmployeeFilePage() {
  // @ts-ignore
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [buttonDisabled, setButtonDisabled] = useState(true);
  const [uploadError, setUploadError] = useState<string | null>(null);
  const [duplicateFilesError, setDuplicateFilesError] = useState<string | null>(null);
  const [duplicateErrorMessageVisible, setDuplicateErrorMessageVisible] = useState(false);
  const [uploadErrorMessageVisible, setUploadErrorMessageVisible] = useState(false);

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
      setDuplicateFilesError("Ładowanie duplikatów plików nie jest dozwolone");
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
        const errorMessage = 'Zbyt duży rozmiar pliku ' + file.name + ' - jego rozmiar: ' + size + ' MB';
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
          console.log('JSON Response:', response.data['invalidData'].size);
        })
        .catch((error) => {
          setUploadError('Nie udało się przesłać plików');
          setUploadErrorMessageVisible(true);
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

  return (
    <div className="container d-flex justify-content-center mt-5">
      <div className="border p-4 rounded shadow-lg" style={{ width: '80%', maxWidth: '100%', overflow: 'hidden' }}>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Załącz pliki</h2>
        <button type="button" className="custom-button another-color" onClick={() => navigate(-1)}>
          &larr; Powrót
        </button>
      </div>
        <div {...getRootProps()} className="dropzone">
          <input {...getInputProps()} />
          <p>Przeciągnij i upuść, lub kliknij aby wybrać</p>
        </div>
        {duplicateFilesError && duplicateErrorMessageVisible && (
          <div className="alert alert-danger mt-3" role="alert">
            {duplicateFilesError}
          </div>
        )}
        {selectedFiles.length > 0 && (
      <section>
        <h4>Wybrane pliki:</h4>
        <ul className="list-group mb-3" style={{ flexWrap: 'wrap', overflow: 'auto' }}>
          {selectedFiles.map((file, index) => (
            <li key={index} className="list-group-item d-flex justify-content-between align-items-center">
              <span style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                {file.name}
              </span>
              <button
                className="btn btn-danger btn-sm custom-pwr-button"
                onClick={() => deleteFile(file)}
              >
                Usuń
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
        <button onClick={handleUpload} disabled={buttonDisabled} className="btn btn-primary mt-2 custom-pwr-button">
          Prześlij pliki
        </button>
      </div>
    </div>

  );
}

export default UplaodEmployeeFilePage;
