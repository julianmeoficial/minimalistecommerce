import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import api from '../../services/api';
import './Checkout.css';

const Checkout = () => {
    const navigate = useNavigate();
    const { userDetails } = useContext(AuthContext);
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [orderProcessing, setOrderProcessing] = useState(false);
    const [shippingInfo, setShippingInfo] = useState({
        nombre: userDetails?.usuarioNombre || '',
        direccion: '',
        ciudad: '',
        codigoPostal: '',
        telefono: ''
    });

    useEffect(() => {
        const fetchCart = async () => {
            try {
                const response = await api.get('/carrito');
                if (!response.data || !response.data.items || response.data.items.length === 0) {
                    navigate('/dashboard/cart');
                    return;
                }
                setCart(response.data);
            } catch (err) {
                console.error("Error al cargar el carrito:", err);
                setError("No se pudo cargar el carrito. Intente de nuevo más tarde.");
            } finally {
                setLoading(false);
            }
        };

        fetchCart();
    }, [navigate]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setShippingInfo(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setOrderProcessing(true);

        try {
            const response = await api.post('/carrito/checkout');
            navigate('/dashboard/order-confirmation', {
                state: {
                    order: response.data,
                    shippingInfo
                }
            });
        } catch (err) {
            console.error("Error al procesar la orden:", err);
            setError("No se pudo procesar la orden. Intente de nuevo más tarde.");
            setOrderProcessing(false);
        }
    };

    if (loading) {
        return <div className="loading">Cargando información de checkout...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="checkout-container">
            <h1>Finalizar Compra</h1>

            <div className="checkout-grid">
                <div className="checkout-form-container">
                    <h2>Información de Envío</h2>
                    <form onSubmit={handleSubmit} className="checkout-form">
                        <div className="form-group">
                            <label htmlFor="nombre">Nombre completo</label>
                            <input
                                type="text"
                                id="nombre"
                                name="nombre"
                                value={shippingInfo.nombre}
                                onChange={handleInputChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="direccion">Dirección</label>
                            <input
                                type="text"
                                id="direccion"
                                name="direccion"
                                value={shippingInfo.direccion}
                                onChange={handleInputChange}
                                required
                            />
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="ciudad">Ciudad</label>
                                <input
                                    type="text"
                                    id="ciudad"
                                    name="ciudad"
                                    value={shippingInfo.ciudad}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="codigoPostal">Código Postal</label>
                                <input
                                    type="text"
                                    id="codigoPostal"
                                    name="codigoPostal"
                                    value={shippingInfo.codigoPostal}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label htmlFor="telefono">Teléfono</label>
                            <input
                                type="tel"
                                id="telefono"
                                name="telefono"
                                value={shippingInfo.telefono}
                                onChange={handleInputChange}
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            className="place-order-btn"
                            disabled={orderProcessing}
                        >
                            {orderProcessing ? 'Procesando...' : 'Confirmar Pedido'}
                        </button>
                    </form>
                </div>

                <div className="order-summary">
                    <h2>Resumen del Pedido</h2>
                    <div className="order-items">
                        {cart.items.map(item => (
                            <div key={item.itemId} className="order-item">
                                <div className="item-name">
                                    <span>{item.cantidad}x</span> {item.producto.productoNombre}
                                </div>
                                <div className="item-price">
                                    ${(item.cantidad * item.precioUnitario).toFixed(2)}
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className="order-totals">
                        <div className="subtotal">
                            <span>Subtotal:</span>
                            <span>${cart.items.reduce((total, item) => total + (item.cantidad * item.precioUnitario), 0).toFixed(2)}</span>
                        </div>
                        <div className="shipping">
                            <span>Envío:</span>
                            <span>Gratis</span>
                        </div>
                        <div className="total">
                            <span>Total:</span>
                            <span>${cart.items.reduce((total, item) => total + (item.cantidad * item.precioUnitario), 0).toFixed(2)}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Checkout;
