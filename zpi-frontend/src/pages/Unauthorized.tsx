import React from 'react';
import {useNavigate} from "react-router-dom";
import {Button, Card, Container} from "react-bootstrap";

const Unauthorized = () => {
    const navigate = useNavigate();
    const comeBack = () => navigate(-1);

    return (
        <Container className="mt-5">
            <Card>
                <Card.Body>
                    <Card.Title>Unauthorized Access</Card.Title>
                    <Card.Text>You are not authorized to access this page.</Card.Text>
                    <Button onClick={comeBack} className="active">Go back</Button>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default Unauthorized;
