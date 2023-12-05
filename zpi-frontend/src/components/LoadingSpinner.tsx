import { Spinner, Container, Row, Col } from 'react-bootstrap';
import React from 'react';
import {useTranslation} from "react-i18next";

const LoadingSpinner: React.FC<{ height: string }> = ({ height }) => {
    const { i18n, t } = useTranslation();

    return (
        <Container fluid style={{ height: height }}>
            <Row className="h-100 justify-content-center align-items-center">
                <Col className="text-center">
                    <Spinner animation="border" className="custom-spinner" />
                    <p className="spinner-text">{t('general.management.load')}</p>
                </Col>
            </Row>
        </Container>
    );
};

export default LoadingSpinner;
