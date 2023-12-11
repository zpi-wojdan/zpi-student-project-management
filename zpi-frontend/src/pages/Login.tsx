import {useEffect, useState} from "react";
import {Alert, Col, Image, Row} from "react-bootstrap";
import jwt_decode from "jwt-decode";
import {useLocation, useNavigate} from "react-router-dom";
// @ts-ignore
import Cookies from "js-cookie";
import useAuth from "../auth/useAuth";
import api from "../utils/api";
import {useTranslation} from "react-i18next";
import api_access from "../utils/api_access";

const LoginPage = () => {
    // @ts-ignore
    const {auth, setAuth} = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const { i18n, t } = useTranslation();
    const from = location.state?.from?.pathname || "/";
    const [showAlert, setShowAlert] = useState(false);
    const [alertMessage, setAlertMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [alertMessageKey, setAlertMessageKey] = useState('');

    useEffect(() => {
        setAlertMessage(t(alertMessageKey));
    }, [i18n.language]);

    useEffect(() => {
        if(auth?.reasonOfLogout === 'token_expired') {
            setAlertMessage(t('login.sessionExpired'))
            setAlertMessageKey('login.sessionExpired')
            setShowAlert(true)
            setAuth(null)
        }
        if(auth?.reasonOfLogout === 'access_denied') {
            setAlertMessage(t('login.accessDenied'))
            setAlertMessageKey('login.accessDenied')
            setShowAlert(true)
            setAuth(null)
        }

        // @ts-ignore
        google.accounts.id.initialize({
            client_id: process.env.REACT_APP_GOOGLE_CLIENT_ID,
            callback: handleGoogleCallbackResponse
        })

        // @ts-ignore
        google.accounts.id.renderButton(
            document.getElementById("sign-in-button"),
            {
                theme: "filled_black",
                size: "large",
                locale: i18n.language,
            }
        )
    }, [i18n.language, auth]);

    function handleGoogleCallbackResponse(response: any) {
        console.log("Encoded JWT ID token: " + response.credential);
        Cookies.set("google_token", response.credential);
        const decodedUser: any = jwt_decode(response.credential);

        api.get(api_access + `user/${decodedUser.email}/details`)
            .then((res) => {
                Cookies.set('user', JSON.stringify(res.data));
                setShowAlert(false)
                setErrorMessage('')
                navigate(from, { replace: true });
            })
            .catch((error) => {
                Cookies.remove('google_token');
                setErrorMessage(error.response.data.message)
                setAlertMessage(t('login.loginError'))
                setAlertMessageKey('login.loginError')
                setShowAlert(true)
            })
    }

    return (
        <div className='login-page'>
            <Row className="h-100">
                <Col md={6} className="d-flex flex-column justify-content-center text-center">
                    <Alert show={showAlert} variant="danger">
                        <Alert.Heading>{alertMessage}</Alert.Heading>
                        {errorMessage}
                    </Alert>
                    <div className="container">
                        <h2 className="mb-3">{t('login.welcome')}</h2>
                        <div className="mb-5">
                            <p>{t('login.instruction')}</p>
                        </div>
                        <div id="sign-in-button" className="d-flex justify-content-center mt-5"></div>
                    </div>
                </Col>
                <Col md={6} className="d-none d-md-block h-100">
                    <div className="image-div">
                        <Image
                            src="/images/login-image.jpg"
                            alt={t('login.imageAlt')}
                            fluid
                            className="image"
                        />
                    </div>
                </Col>
            </Row>
        </div>
    );
}

export default LoginPage;
