import React, { useState, useEffect, useContext } from 'react';
import { Link, useLocation } from 'react-router-dom';
import api from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import InteractiveLoader from '../common/InteractiveLoader';
import './Home.css';

const Home = () => {
    const [featuredProducts, setFeaturedProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { userDetails } = useContext(AuthContext);
    const location = useLocation();

    // Verificar si estamos en la ruta principal del dashboard
    const isMainDashboard = location.pathname === '/dashboard';

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [productsResponse, categoriesResponse] = await Promise.all([
                    api.get('productos?limit=4'),
                    api.get('categorias/principales')
                ]);

                setFeaturedProducts(productsResponse.data);
                setCategories(categoriesResponse.data);
            } catch (error) {
                console.error('Error al cargar datos:', error);
                setError('No se pudieron cargar los datos. Intente de nuevo más tarde.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (loading) {
        return <div className="loading">Cargando...</div>;
    }

    // Usar InteractiveLoader en lugar de mostrar el mensaje de error
    if (error || featuredProducts.length === 0 || isMainDashboard) {
        return <InteractiveLoader />;
    }

    return (
        <div className="home-container">
            <section className="welcome-section">
                <h1>Bienvenido{userDetails ? `, ${userDetails.usuarioNombre}` : ''}</h1>
                <p>Descubre nuestra selección de productos de alta calidad</p>
            </section>

            <section className="featured-products">
                <h2>Productos Destacados</h2>
                <div className="products-grid">
                    {featuredProducts.map(product => (
                        <div key={product.productoId} className="product-card">
                            <div className="product-image">
                                {product.imagenes && product.imagenes.length > 0 ? (
                                    <img src={product.imagenes.find(img => img.esPrincipal)?.url || product.imagenes[0].url} alt={product.productoNombre} />
                                ) : (
                                    <div className="no-image">Sin imagen</div>
                                )}
                            </div>
                            <div className="product-info">
                                <h3>{product.productoNombre}</h3>
                                <p className="product-price">${product.precio.toFixed(2)}</p>
                                <Link to={`/dashboard/products/${product.productoId}`} className="view-product-btn">
                                    Ver Producto
                                </Link>
                            </div>
                        </div>
                    ))}
                </div>
                <div className="view-all-container">
                    <Link to="/dashboard/products" className="view-all-btn">Ver todos los productos</Link>
                </div>
            </section>

            {categories.length > 0 && (
                <section className="categories-section">
                    <h2>Categorías</h2>
                    <div className="categories-grid">
                        {categories.map(category => (
                            <Link
                                key={category.categoriaId}
                                to={`/dashboard/products?category=${category.categoriaId}`}
                                className="category-card"
                            >
                                <h3>{category.nombre}</h3>
                                <p>{category.descripcion}</p>
                            </Link>
                        ))}
                    </div>
                </section>
            )}
        </div>
    );
};

export default Home;
