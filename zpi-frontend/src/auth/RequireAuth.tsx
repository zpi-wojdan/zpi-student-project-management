import {Navigate, Outlet, useLocation, useNavigate} from "react-router-dom";
import Cookies from "js-cookie";
import {Role} from "../models/Role";
import React, {useEffect} from "react";
import useAuth from "./useAuth";

const RequireAuth: React.FC<{ allowedRoles: string[] }> = ({ allowedRoles }) => {
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const user = Cookies.get('user') ? JSON.parse(Cookies.get('user') as string) : '';
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        if (!user) {
            setAuth({ ...auth, reasonOfLogout: 'access_denied' });
            navigate('/login', { state: { from: location }, replace: true });
        }
    }, [auth, setAuth, user, navigate, location]);

    if(user?.roles?.find((role: Role) => allowedRoles.includes(role.name)) || allowedRoles.includes(user?.role?.name)){
        return <Outlet />
    }
    else if(user){
        return <Navigate to="/unauthorized" state={{ from: location }} replace/>
    }
    else {
        return null
    }
}

export default RequireAuth
