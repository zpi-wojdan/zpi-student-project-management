import {useEffect, useState} from "react";
import {Alert, Col, Container, Row} from "react-bootstrap";
import axios from "axios";
import jwt_decode from "jwt-decode";
import {useNavigate} from "react-router-dom";
// @ts-ignore
import Cookies from "js-cookie";

const LoginPage = () => {
    const navigate = useNavigate();
    const [showAlert, setShowAlert] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
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
            }
        )
    }, []);

    function handleGoogleCallbackResponse(response: any) {
        console.log("Encoded JWT ID token: " + response.credential);
        Cookies.set("google_token", response.credential);
        const decodedUser: any = jwt_decode(response.credential);
        console.log(decodedUser);

        axios.get(`http://localhost:8080/user/${decodedUser.email}/details`, {
            headers: {
                'Authorization': `Bearer ${Cookies.get('google_token')}`
            }
        })
            .then((res) => {
                Cookies.set('user', JSON.stringify(res.data));
                setShowAlert(false)
                setErrorMessage('')
                navigate("/");
            })
            .catch((error) => {
                console.error(error);
                Cookies.remove('google_token');
                setErrorMessage(error.response.data.message)
                setShowAlert(true)
            })
    }

    return (
        <div className="login page">
            <Container>
                <Row>
                    <Col md={6}>
                        <Alert show={showAlert} variant="danger">
                            <Alert.Heading>Wystąpił błąd logowania!</Alert.Heading>
                            {errorMessage}
                        </Alert>
                        <div className="container">
                            <h2>Witaj w systemie logowania</h2>
                            <div id="sign-in-prompt">
                                <p>Kliknij przycisk poniżej, aby zalogować się za pomocą uczelnianego adresu email.</p>
                            </div>
                            <div id="sign-in-button"></div>
                        </div>
                    </Col>
                    <Col md={6} className="d-none d-md-block">
                        <div className="login-image">
                            <img
                                src="images/login-img.JPG"
                                alt="Budynek A1 z logo PWr"
                            />
                        </div>
                    </Col>
                </Row>
            </Container>
        </div>
    );
}

export default LoginPage;
