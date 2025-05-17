import React from 'react';
import { Link } from 'react-router-dom';
import './NotFound404.css';

const NotFound404 = () => {
    return (
        <div className="not-found-container">
            <h1>404</h1>
            <div className="cloak__wrapper">
                <div className="cloak__container">
                    <div className="cloak"></div>
                </div>
            </div>
            <div className="info">
                <h2>Aquí no hay nada todavía</h2>
                <p>Cuando me paguen lo suficiente voy a optimizar esto.</p>
                <Link to="/dashboard">Este botón sí sirve</Link>
            </div>
        </div>
    );
};

export default NotFound404;
