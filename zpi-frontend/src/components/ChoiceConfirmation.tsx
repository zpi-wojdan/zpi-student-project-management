import React from 'react';
import {useTranslation} from "react-i18next";

interface ChoiceConfirmationProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  onCancel: () => void;
  questionText: string;
}

const ChoiceConfirmation: React.FC<ChoiceConfirmationProps> = ({ isOpen, onClose, onConfirm, onCancel, questionText }) => {
  const { i18n, t } = useTranslation();

  if (!isOpen) {
    return null;
  }

  return (
    <div className='d-flex justify-content-center  align-items-center'>
        <div style={{ display: "flex", alignItems: "center" }}>
            <p style={{ margin: "0 10px", alignSelf: "center" }}>{questionText}</p>
            <button className="custom-button" onClick={onConfirm}>{t('general.management.yes')}</button>
            <button className="custom-button another-color" onClick={onCancel}>{t('general.management.no')}</button>
        </div>
    </div>
  );
};

export default ChoiceConfirmation;