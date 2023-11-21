import React, { ReactNode, useState } from 'react'
import { Link, NavLink, useLocation, useNavigate } from "react-router-dom";
// @ts-ignore
import Cookies from "js-cookie";
import handleSignOut from "../auth/Logout";
import { Dropdown, Nav } from 'react-bootstrap';
import { Role } from "../models/Role";
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
        '/deadlines'
    ];

    const isManagementActive = allowedPaths.some(path => location.pathname.startsWith(path));
    const onChangeLang = (lang: string) => {
        console.log(lang);
        if (lang !== i18n.language) {
            i18n.changeLanguage(lang);
            Cookies.set('lang', lang);
        }
    };

    return (
        <>
            <div className='container-fluid p-0'>
                <nav className="navbar navbar-expand-lg navbar-light bg-light p-0">
                    <div className="container collapse navbar-collapse">
                        <div className="me-auto"></div>


                        <ul className="navbar-nav">
                            {isLoggedIn ? (
                                <li className="nav-item mx-0">
                                    <div className="nav-link">{user.name} {user.surname}</div>
                                </li>
                            ) : null}
                            <li className="nav-item mx-0">
                                <div className="nav-link mx-0">|</div>
                            </li>
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
            <img
                src="/images/logo PWr kolor poziom ang  bez tla.png"
                alt={t('navigation.imageAlt')}
                className='my-3 ps-4 pe-5'
                style={{ height: '8vh' }}
            />
            <div className='container'>
                <div className={`container p-0 ${isLoginPage ? 'd-none' : ''}`}>
                    <nav className="navbar navbar-expand-lg navbar-light bg-light">
                        <div className="container">
                            <Link className="navbar-brand" to="/">ZPI Helper</Link>
                            <button className="navbar-toggler" type="button" onClick={() => setShowNav(!showNav)}>
                                <span className="navbar-toggler-icon"></span>
                            </button>
                            <div className={`collapse navbar-collapse ${showNav ? 'show' : ''}`} id="navbarNav">
                                <ul className="navbar-nav me-auto">
                                    <li className="nav-item">
                                        <NavLink className={({ isActive }) => isActive ?
                                            "nav-link active" : "nav-link"} to="/">{t('navigation.home')}</NavLink>
                                    </li>
                                    {isLoggedIn ? (
                                        <>
                                            <li className="nav-item">
                                                <NavLink className={({ isActive }) => isActive ?
                                                    "nav-link active" : "nav-link"} to="/public-theses" >
                                                    {t('general.university.theses')}
                                                </NavLink>
                                            </li>
                                            {user?.roles?.some((role: Role) => role.name === 'admin') ? (
                                                <>
                                                <li className="nav-item">
                                                    <Dropdown as={Nav.Item}>
                                                        <Dropdown.Toggle as={Nav.Link} className={isManagementActive ? "active" : ""}>{t('navigation.manage')}</Dropdown.Toggle>
                                                        <Dropdown.Menu>
                                                            <Dropdown.Item as={Link} to="/theses" className={location.pathname === '/thesis' ? "active" : ""}>
                                                                {t('general.university.theses')}
                                                            </Dropdown.Item>
                                                            <Dropdown.Item as={Link} to="/students" className={location.pathname === '/students' ? "active" : ""}>
                                                                {t('general.people.students')}
                                                            </Dropdown.Item>
                                                            <Dropdown.Item as={Link} to="/employees" className={location.pathname === '/employees' ? "active" : ""}>
                                                                {t('general.people.employees')}
                                                            </Dropdown.Item>
                                                            <Dropdown.Item as={Link} to="/faculties" className={location.pathname === '/faculties' ? "active" : ""}>
                                                                {t('general.university.faculties')}
                                                            </Dropdown.Item>
                                                            <Dropdown.Item as={Link} to="/fields" className={location.pathname === '/fields' ? "active" : ""}>
                                                                {t('general.university.fields')}
                                                            </Dropdown.Item>
                                                            <Dropdown.Item as={Link} to="/specializations" className={location.pathname === '/specializations' ? "active" : ""}>
                                                                {t('general.university.specializations')}
                                                            </Dropdown.Item>
                                                            <Dropdown.Item as={Link} to="/programs" className={location.pathname === '/programs' ? "active" : ""}>
                                                                {t('general.university.studyPrograms')}
                                                            </Dropdown.Item>
                                                            <Dropdown.Item as={Link} to="/cycles" className={location.pathname === '/cycles' ? "active" : ""}>
                                                                {t('general.university.studyCycles')}
                                                            </Dropdown.Item>
                                                            <Dropdown.Item as={Link} to="/departments" className={location.pathname === '/departments' ? "active" : ""}>
                                                                {t('general.university.departments')}
                                                            </Dropdown.Item>
                                                            <Dropdown.Item as={Link} to="/deadlines" className={location.pathname === '/deadlines' ? "active" : ""}>
                                                                {t('general.university.deadlines')}
                                                            </Dropdown.Item>
                                                        </Dropdown.Menu>
                                                    </Dropdown>

                                                </li>
                                                    <li className="nav-item">
                                                        <NavLink className={({ isActive }) => isActive ?
                                                            "nav-link active" : "nav-link"} to="/reports">
                                                            {t('navigation.reports')}
                                                        </NavLink>
                                                    </li>
                                                </>
                                            ) : null}
                                            {user?.roles?.some((role: Role) => role.name === 'supervisor') ? (
                                                <li className="nav-item">
                                                    <NavLink className={({ isActive }) => isActive ?
                                                        "nav-link active" : "nav-link"} to="/my">
                                                        {t('navigation.myTheses')}
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