import React from 'react';
import './Contact.css';

const Contact = () => {
    return (
        <div className="contact-container">
            <div className="contact-icon">
                <svg viewBox="0 0 512.005 512.005">
                    <path d="M511.658 51.675c2.496-11.619-8.895-21.416-20.007-17.176l-482 184a15 15 0 00-.054 28.006L145 298.8v164.713a15 15 0 0028.396 6.75l56.001-111.128 136.664 101.423c8.313 6.17 20.262 2.246 23.287-7.669C516.947 34.532 511.431 52.726 511.658 51.675zm-118.981 52.718L157.874 271.612 56.846 232.594zM175 296.245l204.668-145.757c-176.114 185.79-166.916 176.011-167.684 177.045-1.141 1.535 1.985-4.448-36.984 72.882zm191.858 127.546l-120.296-89.276 217.511-229.462z" />
                </svg>
            </div>
            <h1>Contáctanos</h1>
            <p>Estamos aquí para ayudarte. Envíanos un mensaje y te responderemos lo antes posible.</p>

            <form className="contact-form">
                <div className="form-group">
                    <label htmlFor="name">Nombre</label>
                    <input type="text" id="name" name="name" required />
                </div>

                <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input type="email" id="email" name="email" required />
                </div>

                <div className="form-group">
                    <label htmlFor="message">Mensaje</label>
                    <textarea id="message" name="message" rows="5" required></textarea>
                </div>

                <button type="submit" className="submit-btn">
                    Enviar Mensaje
                    <svg viewBox="0 0 512.005 512.005">
                        <path d="M511.658 51.675c2.496-11.619-8.895-21.416-20.007-17.176l-482 184a15 15 0 00-.054 28.006L145 298.8v164.713a15 15 0 0028.396 6.75l56.001-111.128 136.664 101.423c8.313 6.17 20.262 2.246 23.287-7.669C516.947 34.532 511.431 52.726 511.658 51.675zm-118.981 52.718L157.874 271.612 56.846 232.594zM175 296.245l204.668-145.757c-176.114 185.79-166.916 176.011-167.684 177.045-1.141 1.535 1.985-4.448-36.984 72.882zm191.858 127.546l-120.296-89.276 217.511-229.462z" />
                    </svg>
                </button>
            </form>
        </div>
    );
};

export default Contact;
