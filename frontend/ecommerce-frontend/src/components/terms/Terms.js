import React from 'react';
import styled from 'styled-components';
import TruckLoader from '../common/TruckLoader';
import GradientButton from '../common/GradientButton';

const Terms = () => {
    return (
        <TermsContainer>
            <div className="loader-container">
                <TruckLoader />
            </div>

            <h1>Términos y Condiciones</h1>

            <div className="terms-content">
                <section>
                    <h2>1. Introducción</h2>
                    <p>Bienvenido a Mecommerces. Estos términos y condiciones rigen el uso de nuestro sitio web y servicios. Al acceder a nuestro sitio, usted acepta estos términos y condiciones en su totalidad.</p>
                </section>

                <section>
                    <h2>2. Uso del Sitio</h2>
                    <p>Nuestro sitio web y su contenido son propiedad de Mecommerces. El contenido del sitio incluye, entre otros, textos, gráficos, imágenes, logotipos, iconos, software y cualquier otro material.</p>
                </section>

                <section>
                    <h2>3. Cuentas de Usuario</h2>
                    <p>Al crear una cuenta en nuestro sitio, usted es responsable de mantener la confidencialidad de su cuenta y contraseña. Usted acepta la responsabilidad de todas las actividades que ocurran bajo su cuenta.</p>
                </section>

                <section>
                    <h2>4. Privacidad</h2>
                    <p>Su uso de nuestro sitio está sujeto a nuestra Política de Privacidad, que puede consultar en nuestra página de Política de Privacidad.</p>
                </section>

                <section>
                    <h2>5. Limitación de Responsabilidad</h2>
                    <p>En ningún caso Mecommerces, sus directores, empleados o agentes serán responsables de cualquier daño directo, indirecto, punitivo, incidental, especial o consecuente que surja de o de alguna manera esté relacionado con el uso de este sitio.</p>
                </section>

                <div className="back-button">
                    <GradientButton to="/dashboard">Volver al Inicio</GradientButton>
                </div>
            </div>
        </TermsContainer>
    );
};

const TermsContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
  padding: 2rem;
  background-color: #121212;
  color: #fff;

  .loader-container {
    margin: 2rem 0;
  }

  h1 {
    font-size: 2.5rem;
    margin-bottom: 2rem;
    color: #fff;
    text-align: center;
  }

  .terms-content {
    max-width: 800px;
    width: 100%;
    background-color: #1a1a1a;
    border-radius: 10px;
    padding: 2rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }

  section {
    margin-bottom: 2rem;
  }

  h2 {
    color: #2d8cf0;
    margin-bottom: 1rem;
    font-size: 1.5rem;
  }

  p {
    line-height: 1.6;
    color: #ccc;
    margin-bottom: 1rem;
  }

  .back-button {
    margin-top: 2rem;
    display: flex;
    justify-content: center;
  }
`;

export default Terms;
