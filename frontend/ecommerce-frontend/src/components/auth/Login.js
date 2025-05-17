import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import { login, register } from '../../services/auth.service';
import './Login.css';

const Login = () => {
    const navigate = useNavigate();
    const { setCurrentUser } = useContext(AuthContext);
    const [isSignUp, setIsSignUp] = useState(false);
    const [loginData, setLoginData] = useState({ email: '', password: '' });
    const [registerData, setRegisterData] = useState({
        nombre: '',
        email: '',
        password: '',
        rolId: 2 // Por defecto, rol de COMPRADOR
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleLoginChange = (e) => {
        const { name, value } = e.target;
        setLoginData({ ...loginData, [name]: value });
    };

    const handleRegisterChange = (e) => {
        const { name, value } = e.target;
        setRegisterData({ ...registerData, [name]: value });
    };

    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await login(loginData);
            localStorage.setItem('token', response.token);
            setCurrentUser({ token: response.token });
            navigate('/dashboard');
        } catch (err) {
            console.error("Error de login:", err);
            setError(err.response?.data?.mensaje || 'Credenciales inválidas. Intente de nuevo.');
        } finally {
            setLoading(false);
        }
    };

    const handleRegisterSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await register(registerData);
            localStorage.setItem('token', response.token);
            setCurrentUser({ token: response.token });
            navigate('/dashboard');
        } catch (err) {
            console.error("Error de registro:", err);
            setError(err.response?.data?.mensaje || 'Error al registrarse. Intente de nuevo.');
        } finally {
            setLoading(false);
        }
    };

    const toggleForm = () => {
        setIsSignUp(!isSignUp);
        setError('');
    };

    return (
        <div className="login-container">
            <div className="wrapper">
                <div className="card-switch">
                    <label className="switch">
                        <input
                            type="checkbox"
                            className="toggle"
                            checked={isSignUp}
                            onChange={toggleForm}
                        />
                        <span className="slider"></span>
                        <span className="card-side"></span>
                        <div className="flip-card__inner">
                            <div className="flip-card__front">
                                <div className="title">Iniciar Sesión</div>
                                <form className="flip-card__form" onSubmit={handleLoginSubmit}>
                                    <input
                                        className="flip-card__input"
                                        name="email"
                                        placeholder="Email"
                                        type="email"
                                        value={loginData.email}
                                        onChange={handleLoginChange}
                                        required
                                        disabled={loading}
                                    />
                                    <input
                                        className="flip-card__input"
                                        name="password"
                                        placeholder="Contraseña"
                                        type="password"
                                        value={loginData.password}
                                        onChange={handleLoginChange}
                                        required
                                        disabled={loading}
                                    />
                                    {error && <div className="error-message">{error}</div>}
                                    <button
                                        className="flip-card__btn"
                                        type="submit"
                                        disabled={loading}
                                    >
                                        {loading ? 'Procesando...' : 'Ingresar'}
                                    </button>
                                </form>
                            </div>
                            <div className="flip-card__back">
                                <div className="title">Registrarse</div>
                                <form className="flip-card__form" onSubmit={handleRegisterSubmit}>
                                    <input
                                        className="flip-card__input"
                                        name="nombre"
                                        placeholder="Nombre completo"
                                        type="text"
                                        value={registerData.nombre}
                                        onChange={handleRegisterChange}
                                        required
                                        disabled={loading}
                                    />
                                    <input
                                        className="flip-card__input"
                                        name="email"
                                        placeholder="Email"
                                        type="email"
                                        value={registerData.email}
                                        onChange={handleRegisterChange}
                                        required
                                        disabled={loading}
                                    />
                                    <input
                                        className="flip-card__input"
                                        name="password"
                                        placeholder="Contraseña"
                                        type="password"
                                        value={registerData.password}
                                        onChange={handleRegisterChange}
                                        required
                                        disabled={loading}
                                    />
                                    {error && <div className="error-message">{error}</div>}
                                    <button
                                        className="flip-card__btn"
                                        type="submit"
                                        disabled={loading}
                                    >
                                        {loading ? 'Procesando...' : 'Confirmar'}
                                    </button>
                                </form>
                            </div>
                        </div>
                    </label>
                </div>
            </div>
        </div>
    );
};

export default Login;
