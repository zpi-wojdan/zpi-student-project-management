import useAuth from "../auth/useAuth";
import {useLocation, useNavigate} from "react-router-dom";
import {useTranslation} from "react-i18next";
import React, {useEffect, useState} from "react";
import api from "../utils/api";
import {toast} from "react-toastify";
import handleSignOut from "../auth/Logout";
import {Faculty} from "../models/Faculty";
import {StudyField} from "../models/StudyField";

const Reports = () => {
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const navigate = useNavigate();
    const { i18n, t } = useTranslation();
    const [formData, setFormData] = useState({
        studyFieldAbbr: '',
        facultyAbbr: '',
        reportType: '',
    });
    const [errors, setErrors] = useState<Record<string, string>>({});
    const [errorsKeys, setErrorsKeys] = useState<Record<string, string>>({});

    useEffect(() => {
        const newErrors: Record<string, string> = {};
        Object.keys(errorsKeys).forEach((key) => {
            newErrors[key] = t(errorsKeys[key]);
        });
        setErrors(newErrors);
    }, [i18n.language]);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (validateForm()) {
            let url = 'http://localhost:8080/report/pdf/'
            if (formData.reportType === 'thesisGroups')
                url += "thesis-groups"
            else
                url += "students-without-thesis"
            if (formData.facultyAbbr !== 'all' && formData.studyFieldAbbr !== 'all')
                url += `?facultyAbbr=${formData.facultyAbbr}&studyFieldAbbr=${formData.studyFieldAbbr}`;
            else if (formData.facultyAbbr !== 'all')
                url += `?facultyAbbr=${formData.facultyAbbr}`;
            else if (formData.studyFieldAbbr !== 'all')
                url += `?studyFieldAbbr=${formData.studyFieldAbbr}`;

            api.get(url, { responseType: 'blob' })
                .then((response) => {
                    const file = new Blob([response.data], { type: 'application/pdf' });
                    const fileURL = URL.createObjectURL(file);
                    const link = document.createElement('a');
                    link.href = fileURL;

                    const contentDisposition = response.headers['content-disposition'];
                    let filename = 'report.pdf';
                    if (contentDisposition) {
                        const filenameMatch = contentDisposition.match(/filename=(.+)/i);
                        if (filenameMatch.length === 2)
                            filename = filenameMatch[1];
                    }

                    link.setAttribute('download', filename);
                    document.body.appendChild(link);
                    link.click();
                    toast.success(t('reports.generateSuccessful'));
                })
                .catch((error) => {
                    console.error(error);
                    if (error.response.status === 401 || error.response.status === 403) {
                        setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                        handleSignOut(navigate);
                    }
                    else if (error.response.status === 404) {
                        if (formData.reportType === 'thesisGroups')
                            toast.error(t('reports.generateThesisGroupsError'));
                        else
                            toast.error(t('reports.generateStudentsWithoutThesisError'));
                    }
                    else
                        toast.error(t('reports.generateError'));
                });
        }
    };

    const validateForm = () => {
        const newErrors: Record<string, string> = {};
        const newErrorsKeys: Record<string, string> = {};
        let isValid = true;

        const errorRequireText = t('general.management.fieldIsRequired');

        if (!formData.reportType) {
            newErrors.reportType = errorRequireText;
            newErrorsKeys.reportType = 'general.management.fieldIsRequired';
            isValid = false;
        }

        if (!formData.facultyAbbr) {
            newErrors.faculty = errorRequireText;
            newErrorsKeys.faculty = 'general.management.fieldIsRequired';
            isValid = false;
        }

        if (!formData.studyFieldAbbr) {
            newErrors.studyField = errorRequireText;
            newErrorsKeys.studyField = 'general.management.fieldIsRequired';
            isValid = false;
        }

        setErrors(newErrors);
        setErrorsKeys(newErrorsKeys);
        return isValid;
    };

    const [availableFaculties, setAvailableFaculties] = useState<Faculty[]>([]);
    const [availableFields, setAvailableFields] = useState<StudyField[]>([]);

    useEffect(() => {
        api.get('http://localhost:8080/faculty/ordered')
            .then((response) => {
                setAvailableFaculties(response.data);
            })
            .catch((error) => {
                console.error(error);
                if (error.response.status === 401 || error.response.status === 403) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });
    }, []);

    useEffect(() => {
        api.get('http://localhost:8080/studyfield/ordered')
            .then((response) => {
                setAvailableFields(response.data);
            })
            .catch((error) => {
                console.error(error)
                if (error.response.status === 401 || error.response.status === 403) {
                    setAuth({ ...auth, reasonOfLogout: 'token_expired' });
                    handleSignOut(navigate);
                }
            });
    }, []);

    return (
        <div className='page-margin'>
            <form onSubmit={handleSubmit} className="form">
                <div className="mb-3">
                    <label className="bold" htmlFor="reportType">
                        {t('reports.reportType')}:
                    </label>
                    <select
                        id="reportType"
                        name="reportType"
                        value={formData.reportType}
                        onChange={(e) => {
                            setFormData({
                                ...formData,
                                reportType: e.target.value,
                            });
                        }}
                        className="form-control"
                    >
                        <option value="">{t('general.management.choose')}</option>
                        <option key="thesisGroups" value="thesisGroups">
                            {t('reports.thesisGroups')}
                        </option>
                        <option key="studentsWithoutThesis" value="studentsWithoutThesis">
                            {t('reports.studentsWithoutThesis')}
                        </option>
                    </select>
                    {errors.reportType && <div className="text-danger">{errors.reportType}</div>}
                </div>
                <div className="mb-3">
                    <label className="bold" htmlFor="faculty">
                        {t('general.university.faculty')}:
                    </label>
                    <select
                        id="faculty"
                        name="faculty"
                        value={formData.facultyAbbr}
                        onChange={(e) => {
                            setFormData({
                                ...formData,
                                facultyAbbr: e.target.value,
                                studyFieldAbbr: e.target.value === "all" ? "all" : "",
                            });
                        }}
                        className="form-control"
                    >
                        <option value="">{t('general.management.choose')}</option>
                        {availableFaculties.map((faculty) => (
                            <option key={faculty.id} value={faculty.abbreviation}>
                                {faculty.abbreviation} - {faculty.name}
                            </option>
                        ))}
                        <option key="all" value="all">{t('general.management.all')}</option>
                    </select>
                    {errors.faculty && <div className="text-danger">{errors.faculty}</div>}
                </div>
                <div className="mb-3">
                    <label className="bold" htmlFor="studyField">
                        {t('general.university.field')}:
                    </label>
                    <select
                        id="studyField"
                        name="studyField"
                        value={formData.studyFieldAbbr}
                        onChange={(e) => {
                            setFormData({
                                ...formData,
                                studyFieldAbbr: e.target.value,
                            });
                        }}
                        className="form-control"
                        disabled={formData.facultyAbbr === "" || formData.facultyAbbr === "all"}
                    >
                        <option value={""}>{t('general.management.choose')}</option>
                        {formData.facultyAbbr !== "" &&
                            availableFields
                                .filter((fi) => fi.faculty.abbreviation === formData.facultyAbbr)
                                .map((field, fIndex) => (
                                    <option key={fIndex} value={field.abbreviation}>
                                        {field.abbreviation} - {field.name}
                                    </option>
                                ))}
                        <option key="all" value="all">{t('general.management.all')}</option>
                    </select>
                    {errors.studyField && <div className="text-danger">{errors.studyField}</div>}
                </div>
                <div className='d-flex justify-content-begin  align-items-center mb-3'>
                    <button type="submit" className="custom-button">
                        {t('reports.generate')}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default Reports;
