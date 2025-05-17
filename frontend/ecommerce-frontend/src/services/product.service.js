import api from './api';

export const getProducts = async (params = {}) => {
    try {
        const response = await api.get('productos', { params });
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getProductById = async (id) => {
    try {
        const response = await api.get(`productos/${id}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getProductsByCategory = async (categoryId) => {
    try {
        const response = await api.get(`productos/categoria/${categoryId}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const createProduct = async (productData) => {
    try {
        const response = await api.post('productos', productData);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const updateProduct = async (id, productData) => {
    try {
        const response = await api.put(`productos/${id}`, productData);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const deleteProduct = async (id) => {
    try {
        await api.delete(`productos/${id}`);
        return true;
    } catch (error) {
        throw error;
    }
};
