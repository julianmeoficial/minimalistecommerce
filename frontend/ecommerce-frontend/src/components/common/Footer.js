import React, { useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import ContactButton from './ContactButton';
import GradientButton from './GradientButton';
import BlogButton from './BlogButton';
import './Footer.css';

gsap.registerPlugin(ScrollTrigger);

const Footer = () => {
    const footerRef = useRef(null);
    const logoRef = useRef(null);
    const sectionsRef = useRef([]);
    const socialIconsRef = useRef([]);
    const dividerRef = useRef(null);
    const copyrightRef = useRef(null);
    const buttonsRef = useRef([]);

    useEffect(() => {
        const footer = footerRef.current;
        if (!footer) return;

        // Animación principal del footer al hacer scroll
        const footerTl = gsap.timeline({
            scrollTrigger: {
                trigger: footer,
                start: "top 90%",
                end: "bottom 20%",
                toggleActions: "play none none reverse"
            }
        });

        // Animación del logo con bounce
        footerTl.fromTo(logoRef.current,
            {
                y: 50,
                opacity: 0,
                scale: 0.8,
                rotation: -10
            },
            {
                y: 0,
                opacity: 1,
                scale: 1,
                rotation: 0,
                duration: 1.2,
                ease: "back.out(1.7)"
            }
        );

        // Animación de las secciones con stagger
        footerTl.fromTo(sectionsRef.current,
            {
                y: 60,
                opacity: 0,
                scale: 0.9
            },
            {
                y: 0,
                opacity: 1,
                scale: 1,
                duration: 0.8,
                stagger: 0.2,
                ease: "power3.out"
            },
            "-=0.8"
        );

        // Animación de los botones personalizados
        footerTl.fromTo(buttonsRef.current,
            {
                scale: 0,
                opacity: 0,
                rotation: 180
            },
            {
                scale: 1,
                opacity: 1,
                rotation: 0,
                duration: 0.6,
                stagger: 0.15,
                ease: "back.out(1.7)"
            },
            "-=0.6"
        );

        // Animación de los iconos sociales
        footerTl.fromTo(socialIconsRef.current,
            {
                y: 30,
                opacity: 0,
                scale: 0.5
            },
            {
                y: 0,
                opacity: 1,
                scale: 1,
                duration: 0.6,
                stagger: 0.1,
                ease: "elastic.out(1, 0.75)"
            },
            "-=0.4"
        );

        // Animación del divisor
        footerTl.fromTo(dividerRef.current,
            {
                scaleX: 0,
                opacity: 0
            },
            {
                scaleX: 1,
                opacity: 1,
                duration: 1,
                ease: "power2.out"
            },
            "-=0.3"
        );

        // Animación del copyright
        footerTl.fromTo(copyrightRef.current,
            {
                y: 20,
                opacity: 0
            },
            {
                y: 0,
                opacity: 1,
                duration: 0.8,
                ease: "power2.out"
            },
            "-=0.2"
        );

        // Animaciones de hover para elementos interactivos
        const hoverElements = footer.querySelectorAll('.hover-element');
        hoverElements.forEach(element => {
            element.addEventListener('mouseenter', () => {
                gsap.to(element, {
                    scale: 1.05,
                    y: -3,
                    duration: 0.3,
                    ease: "power2.out"
                });
            });

            element.addEventListener('mouseleave', () => {
                gsap.to(element, {
                    scale: 1,
                    y: 0,
                    duration: 0.3,
                    ease: "power2.out"
                });
            });
        });

        // Animación continua del logo
        gsap.to(logoRef.current, {
            rotation: 360,
            duration: 20,
            repeat: -1,
            ease: "none"
        });

        return () => {
            ScrollTrigger.getAll().forEach(trigger => trigger.kill());
        };
    }, []);

    const currentYear = new Date().getFullYear();

    return (
        <footer className="footer" ref={footerRef}>
            <div className="footer-container">
                {/* Logo animado */}
                <div className="footer-logo" ref={logoRef}>
                    <Link to="/" className="logo-link hover-element">
                        <div className="logo-icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                                <rect width="24" height="24" rx="4" fill="#B68865"/>
                                <path d="M12 8L16 12L12 16L8 12L12 8Z" fill="white"/>
                            </svg>
                        </div>
                        <span className="logo-text">Mecommerces</span>
                    </Link>
                </div>

                {/* Contenido principal del footer */}
                <div className="footer-content">
                    {/* Sección Acerca de */}
                    <div className="footer-section" ref={el => sectionsRef.current[0] = el}>
                        <h4 className="section-title">Acerca de Nosotros</h4>
                        <p className="section-description">
                            Sistema de e-commerce modular diseñado especialmente para emprendedores.
                            Simplicidad, escalabilidad y personalización en una sola plataforma.
                        </p>
                        <div className="features-list">
                            <span className="feature-item hover-element">✨ Diseño Modular</span>
                            <span className="feature-item hover-element">🚀 Escalable</span>
                            <span className="feature-item hover-element">🎨 Personalizable</span>
                        </div>
                    </div>

                    {/* Enlaces rápidos */}
                    <div className="footer-section" ref={el => sectionsRef.current[1] = el}>
                        <h4 className="section-title">Enlaces Rápidos</h4>
                        <nav className="footer-nav">
                            <Link to="/" className="footer-link hover-element">Inicio</Link>
                            <Link to="/categorias" className="footer-link hover-element">Categorías</Link>
                            <Link to="/productos" className="footer-link hover-element">Productos</Link>
                            <Link to="/tendencias" className="footer-link hover-element">Tendencias</Link>
                            <Link to="/nuevos" className="footer-link hover-element">Nuevos</Link>
                            <Link to="/login" className="footer-link hover-element">Iniciar Sesión</Link>
                        </nav>
                    </div>

                    {/* Información de contacto */}
                    <div className="footer-section" ref={el => sectionsRef.current[2] = el}>
                        <h4 className="section-title">Contacto</h4>
                        <div className="contact-info">
                            <div className="contact-item hover-element">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                                    <path d="M3 8L10.89 13.26C11.2187 13.4793 11.6049 13.5963 12 13.5963C12.3951 13.5963 12.7813 13.4793 13.11 13.26L21 8M5 19H19C20.1046 19 21 18.1046 21 17V7C21 5.89543 20.1046 5 19 5H5C3.89543 5 3 5.89543 3 7V17C3 18.1046 3.89543 19 5 19Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                </svg>
                                <span>info@mecommerces.com</span>
                            </div>
                            <div className="contact-item hover-element">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                                    <path d="M22 16.92V19.92C22.0011 20.1985 21.9441 20.4742 21.8325 20.7293C21.7209 20.9845 21.5573 21.2136 21.3521 21.4019C21.1468 21.5901 20.9046 21.7335 20.6407 21.8227C20.3769 21.9119 20.0974 21.9451 19.82 21.92C16.7428 21.5856 13.787 20.5341 11.19 18.85C8.77382 17.3147 6.72533 15.2662 5.19 12.85C3.49998 10.2412 2.44824 7.27099 2.12 4.18C2.09501 3.90347 2.12788 3.62476 2.21649 3.36162C2.3051 3.09849 2.44748 2.85669 2.63468 2.65162C2.82188 2.44655 3.04974 2.28271 3.30372 2.17052C3.55771 2.05833 3.83227 2.00026 4.11 2H7.11C7.59531 1.99522 8.06711 2.16708 8.43849 2.48353C8.80988 2.79999 9.05423 3.23945 9.13 3.72C9.27154 4.68007 9.52478 5.62273 9.88 6.53C10.0202 6.88792 10.0618 7.27691 10.0012 7.65382C9.94066 8.03073 9.78145 8.38687 9.54 8.68L8.12 10.1C9.57896 12.6135 11.3865 14.421 13.9 15.88L15.32 14.46C15.6131 14.2185 15.9693 14.0593 16.3462 13.9988C16.7231 13.9382 17.1121 13.9798 17.47 14.12C18.3773 14.4752 19.3199 14.7285 20.28 14.87C20.7658 14.9458 21.2094 15.1947 21.5265 15.5709C21.8437 15.9471 22.0122 16.4246 22 16.92Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                </svg>
                                <span>+1 (555) 123-4567</span>
                            </div>
                            <div className="contact-item hover-element">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                                    <path d="M21 10C21 17 12 23 12 23S3 17 3 10C3 7.61305 3.94821 5.32387 5.63604 3.63604C7.32387 1.94821 9.61305 1 12 1C14.3869 1 16.6761 1.94821 18.364 3.63604C20.0518 5.32387 21 7.61305 21 10Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                    <path d="M12 13C13.6569 13 15 11.6569 15 10C15 8.34315 13.6569 7 12 7C10.3431 7 9 8.34315 9 10C9 11.6569 10.3431 13 12 13Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                </svg>
                                <span>Ciudad Innovación, País Digital</span>
                            </div>
                        </div>
                    </div>

                    {/* Para desarrolladores */}
                    <div className="footer-section" ref={el => sectionsRef.current[3] = el}>
                        <h4 className="section-title">Para Desarrolladores</h4>
                        <div className="tech-stack">
              <span className="tech-item hover-element">
                <span className="tech-icon">⚛️</span>
                React.js
              </span>
                            <span className="tech-item hover-element">
                <span className="tech-icon">🍃</span>
                Spring Boot
              </span>
                            <span className="tech-item hover-element">
                <span className="tech-icon">🗄️</span>
                MySQL
              </span>
                            <span className="tech-item hover-element">
                <span className="tech-icon">🔐</span>
                JWT Auth
              </span>
                        </div>
                        <div className="api-info">
                            <p className="api-text">API REST disponible para integraciones</p>
                            <Link to="/api-docs" className="api-link hover-element">Ver Documentación</Link>
                        </div>
                    </div>
                </div>

                {/* Botones de acción */}
                <div className="footer-actions">
                    <div className="action-buttons">
                        <div ref={el => buttonsRef.current[0] = el}>
                            <GradientButton to="/terms">Términos y Condiciones</GradientButton>
                        </div>
                        <div ref={el => buttonsRef.current[1] = el}>
                            <BlogButton to="/blog" />
                        </div>
                        <div ref={el => buttonsRef.current[2] = el}>
                            <ContactButton />
                        </div>
                    </div>
                </div>

                {/* Redes sociales */}
                <div className="footer-social">
                    <h4 className="social-title">Síguenos</h4>
                    <div className="social-icons">
                        {[
                            {
                                name: 'GitHub',
                                icon: 'M12 0C5.37 0 0 5.37 0 12C0 17.31 3.435 21.795 8.205 23.385C8.805 23.49 9.03 23.13 9.03 22.815C9.03 22.53 9.015 21.585 9.015 20.58C6 21.135 5.22 19.845 4.98 19.17C4.845 18.825 4.26 17.76 3.75 17.475C3.33 17.25 2.73 16.695 3.735 16.68C4.68 16.665 5.355 17.55 5.58 17.91C6.66 19.725 8.385 19.215 9.075 18.9C9.18 18.12 9.495 17.595 9.84 17.295C7.17 16.995 4.38 15.96 4.38 11.37C4.38 10.065 4.845 8.985 5.61 8.145C5.49 7.845 5.07 6.615 5.73 4.965C5.73 4.965 6.735 4.65 9.03 6.195C9.99 5.925 11.01 5.79 12.03 5.79C13.05 5.79 14.07 5.925 15.03 6.195C17.325 4.635 18.33 4.965 18.33 4.965C18.99 6.615 18.57 7.845 18.45 8.145C19.215 8.985 19.68 10.05 19.68 11.37C19.68 15.975 16.875 16.995 14.205 17.295C14.64 17.67 15.015 18.39 15.015 19.515C15.015 21.12 15 22.41 15 22.815C15 23.13 15.225 23.505 15.825 23.385C20.565 21.795 24 17.295 24 12C24 5.37 18.63 0 12 0Z',
                                url: 'https://github.com'
                            },
                            {
                                name: 'Twitter',
                                icon: 'M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z',
                                url: 'https://twitter.com'
                            },
                            {
                                name: 'LinkedIn',
                                icon: 'M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z',
                                url: 'https://linkedin.com'
                            },
                            {
                                name: 'Instagram',
                                icon: 'M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z',
                                url: 'https://instagram.com'
                            }
                        ].map((social, index) => (
                            <a
                                key={social.name}
                                href={social.url}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="social-icon hover-element"
                                ref={el => socialIconsRef.current[index] = el}
                                aria-label={social.name}
                            >
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d={social.icon} />
                                </svg>
                            </a>
                        ))}
                    </div>
                </div>

                {/* Divisor animado */}
                <div className="footer-divider" ref={dividerRef}></div>

                {/* Copyright */}
                <div className="footer-bottom" ref={copyrightRef}>
                    <p className="copyright-text">
                        &copy; {currentYear} Mecommerces. Todos los derechos reservados.
                    </p>
                    <p className="developed-text">
                        Desarrollado con ❤️ para emprendedores
                    </p>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
