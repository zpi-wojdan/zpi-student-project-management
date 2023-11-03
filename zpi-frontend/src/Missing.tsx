import React from 'react';
import {useNavigate} from "react-router-dom";
import {Button, Card, Container} from "react-bootstrap";

const Missing = () => {
    const navigate = useNavigate();

    const comeBack = () => navigate(-1);

    return (
        <Container className="mt-5">
            <Card>
                <Card.Body>
                    <Card.Title>Page not found</Card.Title>
                    <Card.Text>The page you are looking for does not exist.</Card.Text>
                    <Button onClick={comeBack} className="active">Go back</Button>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default Missing;