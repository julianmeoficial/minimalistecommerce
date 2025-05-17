import React, { useContext } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';

const PrivateRoute = ({ children, requiredRoles = [] }) => {
    const { currentUser, userDetails, loading } = useContext(AuthContext);
    const location = useLocation();

    if (loading) {
        return <div>Cargando...</div>;
    }

    if (!currentUser) {
        // Redirigir a la página de login si no hay usuario autenticado
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    // Si hay roles requeridos y tenemos detalles del usuario, verificar
    if (requiredRoles.length > 0 && userDetails) {
        const hasRequiredRole = requiredRoles.includes(userDetails.rol.nombre);
        if (!hasRequiredRole) {
            // Redirigir a la página de acceso denegado
            return <Navigate to="/forbidden" replace />;
        }
    }

    return children;
};

export default PrivateRoute;
