import React, { ReactNode, useState } from 'react'
import {Link, NavLink, useLocation, useNavigate} from "react-router-dom";
// @ts-ignore
import Cookies from "js-cookie";
import handleSignOut from "../auth/Logout";
import { Dropdown, Nav } from 'react-bootstrap';

type NavigationProps = {} & {
    children?: ReactNode
}

const Navigation = ({ children }: NavigationProps) => {
    const [showNav, setShowNav] = useState(false);
    const isLoggedIn = Cookies.get('user') !== undefined;
    const user = Cookies.get('user') ? JSON.parse(Cookies.get('user') as string) : '';

    const location = useLocation();
    const navigate = useNavigate();
    const signOut = () => handleSignOut(navigate);
    const isLoginPage = location.pathname === '/login';


    return (
        <>
            <div className='container-fluid p-0'>
                <nav className="navbar navbar-expand-lg navbar-light bg-light p-0">
                    <div className="container">
                        <div className="me-auto"></div>

                        <ul className="navbar-nav mw-auto">
                            {isLoggedIn ? (
                                <li className="nav-item">
                                    <div className="nav-link">{user.name} {user.surname}</div>
                                </li>
                            ) : (<li></li>)}
                            <li className="nav-item">
                                <div className="nav-link">|</div>
                            </li>
                            <li className="nav-item">
                                <div className="nav-link">PL</div>
                            </li>
                            <li className="nav-item">
                                <div className="nav-link">|</div>
                            </li>
                            <li className="nav-item">
                                {isLoggedIn ? (
                                    <Link className="nav-link" to="login" onClick={signOut}>Wyloguj</Link>
                                ) : (
                                    <NavLink
                                        className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}
                                        to="login"
                                    >
                                        Logowanie
                                    </NavLink>
                                )}
                            </li>
                        </ul>
                    </div>
                </nav>
            </div>
            <img
                src="images/logo-pwr-2016/logo PWr kolor poziom ang  bez tla.png"
                alt="Logo Politechniki Wrocławskiej"
                className='w-25 my-3 ps-4 pe-5'
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
                                    <NavLink className={({ isActive }) => isActive ? "nav-link active" : "nav-link"} to="/">Strona główna</NavLink>
                                </li>
                                <li className="nav-item">
                                    <NavLink className={({ isActive }) => isActive ? "nav-link active" : "nav-link"} to="/theses" >Tematy</NavLink>
                                </li>
                                <li className="nav-item">
                                <Dropdown as={Nav.Item}>
                                    <Dropdown.Toggle as={Nav.Link}>Zarządzaj</Dropdown.Toggle>
                                    <Dropdown.Menu>
                                        <Dropdown.Item as={Link} to="/students">
                                            Studenci
                                        </Dropdown.Item>
                                        <Dropdown.Item as={Link} to="/employees">
                                            Pracownicy
                                        </Dropdown.Item>
                                        <Dropdown.Item as={Link} to="/faculties">
                                            Wydziały
                                        </Dropdown.Item>
                                        <Dropdown.Item as={Link} to="/fields">
                                            Kierunki
                                        </Dropdown.Item>
                                        <Dropdown.Item as={Link} to="/specializations">
                                            Specjalności
                                        </Dropdown.Item>
                                        <Dropdown.Item as={Link} to="/programs">
                                            Programy studiów
                                        </Dropdown.Item>
                                        <Dropdown.Item as={Link} to="/cycles">
                                            Cykle nauczania
                                        </Dropdown.Item>
                                        <Dropdown.Item as={Link} to="/departments">
                                            Katedry
                                        </Dropdown.Item>
                                    </Dropdown.Menu>
                                </Dropdown>
                                </li>
                                {/* tu później zrobić wyświetlanie warunkowe w zalezności od tego kto zalogowany */}
                                {isLoggedIn ? (
                                    <li className="nav-item">
                                        <NavLink className={({ isActive }) => isActive ? "nav-link active" : "nav-link"} to="/my">Moje</NavLink>
                                    </li>
                                ) : (<li></li>)}
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