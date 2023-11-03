import React from 'react';

interface DeleteConfirmationProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  onCancel: () => void;
}

const DeleteConfirmation: React.FC<DeleteConfirmationProps> = ({ isOpen, onClose, onConfirm, onCancel }) => {
  if (!isOpen) {
    return null;
  }

  return (
    <div className='d-flex justify-content-center  align-items-center'>
        <div style={{ display: "flex", alignItems: "center" }}>
            <p style={{ margin: "0 10px", alignSelf: "center" }}>Czy na pewno chcesz usunąć ten wydział?</p>
            <button className="custom-button" onClick={onConfirm}>Tak</button>
            <button className="custom-button another-color" onClick={onCancel}>Nie</button>
        </div>
    </div>
  );
};

export default DeleteConfirmation;