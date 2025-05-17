import React, { useState, useEffect } from 'react';
import { Routes, Route, Link, useNavigate } from 'react-router-dom';
import api from '../../services/api';
import './AdminPanel.css';

// Componentes para las diferentes secciones del panel de administración
const Dashboard = () => {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const response = await api.get('/admin/dashboard');
                setStats(response.data);
            } catch (err) {
                console.error("Error al cargar el dashboard:", err);
                setError("No se pudieron cargar los datos del dashboard. Intente de nuevo más tarde.");
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, []);

    if (loading) {
        return <div className="loading">Cargando datos del dashboard...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="admin-dashboard">
            <h2>Dashboard de Administración</h2>

            <div className="stats-grid">
                <div className="stat-card">
                    <h3>Usuarios</h3>
                    <div className="stat-value">{stats.totalUsuarios}</div>
                    <div className="stat-details">
                        {Object.entries(stats.usuariosPorRol).map(([rol, count]) => (
                            <div key={rol} className="stat-detail">
                                <span>{rol}:</span>
                                <span>{count}</span>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="stat-card">
                    <h3>Productos</h3>
                    <div className="stat-value">{stats.totalProductos}</div>
                    <div className="stat-details">
                        {Object.entries(stats.productosPorCategoria).map(([categoria, count]) => (
                            <div key={categoria} className="stat-detail">
                                <span>{categoria}:</span>
                                <span>{count}</span>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="stat-card">
                    <h3>Órdenes</h3>
                    <div className="stat-value">{stats.totalOrdenes}</div>
                    <div className="stat-details">
                        {Object.entries(stats.ordenesPorEstado).map(([estado, count]) => (
                            <div key={estado} className="stat-detail">
                                <span>{estado}:</span>
                                <span>{count}</span>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="stat-card">
                    <h3>Ingresos Totales</h3>
                    <div className="stat-value">${stats.ingresosTotales.toFixed(2)}</div>
                </div>
            </div>
        </div>
    );
};

const UsersList = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await api.get('/admin/usuarios');
                setUsers(response.data);
            } catch (err) {
                console.error("Error al cargar usuarios:", err);
                setError("No se pudieron cargar los usuarios. Intente de nuevo más tarde.");
            } finally {
                setLoading(false);
            }
        };

        fetchUsers();
    }, []);

    const handleDeleteUser = async (userId) => {
        if (window.confirm('¿Está seguro de que desea eliminar este usuario?')) {
            try {
                await api.delete(`/admin/usuarios/${userId}`);
                setUsers(users.filter(user => user.usuarioId !== userId));
            } catch (err) {
                console.error("Error al eliminar usuario:", err);
                alert("No se pudo eliminar el usuario. Intente de nuevo más tarde.");
            }
        }
    };

    if (loading) {
        return <div className="loading">Cargando usuarios...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="users-list">
            <h2>Gestión de Usuarios</h2>

            <table className="admin-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Email</th>
                    <th>Rol</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                {users.map(user => (
                    <tr key={user.usuarioId}>
                        <td>{user.usuarioId}</td>
                        <td>{user.usuarioNombre}</td>
                        <td>{user.email}</td>
                        <td>{user.rol?.nombre}</td>
                        <td className="actions-cell">
                            <Link to={`/dashboard/admin/users/${user.usuarioId}`} className="edit-btn">
                                Editar
                            </Link>
                            <button
                                onClick={() => handleDeleteUser(user.usuarioId)}
                                className="delete-btn"
                            >
                                Eliminar
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

const ProductsList = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await api.get('/admin/productos');
                setProducts(response.data);
            } catch (err) {
                console.error("Error al cargar productos:", err);
                setError("No se pudieron cargar los productos. Intente de nuevo más tarde.");
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

    const handleDeleteProduct = async (productId) => {
        if (window.confirm('¿Está seguro de que desea eliminar este producto?')) {
            try {
                await api.delete(`/admin/productos/${productId}`);
                setProducts(products.filter(product => product.productoId !== productId));
            } catch (err) {
                console.error("Error al eliminar producto:", err);
                alert("No se pudo eliminar el producto. Intente de nuevo más tarde.");
            }
        }
    };

    if (loading) {
        return <div className="loading">Cargando productos...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="products-list">
            <h2>Gestión de Productos</h2>

            <table className="admin-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Precio</th>
                    <th>Stock</th>
                    <th>Categoría</th>
                    <th>Vendedor</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                {products.map(product => (
                    <tr key={product.productoId}>
                        <td>{product.productoId}</td>
                        <td>{product.productoNombre}</td>
                        <td>${product.precio.toFixed(2)}</td>
                        <td>{product.stock}</td>
                        <td>{product.categoria?.nombre}</td>
                        <td>{product.vendedor?.usuarioNombre}</td>
                        <td className="actions-cell">
                            <Link to={`/dashboard/admin/products/${product.productoId}`} className="edit-btn">
                                Editar
                            </Link>
                            <button
                                onClick={() => handleDeleteProduct(product.productoId)}
                                className="delete-btn"
                            >
                                Eliminar
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

const OrdersList = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const response = await api.get('/admin/ordenes');
                setOrders(response.data);
            } catch (err) {
                console.error("Error al cargar órdenes:", err);
                setError("No se pudieron cargar las órdenes. Intente de nuevo más tarde.");
            } finally {
                setLoading(false);
            }
        };

        fetchOrders();
    }, []);

    const handleUpdateOrderStatus = async (orderId, newStatus) => {
        try {
            await api.put(`/admin/ordenes/${orderId}/estado`, { estado: newStatus });
            setOrders(orders.map(order =>
                order.ordenId === orderId ? { ...order, estado: newStatus } : order
            ));
        } catch (err) {
            console.error("Error al actualizar estado de la orden:", err);
            alert("No se pudo actualizar el estado de la orden. Intente de nuevo más tarde.");
        }
    };

    if (loading) {
        return <div className="loading">Cargando órdenes...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="orders-list">
            <h2>Gestión de Órdenes</h2>

            <table className="admin-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Fecha</th>
                    <th>Cliente</th>
                    <th>Total</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                {orders.map(order => (
                    <tr key={order.ordenId}>
                        <td>{order.ordenId}</td>
                        <td>{new Date(order.fechaCreacion).toLocaleDateString()}</td>
                        <td>{order.usuario?.usuarioNombre}</td>
                        <td>${order.total.toFixed(2)}</td>
                        <td>
                            <select
                                value={order.estado}
                                onChange={(e) => handleUpdateOrderStatus(order.ordenId, e.target.value)}
                                className="status-select"
                            >
                                <option value="PENDIENTE">Pendiente</option>
                                <option value="PROCESANDO">Procesando</option>
                                <option value="ENVIADO">Enviado</option>
                                <option value="ENTREGADO">Entregado</option>
                                <option value="CANCELADO">Cancelado</option>
                            </select>
                        </td>
                        <td className="actions-cell">
                            <Link to={`/dashboard/admin/orders/${order.ordenId}`} className="view-btn">
                                Ver Detalles
                            </Link>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

// Componente principal del panel de administración
const AdminPanel = () => {
    const navigate = useNavigate();

    return (
        <div className="admin-panel">
            <div className="admin-sidebar">
                <h2>Administración</h2>
                <nav className="admin-nav">
                    <Link to="/dashboard/admin" className="nav-item">Dashboard</Link>
                    <Link to="/dashboard/admin/users" className="nav-item">Usuarios</Link>
                    <Link to="/dashboard/admin/products" className="nav-item">Productos</Link>
                    <Link to="/dashboard/admin/orders" className="nav-item">Órdenes</Link>
                </nav>
            </div>

            <div className="admin-content">
                <Routes>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/users" element={<UsersList />} />
                    <Route path="/products" element={<ProductsList />} />
                    <Route path="/orders" element={<OrdersList />} />
                </Routes>
            </div>
        </div>
    );
};

export default AdminPanel;
