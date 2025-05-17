import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import api from '../../services/api';
import './Cart.css';

const Cart = () => {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { userDetails } = useContext(AuthContext);

    useEffect(() => {
        const fetchCart = async () => {
            try {
                const response = await api.get('/carrito');
                setCart(response.data);
            } catch (err) {
                console.error("Error al cargar el carrito:", err);
                setError("No se pudo cargar el carrito. Intente de nuevo más tarde.");
            } finally {
                setLoading(false);
            }
        };

        fetchCart();
    }, []);

    const handleUpdateQuantity = async (itemId, newQuantity) => {
        if (newQuantity <= 0) {
            handleRemoveItem(itemId);
            return;
        }

        try {
            await api.put(`/carrito/items/${itemId}`, { cantidad: newQuantity });
            // Actualizar el carrito en el estado
            setCart(prevCart => ({
                ...prevCart,
                items: prevCart.items.map(item =>
                    item.itemId === itemId ? { ...item, cantidad: newQuantity } : item
                )
            }));
        } catch (err) {
            console.error("Error al actualizar cantidad:", err);
            setError("No se pudo actualizar la cantidad. Intente de nuevo.");
        }
    };

    const handleRemoveItem = async (itemId) => {
        try {
            await api.delete(`/carrito/items/${itemId}`);
            // Eliminar el item del carrito en el estado
            setCart(prevCart => ({
                ...prevCart,
                items: prevCart.items.filter(item => item.itemId !== itemId)
            }));
        } catch (err) {
            console.error("Error al eliminar item:", err);
            setError("No se pudo eliminar el producto. Intente de nuevo.");
        }
    };

    const handleEmptyCart = async () => {
        try {
            await api.delete('/carrito');
            setCart(prevCart => ({ ...prevCart, items: [] }));
        } catch (err) {
            console.error("Error al vaciar carrito:", err);
            setError("No se pudo vaciar el carrito. Intente de nuevo.");
        }
    };

    if (loading) {
        return <div className="loading">Cargando carrito...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    if (!cart || !cart.items || cart.items.length === 0) {
        return (
            <div className="empty-cart">
                <h2>Tu carrito está vacío</h2>
                <p>Agrega productos al carrito para comenzar a comprar.</p>
                <Link to="/dashboard/products" className="continue-shopping-btn">
                    Continuar comprando
                </Link>
            </div>
        );
    }

    return (
        <div className="cart-container">
            <h1>Tu Carrito de Compras</h1>

            <div className="cart-items">
                {cart.items.map(item => (
                    <div key={item.itemId} className="cart-item">
                        <div className="item-image">
                            {item.producto.imagenes && item.producto.imagenes.length > 0 ? (
                                <img
                                    src={item.producto.imagenes.find(img => img.esPrincipal)?.url || item.producto.imagenes[0].url}
                                    alt={item.producto.productoNombre}
                                />
                            ) : (
                                <div className="no-image">Sin imagen</div>
                            )}
                        </div>
                        <div className="item-details">
                            <h3>{item.producto.productoNombre}</h3>
                            <p className="item-price">${item.precioUnitario.toFixed(2)}</p>
                        </div>
                        <div className="item-quantity">
                            <button
                                onClick={() => handleUpdateQuantity(item.itemId, item.cantidad - 1)}
                                className="quantity-btn"
                            >
                                -
                            </button>
                            <span>{item.cantidad}</span>
                            <button
                                onClick={() => handleUpdateQuantity(item.itemId, item.cantidad + 1)}
                                className="quantity-btn"
                                disabled={item.cantidad >= item.producto.stock}
                            >
                                +
                            </button>
                        </div>
                        <div className="item-subtotal">
                            ${(item.cantidad * item.precioUnitario).toFixed(2)}
                        </div>
                        <button
                            onClick={() => handleRemoveItem(item.itemId)}
                            className="remove-btn"
                        >
                            Eliminar
                        </button>
                    </div>
                ))}
            </div>

            <div className="cart-summary">
                <div className="cart-total">
                    <span>Total:</span>
                    <span>${cart.items.reduce((total, item) => total + (item.cantidad * item.precioUnitario), 0).toFixed(2)}</span>
                </div>
                <div className="cart-actions">
                    <button onClick={handleEmptyCart} className="empty-cart-btn">
                        Vaciar carrito
                    </button>
                    <Link to="/dashboard/checkout" className="checkout-btn">
                        Proceder al pago
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default Cart;
