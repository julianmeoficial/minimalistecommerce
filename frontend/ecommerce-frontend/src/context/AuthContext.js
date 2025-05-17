import React, { createContext, useState, useEffect } from 'react';
import { getCurrentUser } from '../services/auth.service';
import api from '../services/api';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [userDetails, setUserDetails] = useState(null);

    useEffect(() => {
        const initAuth = async () => {
            const user = getCurrentUser();
            if (user) {
                setCurrentUser(user);
                try {
                    // Intentar obtener detalles del usuario si está autenticado
                    const response = await api.get('usuarios/me');
                    setUserDetails(response.data);
                } catch (error) {
                    console.error("Error al obtener detalles del usuario:", error);
                }
            }
            setLoading(false);
        };

        initAuth();
    }, []);

    const value = {
        currentUser,
        setCurrentUser,
        userDetails,
        setUserDetails,
        loading
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};
