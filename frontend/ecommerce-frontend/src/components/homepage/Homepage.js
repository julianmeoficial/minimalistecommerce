import React, { useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import './Homepage.css';

gsap.registerPlugin(ScrollTrigger);

const Homepage = () => {
    const headerRef = useRef(null);
    const logoRef = useRef(null);
    const navItemsRef = useRef([]);
    const heroRef = useRef(null);
    const heroTitleRef = useRef(null);
    const heroSubtitleRef = useRef(null);
    const heroButtonRef = useRef(null);
    const featuredRef = useRef(null);
    const categoriesRef = useRef(null);
    const footerRef = useRef(null);

    useEffect(() => {
        const tl = gsap.timeline();

        // Animación del header
        tl.fromTo(headerRef.current,
            { y: -100, opacity: 0 },
            { y: 0, opacity: 1, duration: 1, ease: "power3.out" }
        )
            // Logo bounce
            .fromTo(logoRef.current,
                { scale: 0, rotation: -180 },
                { scale: 1, rotation: 0, duration: 0.8, ease: "back.out(1.7)" },
                "-=0.7"
            )
            // Nav items stagger
            .fromTo(navItemsRef.current,
                { y: -30, opacity: 0 },
                { y: 0, opacity: 1, duration: 0.6, stagger: 0.1, ease: "power2.out" },
                "-=0.5"
            );

        // Hero animations
        const heroTl = gsap.timeline({ delay: 0.5 });
        heroTl.fromTo(heroRef.current,
            { scale: 0.9, opacity: 0 },
            { scale: 1, opacity: 1, duration: 1.2, ease: "power3.out" }
        )
            .fromTo(heroTitleRef.current,
                { y: 100, opacity: 0 },
                { y: 0, opacity: 1, duration: 1, ease: "power3.out" },
                "-=0.8"
            )
            .fromTo(heroSubtitleRef.current,
                { y: 50, opacity: 0 },
                { y: 0, opacity: 1, duration: 0.8, ease: "power2.out" },
                "-=0.6"
            )
            .fromTo(heroButtonRef.current,
                { scale: 0, opacity: 0 },
                { scale: 1, opacity: 1, duration: 0.6, ease: "back.out(1.7)" },
                "-=0.4"
            );

        // Scroll animations
        gsap.fromTo(featuredRef.current,
            { y: 100, opacity: 0 },
            {
                y: 0,
                opacity: 1,
                duration: 1,
                scrollTrigger: {
                    trigger: featuredRef.current,
                    start: "top 80%",
                    end: "bottom 20%",
                    toggleActions: "play none none reverse"
                }
            }
        );

        gsap.fromTo(categoriesRef.current,
            { y: 100, opacity: 0 },
            {
                y: 0,
                opacity: 1,
                duration: 1,
                scrollTrigger: {
                    trigger: categoriesRef.current,
                    start: "top 80%",
                    end: "bottom 20%",
                    toggleActions: "play none none reverse"
                }
            }
        );

        // Product cards animation
        const productCards = document.querySelectorAll('.product-card');
        productCards.forEach((card, index) => {
            gsap.fromTo(card,
                { y: 80, opacity: 0, scale: 0.8, rotationY: 15 },
                {
                    y: 0,
                    opacity: 1,
                    scale: 1,
                    rotationY: 0,
                    duration: 0.8,
                    delay: index * 0.15,
                    ease: "power3.out",
                    scrollTrigger: {
                        trigger: card,
                        start: "top 85%",
                        toggleActions: "play none none reverse"
                    }
                }
            );
        });

        // Category cards animation
        const categoryCards = document.querySelectorAll('.category-card');
        categoryCards.forEach((card, index) => {
            gsap.fromTo(card,
                { y: 60, opacity: 0, scale: 0.9 },
                {
                    y: 0,
                    opacity: 1,
                    scale: 1,
                    duration: 0.7,
                    delay: index * 0.1,
                    ease: "power2.out",
                    scrollTrigger: {
                        trigger: card,
                        start: "top 85%",
                        toggleActions: "play none none reverse"
                    }
                }
            );
        });

        // Footer animation
        gsap.fromTo(footerRef.current,
            { y: 50, opacity: 0 },
            {
                y: 0,
                opacity: 1,
                duration: 1,
                scrollTrigger: {
                    trigger: footerRef.current,
                    start: "top 90%",
                    toggleActions: "play none none reverse"
                }
            }
        );

        return () => {
            ScrollTrigger.getAll().forEach(trigger => trigger.kill());
        };
    }, []);

    return (
        <div className="homepage">
            {/* Header */}
            <header className="header" ref={headerRef}>
                <div className="header-container">
                    <div className="logo" ref={logoRef}>
                        <Link to="/">
                            <div className="logo-icon">
                                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                                    <rect width="16" height="16" rx="3" fill="#B68865"/>
                                </svg>
                            </div>
                            <span className="logo-text">Meecommerce</span>
                        </Link>
                    </div>

                    <nav className="navigation">
                        <div ref={el => navItemsRef.current[0] = el}>
                            <Link to="/">Inicio</Link>
                        </div>
                        <div ref={el => navItemsRef.current[1] = el}>
                            <Link to="/categorias">Categorías</Link>
                        </div>
                        <div ref={el => navItemsRef.current[2] = el}>
                            <Link to="/tendencias">Tendencias</Link>
                        </div>
                        <div ref={el => navItemsRef.current[3] = el}>
                            <Link to="/nuevos">Nuevos</Link>
                        </div>
                    </nav>

                    <div className="header-actions">
                        <Link to="/login" className="login-btn">
                            Iniciar Sesión / Registrarse
                        </Link>
                        <button className="action-btn cart-btn">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                                <path d="M3 3H5L5.4 5M7 13H17L21 5H5.4M7 13L5.4 5M7 13L4.7 15.3C4.3 15.7 4.6 16.5 5.1 16.5H17M17 13V17C17 18.1 17.9 19 19 19S21 18.1 21 17V13M9 19.5C9.8 19.5 10.5 20.2 10.5 21S9.8 22.5 9 22.5 7.5 21.8 7.5 21 8.2 19.5 9 19.5ZM20 19.5C20.8 19.5 21.5 20.2 21.5 21S20.8 22.5 20 22.5 18.5 21.8 18.5 21 19.2 19.5 20 19.5Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            </svg>
                        </button>
                        <button className="action-btn wishlist-btn">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                                <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69364 19.0554 3.41708C18.3879 3.14052 17.6725 2.99817 16.95 2.99817C16.2275 2.99817 15.5121 3.14052 14.8446 3.41708C14.1772 3.69364 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.5783 8.50903 2.9987 7.05 2.9987C5.59096 2.9987 4.19169 3.5783 3.16 4.61C2.1283 5.6417 1.5487 7.04097 1.5487 8.5C1.5487 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6053C22.3095 9.93789 22.4518 9.22248 22.4518 8.5C22.4518 7.77752 22.3095 7.06211 22.0329 6.39467C21.7563 5.72723 21.351 5.1208 20.84 4.61V4.61Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            </svg>
                        </button>
                    </div>
                </div>
            </header>

            {/* Hero Banner */}
            <section className="hero-banner" ref={heroRef}>
                <div className="hero-content">
                    <div className="hero-image">
                        <img src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80" alt="Paisaje montañoso" />
                        <div className="hero-overlay">
                            <div className="hero-text">
                                <h1 ref={heroTitleRef}>Descubre Productos Únicos</h1>
                                <p ref={heroSubtitleRef}>Encuentra productos innovadores de creadores y emprendedores independientes.</p>
                                <Link to="/productos" className="cta-button" ref={heroButtonRef}>
                                    Explorar Productos
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Productos Destacados */}
            <section className="featured-products" ref={featuredRef}>
                <div className="container">
                    <h2>Productos Destacados</h2>
                    <div className="products-grid">
                        {[
                            {
                                image: "https://images.unsplash.com/photo-1583394838336-acd977736f90?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
                                title: "Accesorio para dispositivos",
                                description: "Mejora tu experiencia con dispositivos con este accesorio esencial."
                            },
                            {
                                image: "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
                                title: "Cuaderno minimalista",
                                description: "Organiza tus ideas con este cuaderno elegante y funcional."
                            },
                            {
                                image: "https://images.unsplash.com/photo-1523362628745-0c100150b504?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
                                title: "Botella de agua",
                                description: "Mantente hidratado con estilo con esta botella de agua reutilizable."
                            },
                            {
                                image: "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80",
                                title: "Pisapapeles",
                                description: "Añade un toque de sofisticación a tu escritorio con este pisapapeles."
                            }
                        ].map((product, index) => (
                            <div key={index} className="product-card">
                                <div className="product-image">
                                    <img src={product.image} alt={product.title} />
                                </div>
                                <div className="product-info">
                                    <h3>{product.title}</h3>
                                    <p>{product.description}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* Comprar por Categoría */}
            <section className="categories-section" ref={categoriesRef}>
                <div className="container">
                    <h2>Comprar por Categoría</h2>
                    <div className="categories-grid">
                        {[
                            {
                                image: "https://images.unsplash.com/photo-1441986300917-64674bd600d8?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80",
                                title: "Accesorios de Tecnología",
                                link: "/categorias"
                            },
                            {
                                image: "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80",
                                title: "Accesorios para el Hogar",
                                link: "/categorias"
                            },
                            {
                                image: "https://images.unsplash.com/photo-1445205170230-053b83016050?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80",
                                title: "Accesorios de Moda",
                                link: "/categorias"
                            },
                            {
                                image: "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80",
                                title: "Accesorios para Mascotas",
                                link: "/categorias"
                            },
                            {
                                image: "https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80",
                                title: "Arte y Manualidades",
                                link: "/categorias"
                            }
                        ].map((category, index) => (
                            <Link key={index} to={category.link} className="category-card">
                                <div className="category-image">
                                    <img src={category.image} alt={category.title} />
                                </div>
                                <h3>{category.title}</h3>
                            </Link>
                        ))}
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer className="footer" ref={footerRef}>
                <div className="container">
                    <div className="footer-content">
                        <div className="footer-section">
                            <h4>Acerca de Nosotros</h4>
                            <p>Descubre productos únicos de emprendedores independientes.</p>
                        </div>
                        <div className="footer-section">
                            <h4>Contacto</h4>
                            <p>Email: info@meecommerce.com</p>
                            <p>Teléfono: +1 234 567 8900</p>
                        </div>
                        <div className="footer-section">
                            <h4>Política de Privacidad</h4>
                            <Link to="/privacy">Ver política</Link>
                        </div>
                        <div className="footer-section">
                            <h4>Términos de Servicio</h4>
                            <Link to="/terms">Ver términos</Link>
                        </div>
                    </div>
                    <div className="footer-social">
                        <div className="social-icons">
                            <a href="#" aria-label="Twitter" className="social-icon">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z"/>
                                </svg>
                            </a>
                            <a href="#" aria-label="Instagram" className="social-icon">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/>
                                </svg>
                            </a>
                            <a href="#" aria-label="Facebook" className="social-icon">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                                </svg>
                            </a>
                        </div>
                    </div>
                    <div className="footer-bottom">
                        <p>&copy; 2025 Meecommerce. Todos los derechos reservados.</p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Homepage;
