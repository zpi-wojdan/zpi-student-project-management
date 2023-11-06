// @ts-ignore
import Cookies from "js-cookie";

function handleSignOut (navigate: any) {
    console.log("Signed out");
    Cookies.remove('user');
    Cookies.remove('google_token');
    Cookies.remove('lang');
    navigate("/login");
}

export default handleSignOut;
