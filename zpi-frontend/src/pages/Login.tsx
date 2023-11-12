import {useEffect, useState} from "react";
import {Alert, Col, Container, Row} from "react-bootstrap";
import jwt_decode from "jwt-decode";
import {useLocation, useNavigate} from "react-router-dom";
// @ts-ignore
import Cookies from "js-cookie";
import useAuth from "../auth/useAuth";
import api from "../utils/api";
import {useTranslation} from "react-i18next";

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
            client_id: "333365127566-llqb3rl4kcvvcnurr7cih7s126iu0e5v.apps.googleusercontent.com",
            callback: handleGoogleCallbackResponse
        })

        // @ts-ignore
        google.accounts.id.renderButton(
            document.getElementById("sign-in-button"),
            {
                theme: "filled_black",
                size: "large",
                width: document.getElementById("sign-in-button")?.offsetWidth,
                locale: i18n.language,
            }
        )
    }, [i18n.language, auth]);

    function handleGoogleCallbackResponse(response: any) {
        console.log("Encoded JWT ID token: " + response.credential);
        Cookies.set("google_token", response.credential);
        const decodedUser: any = jwt_decode(response.credential);
        console.log(decodedUser);

        api.get(`http://localhost:8080/user/${decodedUser.email}/details`)
            .then((res) => {
                Cookies.set('user', JSON.stringify(res.data));
                setShowAlert(false)
                setErrorMessage('')
                navigate(from, { replace: true });
            })
            .catch((error) => {
                console.error(error);
                Cookies.remove('google_token');
                setErrorMessage(error.response.data.message)
                setAlertMessage(t('login.loginError'))
                setAlertMessageKey('login.loginError')
                setShowAlert(true)
            })
    }

    return (
        <div className="login page">
            <Container>
                <Row>
                    <Col md={6}>
                        <Alert show={showAlert} variant="danger">
                            <Alert.Heading>{alertMessage}</Alert.Heading>
                            {errorMessage}
                        </Alert>
                        <div className="container">
                            <h2>{t('login.welcome')}</h2>
                            <div id="sign-in-prompt">
                                <p>{t('login.instruction')}</p>
                            </div>
                            <div id="sign-in-button"></div>
                        </div>
                    </Col>
                    <Col md={6} className="d-none d-md-block">
                        <div className="login-image">
                            <img
                                src="images/login-img.JPG"
                                alt={t('login.imageAlt')}
                            />
                        </div>
                    </Col>
                </Row>
            </Container>
        </div>
    );
}

export default LoginPage;
