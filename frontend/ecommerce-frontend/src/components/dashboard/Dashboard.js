import React, { useContext, useEffect } from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Header from '../common/Header';
import Footer from '../common/Footer';
import { AuthContext } from '../../context/AuthContext';
import ProductList from '../products/ProductList';
import ProductDetail from '../products/ProductDetail';
import InteractiveLoader from '../common/InteractiveLoader';
import Home from './Home';
import VendorProducts from '../vendor/VendorProducts';
import UserProfile from '../user/UserProfile'; // Importar el componente UserProfile
import Error500 from '../common/Error500';
import './Dashboard.css';

// Componentes temporales para las secciones que aún no están implementadas
const Cart = () => <div className="dashboard-section">Carrito de Compras (En desarrollo)</div>;
const Checkout = () => <div className="dashboard-section">Proceso de Pago (En desarrollo)</div>;
const AdminPanel = () => <div className="dashboard-section">Panel de Administración (En desarrollo)</div>;

const Dashboard = () => {
    const { userDetails } = useContext(AuthContext);
    const location = useLocation();

    // MOVER EL useEffect AQUÍ DENTRO DEL COMPONENTE
    useEffect(() => {
        const adjustHeights = () => {
            const windowHeight = window.innerHeight;
            const headerHeight = document.querySelector('.header')?.offsetHeight || 0;
            const footerHeight = document.querySelector('.footer')?.offsetHeight || 0;
            const availableHeight = windowHeight - headerHeight - footerHeight;

            // Ajustar altura mínima del contenido
            const dashboardContent = document.querySelector('.dashboard-content');
            if (dashboardContent) {
                dashboardContent.style.minHeight = `${availableHeight}px`;
            }

            // Ajustar altura de las tarjetas en el loader si existe
            const cards = document.querySelectorAll('.card');
            if (cards.length) {
                const cardHeight = Math.min(500, Math.max(300, availableHeight * 0.6));
                cards.forEach(card => {
                    card.style.height = `${cardHeight}px`;
                });
            }
        };

        // Ajustar al cargar y al redimensionar
        adjustHeights();
        window.addEventListener('resize', adjustHeights);

        return () => {
            window.removeEventListener('resize', adjustHeights);
        };
    }, []);

    // Verificar si estamos en la ruta principal del dashboard
    const isMainDashboard = location.pathname === '/dashboard' || location.pathname === '/dashboard/';

    return (
        <div className="dashboard">
            <Header />
            <main className="dashboard-content">
                <Routes>
                    <Route path="/" element={isMainDashboard ? <InteractiveLoader /> : <Home />} />
                    <Route path="/products" element={<Error500 />} />
                    <Route path="/products/:id" element={<ProductDetail />} />

                    {/* Rutas para compradores */}
                    {userDetails?.rol?.nombre === 'COMPRADOR' && (
                        <>
                            <Route path="/cart" element={<Cart />} />
                            <Route path="/checkout" element={<Checkout />} />
                        </>
                    )}

                    {/* Rutas para vendedores */}
                    {userDetails?.rol?.nombre === 'VENDEDOR' && (
                        <Route path="/mis-productos/*" element={<VendorProducts />} />
                    )}

                    {/* Rutas para administradores */}
                    {userDetails?.rol?.nombre === 'ADMINISTRADOR' && (
                        <Route path="/admin/*" element={<AdminPanel />} />
                    )}

                    <Route path="/profile" element={<UserProfile />} />

                    {/* Ruta por defecto */}
                    <Route path="*" element={<Navigate to="/dashboard" replace />} />
                </Routes>
            </main>
            <Footer />
        </div>
    );
};

export default Dashboard;
