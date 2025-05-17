import React from 'react';
import { Link } from 'react-router-dom';
import ContactButton from './ContactButton';
import GradientButton from './GradientButton';
import BlogButton from './BlogButton';
import './Footer.css';

const Footer = () => {
    const currentYear = new Date().getFullYear();

    return (
        <footer className="footer">
            <div className="footer-content">
                <p className="copyright">&copy; {currentYear} Mecommerces. Todos los derechos reservados.</p>
                <div className="footer-links">
                    <GradientButton to="/terms">Términos y Condiciones</GradientButton>
                    <BlogButton to="/blog" />
                    <ContactButton />
                </div>
            </div>
        </footer>
    );
};

export default Footer;
