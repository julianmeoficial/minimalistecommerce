import React from 'react';
import styled from 'styled-components';
import Card from './Card';  // Importamos el componente de tarjeta 3D

const UserProfile = () => {
    return (
        <ProfileContainer>
            <ProfileHeader>
                <h1>Perfil de Usuario</h1>
            </ProfileHeader>

            <CardContainer>
                <Card /> {/* Aquí integramos la tarjeta 3D sin modificarla */}
            </CardContainer>

            <ProfileInfo>
                <InfoSection>
                    <h2>Información Personal</h2>
                    <InfoGrid>
                        <InfoItem>
                            <InfoLabel>Nombre:</InfoLabel>
                            <InfoValue>Ana Espitia</InfoValue>
                        </InfoItem>
                        <InfoItem>
                            <InfoLabel>Email:</InfoLabel>
                            <InfoValue>anaespitiao@ecommerce.com</InfoValue>
                        </InfoItem>
                        <InfoItem>
                            <InfoLabel>Teléfono:</InfoLabel>
                            <InfoValue>+57 302 256 1879</InfoValue>
                        </InfoItem>
                        <InfoItem>
                            <InfoLabel>Dirección:</InfoLabel>
                            <InfoValue>Calle 22, Montería</InfoValue>
                        </InfoItem>
                    </InfoGrid>
                </InfoSection>
            </ProfileInfo>
        </ProfileContainer>
    );
};

// Estilizado para el contenedor principal
const ProfileContainer = styled.div`
    width: 100%;
    min-height: calc(100vh - 150px);
    padding: 2rem;
    background-color: #121212;
    display: flex;
    flex-direction: column;
    align-items: center;
`;

// Estilizado para el encabezado
const ProfileHeader = styled.div`
    width: 100%;
    text-align: center;
    margin-bottom: 2rem;

    h1 {
        font-size: 2.5rem;
        color: #fff;
    }
`;

// Contenedor para centrar la tarjeta 3D
const CardContainer = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 2rem 0;
    width: 100%;
    height: 350px; /* Altura fija para mantener el diseño consistente */
`;

// Sección de información adicional
const ProfileInfo = styled.div`
    width: 100%;
    max-width: 800px;
    background-color: #1a1a1a;
    border-radius: 8px;
    padding: 2rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    color: #fff;
`;

const InfoSection = styled.div`
    margin-bottom: 1.5rem;

    h2 {
        color: #fff;
        border-bottom: 1px solid #333;
        padding-bottom: 0.5rem;
        margin-bottom: 1rem;
    }
`;

const InfoGrid = styled.div`
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;

    @media (max-width: 768px) {
        grid-template-columns: 1fr;
    }
`;

const InfoItem = styled.div`
    display: flex;
    flex-direction: column;
`;

const InfoLabel = styled.span`
    font-weight: 500;
    color: #999;
    margin-bottom: 0.25rem;
`;

const InfoValue = styled.span`
    color: #fff;
    font-size: 1.1rem;
`;

export default UserProfile;
