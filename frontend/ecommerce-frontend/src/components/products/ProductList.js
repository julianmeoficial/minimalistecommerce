import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { getProducts, getProductsByCategory } from '../../services/product.service';
import { getCategories } from '../../services/category.service';
import InteractiveLoader from '../common/InteractiveLoader';
import './ProductList.css';

const ProductList = () => {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState('all');
    const location = useLocation();

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const data = await getCategories();
                setCategories(data);
            } catch (error) {
                console.error('Error al cargar categorías:', error);
                setError(true);
            }
        };

        fetchCategories();
    }, []);

    useEffect(() => {
        const fetchProducts = async () => {
            setLoading(true);
            try {
                const queryParams = new URLSearchParams(location.search);
                const categoryParam = queryParams.get('category');

                if (categoryParam) {
                    setSelectedCategory(categoryParam);
                    const data = await getProductsByCategory(categoryParam);
                    setProducts(data);
                } else {
                    setSelectedCategory('all');
                    const data = await getProducts();
                    setProducts(data);
                }
            } catch (error) {
                console.error('Error al cargar productos:', error);
                setError(true);
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, [location.search]);

    const handleCategoryChange = (e) => {
        const value = e.target.value;
        if (value === 'all') {
            window.history.pushState({}, '', '/dashboard/products');
            window.dispatchEvent(new Event('popstate'));
        } else {
            window.history.pushState({}, '', `/dashboard/products?category=${value}`);
            window.dispatchEvent(new Event('popstate'));
        }
    };

    if (error) {
        return <InteractiveLoader />;
    }

    if (loading) {
        return <div className="loading-container">
            <div className="loading-spinner"></div>
            <p>Cargando productos...</p>
        </div>;
    }

    return (
        <div className="product-list-container">
            <div className="product-list-header">
                <h1>Catálogo de Productos</h1>
                <div className="filter-container">
                    <label htmlFor="category-filter">Filtrar por categoría:</label>
                    <select
                        id="category-filter"
                        value={selectedCategory}
                        onChange={handleCategoryChange}
                    >
                        <option value="all">Todas las categorías</option>
                        {categories.map(category => (
                            <option key={category.categoriaId} value={category.categoriaId}>
                                {category.nombre}
                            </option>
                        ))}
                    </select>
                </div>
            </div>

            {products.length === 0 ? (
                <div className="no-products">
                    <div className="no-products-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" viewBox="0 0 16 16">
                            <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                            <path d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>
                        </svg>
                    </div>
                    <p>No se encontraron productos en esta categoría.</p>
                    <Link to="/dashboard" className="back-to-home">Volver al inicio</Link>
                </div>
            ) : (
                <div className="products-grid">
                    {products.map(product => (
                        <div key={product.productoId} className="product-card">
                            <div className="product-image">
                                {product.imagenes && product.imagenes.length > 0 ? (
                                    <img
                                        src={product.imagenes.find(img => img.esPrincipal)?.url || product.imagenes[0].url}
                                        alt={product.productoNombre}
                                    />
                                ) : (
                                    <div className="no-image">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" viewBox="0 0 16 16">
                                            <path d="M6.002 5.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z"/>
                                            <path d="M2.002 1a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2h-12zm12 1a1 1 0 0 1 1 1v6.5l-3.777-1.947a.5.5 0 0 0-.577.093l-3.71 3.71-2.66-1.772a.5.5 0 0 0-.63.062L1.002 12V3a1 1 0 0 1 1-1h12z"/>
                                        </svg>
                                        <span>Sin imagen</span>
                                    </div>
                                )}
                            </div>
                            <div className="product-info">
                                <h3>{product.productoNombre}</h3>
                                <p className="product-price">${product.precio.toFixed(2)}</p>
                                <p className="product-stock">
                                    {product.stock > 0 ? (
                                        <span className="in-stock">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                        <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>
                      </svg>
                                            {product.stock} disponibles
                    </span>
                                    ) : (
                                        <span className="out-of-stock">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                        <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                        <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                      </svg>
                      Agotado
                    </span>
                                    )}
                                </p>
                                <Link
                                    to={`/dashboard/products/${product.productoId}`}
                                    className="view-product-btn"
                                >
                                    Ver Detalles
                                </Link>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ProductList;
