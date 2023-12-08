// @ts-ignore
import Cookies from "js-cookie";

function handleSignOut (navigate: any) {
    Cookies.remove('user');
    Cookies.remove('google_token');
    navigate("/login");
}

export default handleSignOut;
