import React, { useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import Footer from '../common/Footer';
import './Categories.css';

gsap.registerPlugin(ScrollTrigger);

const Categories = () => {
    const sectionRef = useRef(null);
    const titleRef = useRef(null);
    const categoryRefs = useRef([]);
    const searchRef = useRef(null);
    const logoRef = useRef(null);

    // Datos basados en backend MySQL - categorías reales de BD
    const categories = [
        {
            id: 1,
            name: 'Electrónica',
            slug: 'electronica',
            image: 'https://images.unsplash.com/photo-1518717758536-85ae29035b6d?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
            description: 'Productos electrónicos y tecnológicos'
        },
        {
            id: 2,
            name: 'Ropa',
            slug: 'ropa',
            image: 'https://images.unsplash.com/photo-1445205170230-053b83016050?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
            description: 'Ropa y accesorios'
        },
        {
            id: 3,
            name: 'Hogar',
            slug: 'hogar',
            image: 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
            description: 'Artículos para el hogar'
        },
        {
            id: 4,
            name: 'Laptops',
            slug: 'laptops',
            image: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
            description: 'Computadoras portátiles'
        },
        {
            id: 5,
            name: 'Smartphones',
            slug: 'smartphones',
            image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80',
            description: 'Teléfonos inteligentes'
        }
    ];

    useEffect(() => {
        const tl = gsap.timeline();

        // Animación del logo
        tl.fromTo(logoRef.current,
            {
                scale: 0,
                rotation: -180,
                opacity: 0
            },
            {
                scale: 1,
                rotation: 0,
                opacity: 1,
                duration: 1,
                ease: "back.out(1.7)"
            }
        );

        // Animación de la búsqueda
        tl.fromTo(searchRef.current,
            {
                x: 100,
                opacity: 0
            },
            {
                x: 0,
                opacity: 1,
                duration: 0.8,
                ease: "power3.out"
            },
            "-=0.5"
        );

        // Animación del título
        tl.fromTo(titleRef.current,
            {
                y: 100,
                opacity: 0,
                scale: 0.8
            },
            {
                y: 0,
                opacity: 1,
                scale: 1,
                duration: 1,
                ease: "power3.out"
            },
            "-=0.3"
        );

        // Animación de las categorías con stagger
        tl.fromTo(categoryRefs.current,
            {
                y: 150,
                opacity: 0,
                scale: 0.7,
                rotationY: 45
            },
            {
                y: 0,
                opacity: 1,
                scale: 1,
                rotationY: 0,
                duration: 1.2,
                stagger: 0.2,
                ease: "back.out(1.3)"
            },
            "-=0.5"
        );

        // Efectos de hover para cada categoría
        categoryRefs.current.forEach((category, index) => {
            if (category) {
                category.addEventListener('mouseenter', () => {
                    gsap.to(category, {
                        y: -15,
                        scale: 1.05,
                        duration: 0.4,
                        ease: "power2.out"
                    });

                    const image = category.querySelector('.category-image');
                    if (image) {
                        gsap.to(image, {
                            scale: 1.1,
                            duration: 0.4,
                            ease: "power2.out"
                        });
                    }
                });

                category.addEventListener('mouseleave', () => {
                    gsap.to(category, {
                        y: 0,
                        scale: 1,
                        duration: 0.4,
                        ease: "power2.out"
                    });

                    const image = category.querySelector('.category-image');
                    if (image) {
                        gsap.to(image, {
                            scale: 1,
                            duration: 0.4,
                            ease: "power2.out"
                        });
                    }
                });
            }
        });

        return () => {
            ScrollTrigger.getAll().forEach(trigger => trigger.kill());
        };
    }, []);

    return (
        <div className="categories-page" ref={sectionRef}>
            {/* Header */}
            <header className="categories-header">
                <div className="header-container">
                    <div className="logo-section" ref={logoRef}>
                        <Link to="/" className="logo-link">
                            <div className="logo-icon">
                                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                                    <rect width="16" height="16" rx="3" fill="#B68865"/>
                                </svg>
                            </div>
                            <span className="logo-text">Mecommerces</span>
                        </Link>
                    </div>

                    <div className="search-section" ref={searchRef}>
                        <div className="search-container">
                            <div className="search-input-wrapper">
                                <div className="search-icon">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                                        <circle cx="11" cy="11" r="8" stroke="currentColor" strokeWidth="2"/>
                                        <path d="m21 21-4.35-4.35" stroke="currentColor" strokeWidth="2"/>
                                    </svg>
                                </div>
                                <input
                                    type="text"
                                    placeholder="Buscar productos..."
                                    className="search-input"
                                />
                            </div>
                            <div className="action-buttons">
                                <Link to="/login" className="action-btn">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" strokeWidth="2"/>
                                        <circle cx="12" cy="7" r="4" stroke="currentColor" strokeWidth="2"/>
                                    </svg>
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </header>

            {/* Main Content */}
            <main className="categories-main">
                <div className="container">
                    <div className="categories-content">
                        <div className="title-section">
                            <h1 ref={titleRef} className="main-title">
                                Explora nuestras categorías
                            </h1>
                            <p className="subtitle">
                                Encuentra productos organizados según tu backend MySQL
                            </p>
                        </div>

                        <div className="categories-grid">
                            {categories.map((category, index) => (
                                <Link
                                    key={category.id}
                                    to={`/productos?categoria=${category.slug}`}
                                    className="category-card"
                                    ref={el => categoryRefs.current[index] = el}
                                >
                                    <div className="category-image">
                                        <img src={category.image} alt={category.name} />
                                        <div className="category-overlay">
                                            <span className="category-name">{category.name}</span>
                                        </div>
                                    </div>
                                </Link>
                            ))}
                        </div>

                        <div className="back-to-home">
                            <Link to="/" className="back-button">
                                ← Volver al Inicio
                            </Link>
                        </div>
                    </div>
                </div>
            </main>

            <Footer />
        </div>
    );
};

export default Categories;
