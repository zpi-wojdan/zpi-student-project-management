import {Navigate, Outlet, useLocation} from "react-router-dom";
import Cookies from "js-cookie";
import {Role} from "../models/Role";
import React from "react";

const RequireAuth: React.FC<{ allowedRoles: string[] }> = ({ allowedRoles }) => {
    const user = Cookies.get('user') ? JSON.parse(Cookies.get('user') as string) : '';
    const location = useLocation();

    return (
        user?.roles?.find((role: Role) => allowedRoles.includes(role.name)) || allowedRoles.includes(user?.role?.name)
            ? <Outlet />
            : user
                ? <Navigate to="/unauthorized" state={{ from: location }} replace/>
                : <Navigate to="/login" state={{ from: location }} replace/>
    );
}

export default RequireAuth
