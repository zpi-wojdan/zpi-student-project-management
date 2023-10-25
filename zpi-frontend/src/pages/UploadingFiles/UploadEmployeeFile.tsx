import { useState, useCallback } from 'react';
import axios from 'axios';
import { useDropzone } from 'react-dropzone';
import '../../App.css'

function UplaodEmployeeFilePage() {
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [buttonDisabled, setButtonDisabled] = useState(true);
  const [uploadError, setUploadError] = useState<string | null>(null);
  const [duplicateFilesError, setDuplicateFilesError] = useState<string | null>(null);
  const [duplicateErrorMessageVisible, setDuplicateErrorMessageVisible] = useState(false);
  const [uploadErrorMessageVisible, setUploadErrorMessageVisible] = useState(false);

  setTimeout(() => {
    setDuplicateErrorMessageVisible(false);
  }, 10000);

  setTimeout(() => {
    setUploadErrorMessageVisible(false);
  }, 10000);

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
      const formData = new FormData();
      formData.append('file', file);

      axios
        .post('http://localhost:8080/file/employee', formData)
        .then((response) => {
          console.log('Przesłano plik:', response.data);
        })
        .catch((error) => {
          setUploadError('Wystąpił błąd: nie udało się przesłać plików');
          setUploadErrorMessageVisible(true);
          console.error('Wystąpił błąd: nie udało się przesłać plików', error);
        });
    });
    setSelectedFiles([]);
    setButtonDisabled(true);
  };

  return (
    <div className="container d-flex justify-content-center">
      <div className="border p-4 rounded shadow-lg">
        <h2 className="mb-4">Załącz pliki</h2>
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
            <ul className='list-group'>
              {selectedFiles.map((file, index) => (
                <li key={index} className="list-group-item d-flex justify-content-between align-items-center">
                  {file.name}
                  <button
                    className="btn btn-danger btn-sm ml-3"
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
          <div className="alert alert-danger mt-3" role="alert">
            {uploadError}
          </div>
        )}
        <button onClick={handleUpload} disabled={buttonDisabled} className="btn btn-primary mt-3">
          Prześlij pliki
        </button>
      </div>
    </div>
  );
}

export default UplaodEmployeeFilePage;
