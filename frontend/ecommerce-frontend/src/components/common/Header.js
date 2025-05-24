import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import { logout } from '../../services/auth.service';
import LogoutButton from './LogoutButton';
import './DashboardHeader.css';

const DashboardHeader = () => {
    const { currentUser, setCurrentUser, userDetails } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        setCurrentUser(null);
        navigate('/');
    };

    return (
        <>
            <header className="dashboard-header">
                <div className="header-container">
                    <div className="logo">
                        <Link to="/">
                            <span className="logo-text">Mecommerces</span>
                        </Link>
                    </div>

                    {currentUser && (
                        <nav className="navigation">
                            <ul>
                                <li><Link to="/dashboard">Dashboard</Link></li>
                                <li><Link to="/error500">Productos</Link></li>
                                {userDetails?.rol?.nombre === 'COMPRADOR' && (
                                    <li><Link to="/dashboard/cart">Carrito</Link></li>
                                )}
                                {userDetails?.rol?.nombre === 'VENDEDOR' && (
                                    <li><Link to="/dashboard/mis-productos">Mis Productos</Link></li>
                                )}
                                {userDetails?.rol?.nombre === 'ADMINISTRADOR' && (
                                    <li><Link to="/dashboard/admin">Administración</Link></li>
                                )}
                                <li><Link to="/dashboard/profile">Perfil</Link></li>
                            </ul>
                        </nav>
                    )}

                    <div className="user-actions">
                        {userDetails && (
                            <span className="user-name">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4zm-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10c-2.29 0-3.516.68-4.168 1.332-.678.678-.83 1.418-.832 1.664h10z"/>
                </svg>
                                {userDetails?.usuarioNombre || 'Usuario'}
              </span>
                        )}

                        {currentUser && (
                            <div onClick={handleLogout} className="logout-container">
                                <LogoutButton />
                            </div>
                        )}
                    </div>
                </div>
            </header>
            <div className="header-divider"></div>
        </>
    );
};

export default DashboardHeader;
