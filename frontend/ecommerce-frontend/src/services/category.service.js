import api from './api';

export const getCategories = async () => {
    try {
        const response = await api.get('categorias');
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getMainCategories = async () => {
    try {
        const response = await api.get('categorias/principales');
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getCategoryById = async (id) => {
    try {
        const response = await api.get(`categorias/${id}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getSubcategories = async (categoryId) => {
    try {
        const response = await api.get(`categorias/${categoryId}/subcategorias`);
        return response.data;
    } catch (error) {
        throw error;
    }
};
