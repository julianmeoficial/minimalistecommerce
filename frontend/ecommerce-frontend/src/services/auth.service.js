import api from './api';

// Configura los endpoints para autenticación
export const login = async (credentials) => {
    try {
        const response = await api.post('auth/login', credentials);
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
        }
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const register = async (userData) => {
    try {
        const response = await api.post('auth/registro', userData);
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
        }
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const logout = () => {
    localStorage.removeItem('token');
};

export const getCurrentUser = () => {
    const token = localStorage.getItem('token');
    if (token) {
        // Implementar lógica para decodificar token si es necesario
        return { token };
    }
    return null;
};
