import React, { ReactNode, useEffect, useState } from 'react'
import { Link, NavLink, useLocation, useNavigate } from "react-router-dom";
// @ts-ignore
import Cookies from "js-cookie";
import handleSignOut from "../auth/Logout";
import { Dropdown, Nav } from 'react-bootstrap';
import { Role } from "../models/user/Role";
import { useTranslation } from "react-i18next";

type NavigationProps = {} & {
    children?: ReactNode
}

const Navigation = ({ children }: NavigationProps) => {
    const [showNav, setShowNav] = useState(false);
    const { i18n, t } = useTranslation();
    const isLoggedIn = Cookies.get('user') !== undefined;
    const user = Cookies.get('user') ? JSON.parse(Cookies.get('user') as string) : '';

    const location = useLocation();
    const navigate = useNavigate();
    const signOut = () => handleSignOut(navigate);
    const isLoginPage = location.pathname === '/login';

    const allowedPaths = [
        '/students',
        '/employees',
        '/faculties',
        '/fields',
        '/specializations',
        '/programs',
        '/cycles',
        '/departments',
        '/theses',
        '/deadlines',
        '/titles',
        '/clear'
    ];

    const isManagementActive = allowedPaths.some(path => location.pathname.startsWith(path));
    const isActive = (path: string) => location.pathname.startsWith(path);
    const onChangeLang = (lang: string) => {
        if (lang !== i18n.language) {
            i18n.changeLanguage(lang);
            Cookies.set('lang', lang);
        }
    };

    const [prevPath, setPrevPath] = useState<string>("");

    useEffect(() => {
        const path = location.pathname;
        if (prevPath.startsWith('/students') && !path.startsWith('/students')) {
            localStorage.removeItem('studentFilterFaculty');
            localStorage.removeItem('studentFilterField');
            localStorage.removeItem('studentFilterSpecialization');
        }
        if (prevPath.startsWith('/employees') && !path.startsWith('/employees')) {
            localStorage.removeItem('employeeFilterDepartment');
            localStorage.removeItem('employeeFilterRole');
            localStorage.removeItem('employeeFilterTitle')
        }
        if (prevPath.startsWith('/theses') && !path.startsWith('/theses')) {
            localStorage.removeItem('adminThesesFilterFaculty');
            localStorage.removeItem('adminThesesFilterField');
            localStorage.removeItem('adminThesesFilterSpecialization');
            localStorage.removeItem('adminThesesFilterMinVacancies');
            localStorage.removeItem('adminThesesFilterMaxVacancies');
            localStorage.removeItem('adminThesesFilterCycle');
            localStorage.removeItem('adminThesesFilterSupervisors');
            localStorage.removeItem('adminThesesFilterStatus')
        }
        if (prevPath.startsWith('/public-theses') && !path.startsWith('/public-theses')) {
            localStorage.removeItem('publicThesesFilterFaculty');
            localStorage.removeItem('publicThesesFilterField');
            localStorage.removeItem('publicThesesFilterSpecialization');
            localStorage.removeItem('publicThesesFilterMinVacancies');
            localStorage.removeItem('publicThesesFilterMaxVacancies');
            localStorage.removeItem('publicThesesFilterCycle');
            localStorage.removeItem('publicThesesFilterSupervisors');
        }
        if (prevPath.startsWith('/manage') && !path.startsWith('/manage')) {
            localStorage.removeItem('approverFilterFaculty');
            localStorage.removeItem('approverFilterField');
            localStorage.removeItem('approverFilterSpecialization');
            localStorage.removeItem('approverFilterCycle');
            localStorage.removeItem('approverFilterSupervisors');
        }
        setPrevPath(path);
    }, [location.pathname]);

    return (
        <>
            <div className='container-fluid p-0'>
                <nav className="navbar navbar-expand navbar-light bg-light p-0">
                    <div className="container collapse navbar-collapse">
                        <Link className="navbar-brand px-4 fs-4" to="/">ZPI Helper</Link>
                        <ul className="navbar-nav">
                            {isLoggedIn ? (
                                <><li className="nav-item mx-0 text-nowrap">
                                    <div className="nav-link">{user.name} {user.surname}</div>
                                </li><li className="nav-item mx-0">
                                        <div className="nav-link mx-0">|</div>
                                    </li></>
                            ) : null}
                            <li className="nav-item mx-0">
                                <Link
                                    className={`mx-0 nav-link ${i18n.language === 'pl' ? 'lang-link-active' : ''}`}
                                    onClick={() => onChangeLang('pl')}
                                    to={location.pathname}
                                >
                                    PL
                                </Link>
                            </li>
                            <li className="nav-item mx-0">
                                <div className="nav-link mx-0">&bull;</div>
                            </li>
                            <li className="nav-item mx-0">
                                <Link
                                    className={`mx-0 nav-link ${i18n.language === 'en' ? 'lang-link-active' : ''}`}
                                    onClick={() => onChangeLang('en')}
                                    to={location.pathname}
                                >
                                    EN
                                </Link>
                            </li>
                            <li className="nav-item mx-0">
                                <div className="nav-link mx-0">|</div>
                            </li>
                            <li className="nav-item mx-0">
                                {isLoggedIn ? (
                                    <Link
                                        className="nav-link mx-0"
                                        to="login"
                                        onClick={signOut}
                                    >
                                        {t('navigation.logout')}
                                    </Link>
                                ) : (
                                    <NavLink
                                        className={({ isActive }) => isActive ? "nav-link active mx-0" :
                                            "nav-link"}
                                        to="login"
                                    >
                                        {t('navigation.login')}
                                    </NavLink>
                                )}
                            </li>
                        </ul>
                    </div>
                </nav>
            </div>
            <div className='container'>
                {i18n.language == "pl" ? (
                    <img
                        src="/images/logoPl.png"
                        alt={t('navigation.imageAlt')}
                        className='my-3 ps-4 pe-5'
                        style={{ width: 'auto', height: '60px' }}
                    />
                ) : (
                    <img
                        src="/images/logoEn.png"
                        alt={t('navigation.imageAlt')}
                        className='my-3 ps-4 pe-5'
                        style={{ width: 'auto', height: '60px' }}
                    />
                )}
            </div>
            <div className='container'>
                <div className={'container p-0'}>
                    <nav className="navbar navbar-expand-lg navbar-light bg-light">
                        <div className="container">
                            <button className="navbar-toggler ms-auto" type="button" onClick={() => setShowNav(!showNav)}>
                                <span className="navbar-toggler-icon"></span>
                            </button>
                            <div className={`collapse navbar-collapse ${showNav ? 'show' : ''}`} id="navbarNav">
                                <ul className="navbar-nav me-auto ms-2">
                                    <li className="nav-item">
                                        <NavLink className={({ isActive }) => isActive ?
                                            "nav-link active px-2" : "nav-link px-2"} to="/">{t('navigation.home')}</NavLink>
                                    </li>
                                    {isLoggedIn ? (
                                        <>
                                            <li className="nav-item">
                                                <NavLink className={({ isActive }) => isActive ?
                                                    "nav-link active px-2" : "nav-link px-2"} to="/public-theses" >
                                                    {t('general.university.theses')}
                                                </NavLink>
                                            </li>
                                            {user?.roles?.some((role: Role) => role.name === 'admin') ? (
                                                <>
                                                    <li className="nav-item">
                                                        <Dropdown as={Nav.Item} className="custom-dropdown">
                                                            <Dropdown.Toggle as={Nav.Link} className={isManagementActive ? "active px-2" : "px-2"}>{t('navigation.manage')}</Dropdown.Toggle>
                                                            <Dropdown.Menu>
                                                                <Dropdown.Item as={Link} to="/theses" className={isActive('/theses') ? "active" : ""}>
                                                                    {t('general.university.theses')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/students" className={isActive('/students') ? "active" : ""}>
                                                                    {t('general.people.students')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/employees" className={isActive('/employees') ? "active" : ""}>
                                                                    {t('general.people.employees')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/faculties" className={isActive('/faculties') ? "active" : ""}>
                                                                    {t('general.university.faculties')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/fields" className={isActive('/fields') ? "active" : ""}>
                                                                    {t('general.university.fields')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/specializations" className={isActive('/specializations') ? "active" : ""}>
                                                                    {t('general.university.specializations')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/programs" className={isActive('/programs') ? "active" : ""}>
                                                                    {t('general.university.studyPrograms')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/cycles" className={isActive('/cycles') ? "active" : ""}>
                                                                    {t('general.university.studyCycles')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/departments" className={isActive('/departments') ? "active" : ""}>
                                                                    {t('general.university.departments')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/deadlines" className={isActive('/deadlines') ? "active" : ""}>
                                                                    {t('general.university.deadlines')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/titles" className={isActive('/titles') ? "active" : ""}>
                                                                    {t('general.titles')}
                                                                </Dropdown.Item>
                                                                <Dropdown.Item as={Link} to="/clear" className={isActive('/clear') ? "active" : ""}>
                                                                    {t('general.clearData.clearSystem')}
                                                                </Dropdown.Item>
                                                            </Dropdown.Menu>
                                                        </Dropdown>

                                                    </li>
                                                    <li className="nav-item">
                                                        <NavLink className={({ isActive }) => isActive ?
                                                            "nav-link active px-2" : "nav-link px-2"} to="/reports">
                                                            {t('navigation.reports')}
                                                        </NavLink>
                                                    </li>
                                                </>
                                            ) : null}
                                            {user?.roles?.some((role: Role) => role.name === 'supervisor') ? (
                                                <li className="nav-item">
                                                    <NavLink className={isActive('/my') ?
                                                        "nav-link active px-2" : "nav-link px-2"} to="/my">
                                                        {t('navigation.myTheses')}
                                                    </NavLink>
                                                </li>
                                            ) : null}
                                            {user?.role?.name === 'student' ? (
                                                <li className="nav-item">
                                                    <NavLink className={isActive('/myThesis') ?
                                                        "nav-link active px-2" : "nav-link px-2"} to="/myThesis">
                                                        {t('navigation.myThesis')}
                                                    </NavLink>
                                                </li>
                                            ) : null}
                                            {user?.roles?.some((role: Role) => role.name === 'approver') ? (
                                                <li className="nav-item">
                                                    <NavLink className={({ isActive }) => isActive ?
                                                        "nav-link active" : "nav-link"} to="/manage">
                                                        {t('navigation.confirm')}
                                                    </NavLink>
                                                </li>
                                            ) : null}
                                        </>
                                    ) : null}
                                </ul>
                            </div>
                        </div>
                    </nav>
                </div>
                {children}
            </div>
        </>
    )
}

export default Navigation