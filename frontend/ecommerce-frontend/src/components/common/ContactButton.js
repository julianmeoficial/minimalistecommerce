import React, { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import './ContactButton.css';
import gsap from 'gsap';

const ContactButton = () => {
    const buttonRef = useRef(null);
    const navigate = useNavigate();

    useEffect(() => {
        const button = buttonRef.current;
        if (!button) return;

        const handleClick = () => {
            const tl = gsap.timeline({
                onComplete: () => {
                    // Redirigir a la página de contacto después de la animación
                    navigate('/contact');
                }
            });

            tl.to('.icon svg', 0.4, {
                x: -8,
                y: 8,
                transition: 'none'
            })
                .to('.icon svg', 0.4, {
                    x: '50vw',
                    y: '-50vh',
                })
                .set('.icon svg', {
                    x: '-50vw',
                    y: '50vh'
                })
                .to('.icon svg', 0.3, {
                    x: 0,
                    y: 0
                });
        };

        button.addEventListener('click', handleClick);

        return () => {
            button.removeEventListener('click', handleClick);
        };
    }, [navigate]);

    return (
        <button className="send" ref={buttonRef}>
            <span className="text">Contacto</span>
            <span className="icon">
        <svg viewBox="0 0 512.005 512.005">
          <path d="M511.658 51.675c2.496-11.619-8.895-21.416-20.007-17.176l-482 184a15 15 0 00-.054 28.006L145 298.8v164.713a15 15 0 0028.396 6.75l56.001-111.128 136.664 101.423c8.313 6.17 20.262 2.246 23.287-7.669C516.947 34.532 511.431 52.726 511.658 51.675zm-118.981 52.718L157.874 271.612 56.846 232.594zM175 296.245l204.668-145.757c-176.114 185.79-166.916 176.011-167.684 177.045-1.141 1.535 1.985-4.448-36.984 72.882zm191.858 127.546l-120.296-89.276 217.511-229.462z" />
        </svg>
      </span>
        </button>
    );
};

export default ContactButton;
