import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProductById } from '../../services/product.service';
import { AuthContext } from '../../context/AuthContext';
import api from '../../services/api';
import './ProductDetail.css';

const ProductDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { currentUser, userDetails } = useContext(AuthContext);
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [activeImage, setActiveImage] = useState(0);
    const [addingToCart, setAddingToCart] = useState(false);

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const data = await getProductById(id);
                setProduct(data);
            } catch (error) {
                console.error('Error al cargar el producto:', error);
                setError('No se pudo cargar el producto. Intente de nuevo más tarde.');
            } finally {
                setLoading(false);
            }
        };

        fetchProduct();
    }, [id]);

    const handleQuantityChange = (e) => {
        const value = parseInt(e.target.value);
        if (value > 0 && value <= product.stock) {
            setQuantity(value);
        }
    };

    const increaseQuantity = () => {
        if (quantity < product.stock) {
            setQuantity(quantity + 1);
        }
    };

    const decreaseQuantity = () => {
        if (quantity > 1) {
            setQuantity(quantity - 1);
        }
    };

    const handleAddToCart = async () => {
        if (!currentUser) {
            navigate('/login');
            return;
        }

        if (userDetails?.rol?.nombre !== 'COMPRADOR') {
            alert('Solo los compradores pueden añadir productos al carrito.');
            return;
        }

        setAddingToCart(true);
        try {
            await api.post('/carrito/items', {
                productold: product.productold,
                cantidad: quantity
            });
            alert('Producto añadido al carrito');
            navigate('/dashboard/cart');
        } catch (error) {
            console.error('Error al añadir al carrito:', error);
            alert('Error al añadir el producto al carrito. Intente de nuevo.');
        } finally {
            setAddingToCart(false);
        }
    };

    if (loading) {
        return <div className="loading">Cargando producto...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    if (!product) {
        return <div className="error-message">Producto no encontrado</div>;
    }

    return (
        <div className="product-detail-container">
            <div className="product-detail-grid">
                <div className="product-images">
                    <div className="main-image">
                        {product.imagenes && product.imagenes.length > 0 ? (
                            <img
                                src={product.imagenes[activeImage].url}
                                alt={product.productoNombre}
                            />
                        ) : (
                            <div className="no-image">Sin imagen</div>
                        )}
                    </div>
                    {product.imagenes && product.imagenes.length > 1 && (
                        <div className="thumbnail-images">
                            {product.imagenes.map((image, index) => (
                                <div
                                    key={image.imagenld}
                                    className={`thumbnail ${activeImage === index ? 'active' : ''}`}
                                    onClick={() => setActiveImage(index)}
                                >
                                    <img src={image.url} alt={`Thumbnail ${index + 1}`} />
                                </div>
                            ))}
                        </div>
                    )}
                </div>
                <div className="product-info">
                    <h1>{product.productoNombre}</h1>
                    <p className="product-price">${product.precio.toFixed(2)}</p>
                    <div className="product-stock">
                        {product.stock > 0 ? (
                            <span className="in-stock">{product.stock} disponibles</span>
                        ) : (
                            <span className="out-of-stock">Agotado</span>
                        )}
                    </div>
                    <div className="product-description">
                        <h3>Descripción</h3>
                        <p>{product.descripcion || 'No hay descripción disponible para este producto.'}</p>
                    </div>
                    <div className="product-category">
                        <span>Categoría: </span>
                        <span>{product.categoria?.nombre || 'Sin categoría'}</span>
                    </div>
                    <div className="product-vendor">
                        <span>Vendedor: </span>
                        <span>{product.vendedor?.usuarioNombre || 'Desconocido'}</span>
                    </div>

                    {product.stock > 0 && (
                        <div className="product-actions">
                            <div className="quantity-selector">
                                <button onClick={decreaseQuantity} disabled={quantity <= 1}>-</button>
                                <input
                                    type="number"
                                    value={quantity}
                                    onChange={handleQuantityChange}
                                    min="1"
                                    max={product.stock}
                                />
                                <button onClick={increaseQuantity} disabled={quantity >= product.stock}>+</button>
                            </div>
                            <button
                                className="add-to-cart-btn"
                                onClick={handleAddToCart}
                                disabled={addingToCart}
                            >
                                {addingToCart ? 'Añadiendo...' : 'Añadir al carrito'}
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProductDetail;
