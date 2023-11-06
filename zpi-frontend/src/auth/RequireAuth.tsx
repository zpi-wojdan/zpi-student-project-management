import {Navigate, Outlet, useLocation} from "react-router-dom";
import Cookies from "js-cookie";
import {Role} from "../models/Role";
import React from "react";
import useAuth from "./useAuth";

const RequireAuth: React.FC<{ allowedRoles: string[] }> = ({ allowedRoles }) => {
    // @ts-ignore
    const { auth, setAuth } = useAuth();
    const user = Cookies.get('user') ? JSON.parse(Cookies.get('user') as string) : '';
    const location = useLocation();

    if(user?.roles?.find((role: Role) => allowedRoles.includes(role.name)) || allowedRoles.includes(user?.role?.name)){
        return <Outlet />
    }
    else if(user){
        return <Navigate to="/unauthorized" state={{ from: location }} replace/>
    }
    else{
        setAuth({ ...auth, reasonOfLogout: 'access_denied' });
        return <Navigate to="/login" state={{ from: location }} replace/>
    }
}

export default RequireAuth
