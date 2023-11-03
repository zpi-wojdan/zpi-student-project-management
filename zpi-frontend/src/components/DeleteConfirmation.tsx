import React from 'react';
import {useTranslation} from "react-i18next";

interface DeleteConfirmationProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  onCancel: () => void;
}

const DeleteConfirmation: React.FC<DeleteConfirmationProps> = ({ isOpen, onClose, onConfirm, onCancel }) => {
  const { i18n, t } = useTranslation();
  if (!isOpen) {
    return null;
  }

  return (
    <div className='d-flex justify-content-center  align-items-center'>
        <div style={{ display: "flex", alignItems: "center" }}>
            <p style={{ margin: "0 10px", alignSelf: "center" }}>{t('deleteConfirmation.deleteFacultyConfirm')}</p>
            <button className="custom-button" onClick={onConfirm}>{t('general.management.yes')}</button>
            <button className="custom-button another-color" onClick={onCancel}>{t('general.management.no')}</button>
        </div>
    </div>
  );
};

export default DeleteConfirmation;