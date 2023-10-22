import {useEffect, useState} from "react";
import {Alert, Col, Container, Row} from "react-bootstrap";
import axios from "axios";
import jwt_decode from "jwt-decode";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../auth/AuthContext";
import getLoggedUser from "../auth/auth";
import {Student} from "../models/Models";

const LoginPage = () => {
    const [user, setUser] = useState({});
    // @ts-ignore
    const {setCurrentUser} = useAuth();
    const navigate = useNavigate();
    const loggedInUser = getLoggedUser();
    const [showAlert, setShowAlert] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        if (loggedInUser) {
            setUser(loggedInUser);
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
            }
        )
    }, [setCurrentUser]);

    function handleGoogleCallbackResponse(response: any) {
        console.log("Encoded JWT ID token: " + response.credential);
        const id_token = response.credential;
        const decodedUser: any = jwt_decode(response.credential);
        console.log(decodedUser);

        axios.get(`http://localhost:8080/user/${decodedUser.email}/details`, {
            headers: {
                'Authorization': `Bearer ${id_token}`
            }
        })
            .then((res) => {
                const userObject: Student = res.data;
                setUser(userObject);
                setCurrentUser(JSON.stringify(userObject));
                setShowAlert(false)
                setErrorMessage('')
                navigate("/");
            })
            .catch((error) => {
                console.error(error);
                setErrorMessage(error.response.data.message)
                setShowAlert(true)
            })
    }

    return (
        <div className="login page">
            <Container>
                <Row>
                    <Col md={6}>
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
            <Alert show={showAlert} variant="danger">
                <Alert.Heading>Something went wrong!</Alert.Heading>
                {errorMessage}
            </Alert>
        </div>
    );
}

export default LoginPage;
