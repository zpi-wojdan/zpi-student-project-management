// @ts-ignore
import Cookies from "js-cookie";

const handleSignOut = () => {
    console.log("Signed out");
    Cookies.remove('user');
    Cookies.remove('google_token');
}

export default handleSignOut;
