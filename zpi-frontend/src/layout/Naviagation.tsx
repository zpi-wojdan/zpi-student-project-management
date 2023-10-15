import React, { ReactNode, useState } from 'react'
import { Link, NavLink } from 'react-router-dom'

type NavigationProps = {} & {
    children?: ReactNode
}

const Naviagation = ({ children }: NavigationProps) => {
    const [showNav, setShowNav] = useState(false);

    return (
        <>
            <div className='container-fluid'>
                <nav className="navbar navbar-expand-lg navbar-light bg-light p-0">
                    <div className="container">
                        <div className="me-auto"></div>

                        <ul className="navbar-nav mw-auto">
                            <li className="nav-item">
                                <Link className="nav-link" to="login">PL</Link>
                            </li>
                            <li className="nav-item">
                                <div className="nav-link">|</div>
                            </li>
                            {/* tu później zrobić wyświetlanie warunkowe w zalezności od tego czy zalogowany czy nie */}
                            <li className="nav-item">
                                <NavLink className={({ isActive }) => isActive ? "nav-link active" : "nav-link"} to="login">Logowanie</NavLink>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="logout">Logout</Link>
                            </li>
                        </ul>
                    </div>
                </nav>
            </div>
            <img
                src="logo-pwr-2016/logo PWr kolor poziom ang  bez tla.png"
                alt="Logo Politechniki Wrocławskiej"
                className='w-25 my-2'
            />
            <div className='container'>
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
                                    <NavLink className={({ isActive }) => isActive ? "nav-link active" : "nav-link"} to="/topics" >Tematy</NavLink>
                                </li>
                                {/* tu później zrobić wyświetlanie warunkowe w zalezności od tego kto zalogowany */}
                                <li className="nav-item">
                                    <NavLink className={({ isActive }) => isActive ? "nav-link active" : "nav-link"} to="/my">Moje</NavLink>
                                </li>
                            </ul>
                        </div>
                    </div>
                </nav>
                {children}
            </div>
        </>
    )
}

export default Naviagation