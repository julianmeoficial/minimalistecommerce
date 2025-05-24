import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';

// Componentes principales existentes
import Homepage from './components/homepage/Homepage';
import Login from './components/auth/Login';
import Dashboard from './components/dashboard/Dashboard';
import PrivateRoute from './components/common/PrivateRoute';

// Componentes públicos existentes
import Error500 from './components/common/Error500';
import Contact from './components/contact/Contact';
import Terms from './components/terms/Terms';
import NotFound404 from './components/blog/NotFound404';
import Categories from './components/categories/Categories';

import './App.css';

// Componentes temporales
const TemporaryProductCatalog = () => (
    <div className="temp-page">
        <h1>Catálogo Público de Productos</h1>
        <p>Conectado con el backend Spring Boot 3.0</p>
        <p>Endpoint: <code>/api/productos</code></p>
        <p>Base de datos: MySQL - tabla <code>producto</code></p>
        <div className="features">
            <h3>Funcionalidades programadas:</h3>
            <ul>
                <li>✅ Filtros por categoría</li>
                <li>✅ Búsqueda por nombre</li>
                <li>✅ Productos destacados</li>
                <li>✅ Paginación</li>
                <li>✅ Sistema de slugs SEO</li>
            </ul>
        </div>
    </div>
);

const TemporaryTrendingProducts = () => (
    <div className="temp-page">
        <h1>Productos en Tendencia</h1>
        <p>Productos destacados y populares</p>
        <p>Filtros: <code>destacado=true&activo=true</code></p>
    </div>
);

const TemporaryNewProducts = () => (
    <div className="temp-page">
        <h1>Productos Nuevos</h1>
        <p>Últimos productos agregados al catálogo</p>
        <p>Ordenado por: <code>created_at DESC</code></p>
    </div>
);

const TemporaryRegister = () => (
    <div className="temp-page">
        <h1>Registro de Usuario</h1>
        <p>Formulario de registro conectado con:</p>
        <p>Endpoint: <code>/api/auth/registro</code></p>
        <div className="roles">
            <h3>Roles disponibles:</h3>
            <ul>
                <li>COMPRADOR (ID: 2)</li>
                <li>VENDEDOR (ID: 3)</li>
                <li>ADMINISTRADOR (ID: 1) - Solo admin</li>
            </ul>
        </div>
    </div>
);

const TemporaryHealthCheck = () => (
    <div className="temp-page">
        <h1>Estado del Sistema</h1>
        <p>Monitoreo de salud del backend</p>
        <p>Endpoint: <code>/api/health</code></p>
        <div className="status">
            <p>🟢 Spring Boot 3.0 - Activo</p>
            <p>🟢 MySQL Database - Conectada</p>
            <p>🟢 JWT Authentication - Funcional</p>
        </div>
    </div>
);

function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    {/* Página principal pública */}
                    <Route path="/" element={<Homepage />} />

                    {/* Sistema de autenticación existente */}
                    <Route path="/login" element={<Login />} />

                    {/* Dashboard protegido */}
                    <Route
                        path="/dashboard/*"
                        element={
                            <PrivateRoute>
                                <Dashboard />
                            </PrivateRoute>
                        }
                    />

                    {/* CATEGORÍAS - LA RUTA PRINCIPAL */}
                    <Route path="/categorias" element={<Categories />} />

                    {/* Catálogo público de productos */}
                    <Route path="/productos" element={<TemporaryProductCatalog />} />
                    <Route path="/productos/:slug" element={<div>Detalle del Producto (En desarrollo)</div>} />
                    <Route path="/productos/categoria/:categoriaSlug" element={<TemporaryProductCatalog />} />

                    <Route path="/categorias/:slug" element={<TemporaryProductCatalog />} />

                    {/* Productos especiales */}
                    <Route path="/tendencias" element={<TemporaryTrendingProducts />} />
                    <Route path="/nuevos" element={<TemporaryNewProducts />} />
                    <Route path="/destacados" element={<TemporaryProductCatalog />} />

                    {/* Rutas de autenticación */}
                    <Route path="/register" element={<TemporaryRegister />} />
                    <Route path="/forgot-password" element={<div>Recuperar Contraseña (En desarrollo)</div>} />
                    <Route path="/reset-password" element={<div>Restablecer Contraseña (En desarrollo)</div>} />

                    {/* Rutas existentes */}
                    <Route path="/contact" element={<Contact />} />
                    <Route path="/terms" element={<Terms />} />
                    <Route path="/blog" element={<NotFound404 />} />

                    {/* Rutas del sistema */}
                    <Route path="/health" element={<TemporaryHealthCheck />} />
                    <Route path="/api-docs" element={<div>Documentación de API (Swagger disponible en backend)</div>} />

                    {/* Búsqueda */}
                    <Route path="/buscar" element={<TemporaryProductCatalog />} />
                    <Route path="/buscar/:query" element={<TemporaryProductCatalog />} />

                    {/* Páginas informativas */}
                    <Route path="/privacy" element={<div>Política de Privacidad</div>} />
                    <Route path="/about" element={<div>Acerca de Nosotros</div>} />

                    {/* Rutas para vendedores públicos */}
                    <Route path="/vendedor/:vendedorSlug" element={<TemporaryProductCatalog />} />
                    <Route path="/tienda/:vendedorSlug" element={<div>Tienda del Vendedor</div>} />

                    {/* Páginas de error */}
                    <Route path="/error500" element={<Error500 />} />

                    {/* Rutas de desarrollo */}
                    {process.env.NODE_ENV === 'development' && (
                        <>
                            <Route path="/test/components" element={<div>Página de prueba de componentes</div>} />
                            <Route path="/test/api" element={<div>Página de prueba de API</div>} />
                        </>
                    )}

                    {/* Ruta por defecto */}
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App;
