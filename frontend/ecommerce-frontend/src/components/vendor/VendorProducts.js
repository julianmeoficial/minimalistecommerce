import React, { useState, useEffect, useContext } from 'react';
import { Routes, Route, Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';
import api from '../../services/api';
import './VendorProducts.css';

// Componente para listar los productos del vendedor
const ProductsList = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await api.get('/vendedor/productos');
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
                await api.delete(`/vendedor/productos/${productId}`);
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
        <div className="vendor-products-list">
            <div className="list-header">
                <h2>Mis Productos</h2>
                <Link to="/dashboard/mis-productos/new" className="add-product-btn">
                    Añadir Producto
                </Link>
            </div>

            {products.length === 0 ? (
                <div className="no-products">
                    <p>No tienes productos registrados.</p>
                    <Link to="/dashboard/mis-productos/new" className="add-product-btn">
                        Añadir tu primer producto
                    </Link>
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
                                    <div className="no-image">Sin imagen</div>
                                )}
                            </div>
                            <div className="product-info">
                                <h3>{product.productoNombre}</h3>
                                <p className="product-price">${product.precio.toFixed(2)}</p>
                                <p className="product-stock">
                                    Stock: {product.stock}
                                </p>
                                <div className="product-actions">
                                    <Link
                                        to={`/dashboard/mis-productos/edit/${product.productoId}`}
                                        className="edit-product-btn"
                                    >
                                        Editar
                                    </Link>
                                    <button
                                        onClick={() => handleDeleteProduct(product.productoId)}
                                        className="delete-product-btn"
                                    >
                                        Eliminar
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

// Componente para el formulario de producto (crear/editar)
const ProductForm = ({ isEditing = false }) => {
    const navigate = useNavigate();
    const { userDetails } = useContext(AuthContext);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);
    const [formData, setFormData] = useState({
        productoNombre: '',
        descripcion: '',
        precio: '',
        stock: '',
        categoriaId: '',
        imagenes: []
    });
    const [imageUrls, setImageUrls] = useState(['']);
    const [mainImageIndex, setMainImageIndex] = useState(0);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await api.get('/categorias');
                setCategories(response.data);
            } catch (err) {
                console.error("Error al cargar categorías:", err);
                setError("No se pudieron cargar las categorías. Intente de nuevo más tarde.");
            } finally {
                setLoading(false);
            }
        };

        fetchCategories();
    }, []);

    useEffect(() => {
        if (isEditing) {
            const productId = window.location.pathname.split('/').pop();
            const fetchProduct = async () => {
                try {
                    const response = await api.get(`/vendedor/productos/${productId}`);
                    const product = response.data;

                    setFormData({
                        productoNombre: product.productoNombre,
                        descripcion: product.descripcion || '',
                        precio: product.precio.toString(),
                        stock: product.stock.toString(),
                        categoriaId: product.categoria.categoriaId.toString(),
                    });

                    if (product.imagenes && product.imagenes.length > 0) {
                        const urls = product.imagenes.map(img => img.url);
                        setImageUrls(urls);

                        const mainIndex = product.imagenes.findIndex(img => img.esPrincipal);
                        if (mainIndex !== -1) {
                            setMainImageIndex(mainIndex);
                        }
                    }
                } catch (err) {
                    console.error("Error al cargar producto:", err);
                    setError("No se pudo cargar el producto para editar. Intente de nuevo más tarde.");
                }
            };

            fetchProduct();
        }
    }, [isEditing]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleImageUrlChange = (index, value) => {
        const newUrls = [...imageUrls];
        newUrls[index] = value;
        setImageUrls(newUrls);
    };

    const addImageField = () => {
        setImageUrls([...imageUrls, '']);
    };

    const removeImageField = (index) => {
        if (imageUrls.length > 1) {
            const newUrls = imageUrls.filter((_, i) => i !== index);
            setImageUrls(newUrls);

            // Ajustar el índice de la imagen principal si es necesario
            if (mainImageIndex === index) {
                setMainImageIndex(0);
            } else if (mainImageIndex > index) {
                setMainImageIndex(mainImageIndex - 1);
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        setError(null);

        try {
            // Preparar las imágenes
            const imagenes = imageUrls
                .filter(url => url.trim() !== '')
                .map((url, index) => ({
                    url,
                    descripcion: `Imagen ${index + 1} de ${formData.productoNombre}`,
                    esPrincipal: index === mainImageIndex
                }));

            const productData = {
                ...formData,
                precio: parseFloat(formData.precio),
                stock: parseInt(formData.stock),
                categoriaId: parseInt(formData.categoriaId),
                imagenes
            };

            if (isEditing) {
                const productId = window.location.pathname.split('/').pop();
                await api.put(`/vendedor/productos/${productId}`, productData);
            } else {
                await api.post('/vendedor/productos', productData);
            }

            navigate('/dashboard/mis-productos');
        } catch (err) {
            console.error("Error al guardar producto:", err);
            setError("No se pudo guardar el producto. Verifique los datos e intente de nuevo.");
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return <div className="loading">Cargando...</div>;
    }

    return (
        <div className="product-form-container">
            <h2>{isEditing ? 'Editar Producto' : 'Nuevo Producto'}</h2>

            <form onSubmit={handleSubmit} className="product-form">
                <div className="form-group">
                    <label htmlFor="productoNombre">Nombre del Producto *</label>
                    <input
                        type="text"
                        id="productoNombre"
                        name="productoNombre"
                        value={formData.productoNombre}
                        onChange={handleInputChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="descripcion">Descripción</label>
                    <textarea
                        id="descripcion"
                        name="descripcion"
                        value={formData.descripcion}
                        onChange={handleInputChange}
                        rows="4"
                    />
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="precio">Precio *</label>
                        <input
                            type="number"
                            id="precio"
                            name="precio"
                            value={formData.precio}
                            onChange={handleInputChange}
                            step="0.01"
                            min="0"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="stock">Stock *</label>
                        <input
                            type="number"
                            id="stock"
                            name="stock"
                            value={formData.stock}
                            onChange={handleInputChange}
                            min="0"
                            required
                        />
                    </div>
                </div>

                <div className="form-group">
                    <label htmlFor="categoriaId">Categoría *</label>
                    <select
                        id="categoriaId"
                        name="categoriaId"
                        value={formData.categoriaId}
                        onChange={handleInputChange}
                        required
                    >
                        <option value="">Seleccione una categoría</option>
                        {categories.map(category => (
                            <option key={category.categoriaId} value={category.categoriaId}>
                                {category.nombre}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="form-group">
                    <label>Imágenes</label>
                    {imageUrls.map((url, index) => (
                        <div key={index} className="image-input-row">
                            <input
                                type="text"
                                placeholder="URL de la imagen"
                                value={url}
                                onChange={(e) => handleImageUrlChange(index, e.target.value)}
                            />
                            <div className="image-actions">
                                <label>
                                    <input
                                        type="radio"
                                        name="mainImage"
                                        checked={index === mainImageIndex}
                                        onChange={() => setMainImageIndex(index)}
                                    />
                                    Principal
                                </label>
                                <button
                                    type="button"
                                    onClick={() => removeImageField(index)}
                                    disabled={imageUrls.length <= 1}
                                    className="remove-image-btn"
                                >
                                    Eliminar
                                </button>
                            </div>
                        </div>
                    ))}
                    <button
                        type="button"
                        onClick={addImageField}
                        className="add-image-btn"
                    >
                        Añadir Imagen
                    </button>
                </div>

                {error && <div className="error-message">{error}</div>}

                <div className="form-actions">
                    <button
                        type="button"
                        onClick={() => navigate('/dashboard/mis-productos')}
                        className="cancel-btn"
                    >
                        Cancelar
                    </button>
                    <button
                        type="submit"
                        className="save-btn"
                        disabled={saving}
                    >
                        {saving ? 'Guardando...' : 'Guardar Producto'}
                    </button>
                </div>
            </form>
        </div>
    );
};

// Componente principal que maneja las rutas
const VendorProducts = () => {
    return (
        <div className="vendor-products">
            <Routes>
                <Route path="/" element={<ProductsList />} />
                <Route path="/new" element={<ProductForm isEditing={false} />} />
                <Route path="/edit/:id" element={<ProductForm isEditing={true} />} />
            </Routes>
        </div>
    );
};

export default VendorProducts;
