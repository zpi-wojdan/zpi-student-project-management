import React from 'react';
import {useNavigate} from "react-router-dom";
import {Button, Card, Container} from "react-bootstrap";
import {useTranslation} from "react-i18next";

const ServerError = () => {
    const navigate = useNavigate();
    const { i18n, t } = useTranslation();
    const comeBack = () => navigate(-1);

    return (
        <Container className="mt-5">
            <Card>
                <Card.Body>
                    <Card.Title>{t('networkErrorPage.title')}</Card.Title>
                    <Card.Text>{t('networkErrorPage.description')}</Card.Text>
                    <Button onClick={comeBack} className="active">{t('general.management.refresh')}</Button>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default ServerError;
