package com.digital.mecommerces.config;

import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            TipoRepository tipoRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            ProductoImagenRepository productoImagenRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Verificar y crear tipos de usuario si no existen
            Tipo tipoAdmin = tipoRepository.findFirstByTipoNombreAndTipoCategoria("Administrador", TipoCategoria.ADMINISTRADOR)
                    .orElseGet(() -> {
                        Tipo nuevoTipo = new Tipo("Administrador", TipoCategoria.ADMINISTRADOR);
                        return tipoRepository.save(nuevoTipo);
                    });

            Tipo tipoComprador = tipoRepository.findFirstByTipoNombreAndTipoCategoria("Comprador", TipoCategoria.COMPRADOR)
                    .orElseGet(() -> {
                        Tipo nuevoTipo = new Tipo("Comprador", TipoCategoria.COMPRADOR);
                        return tipoRepository.save(nuevoTipo);
                    });

            Tipo tipoVendedor = tipoRepository.findFirstByTipoNombreAndTipoCategoria("Vendedor", TipoCategoria.VENDEDOR)
                    .orElseGet(() -> {
                        Tipo nuevoTipo = new Tipo("Vendedor", TipoCategoria.VENDEDOR);
                        return tipoRepository.save(nuevoTipo);
                    });

            // Verificar y crear tipos de producto si no existen
            Tipo tipoElectronica = tipoRepository.findFirstByTipoNombreAndTipoCategoria("Electrónica", TipoCategoria.ELECTRONICA)
                    .orElseGet(() -> {
                        Tipo nuevoTipo = new Tipo("Electrónica", TipoCategoria.ELECTRONICA);
                        return tipoRepository.save(nuevoTipo);
                    });

            Tipo tipoRopa = tipoRepository.findFirstByTipoNombreAndTipoCategoria("Ropa", TipoCategoria.ROPA)
                    .orElseGet(() -> {
                        Tipo nuevoTipo = new Tipo("Ropa", TipoCategoria.ROPA);
                        return tipoRepository.save(nuevoTipo);
                    });

            Tipo tipoHogar = tipoRepository.findFirstByTipoNombreAndTipoCategoria("Hogar", TipoCategoria.HOGAR)
                    .orElseGet(() -> {
                        Tipo nuevoTipo = new Tipo("Hogar", TipoCategoria.HOGAR);
                        return tipoRepository.save(nuevoTipo);
                    });

            // Verificar y crear usuarios si no existen
            if (!usuarioRepository.existsByEmail("admin@mecommerces.com")) {
                Usuario admin = new Usuario(
                        "Administrador",
                        "admin@mecommerces.com",
                        passwordEncoder.encode("admin123"),
                        tipoAdmin
                );
                usuarioRepository.save(admin);
            }

            if (!usuarioRepository.existsByEmail("comprador@mecommerces.com")) {
                Usuario comprador = new Usuario(
                        "Comprador Demo",
                        "comprador@mecommerces.com",
                        passwordEncoder.encode("comprador123"),
                        tipoComprador
                );
                usuarioRepository.save(comprador);
            }

            if (!usuarioRepository.existsByEmail("vendedor@mecommerces.com")) {
                Usuario vendedor = new Usuario(
                        "Vendedor Demo",
                        "vendedor@mecommerces.com",
                        passwordEncoder.encode("vendedor123"),
                        tipoVendedor
                );
                usuarioRepository.save(vendedor);
            }

            // Obtener el usuario vendedor para asociarlo a los productos
            Usuario vendedor = usuarioRepository.findByEmail("vendedor@mecommerces.com")
                    .orElseThrow(() -> new RuntimeException("Usuario vendedor no encontrado"));

            // Verificar y crear productos si no existen (por nombre)
            if (productoRepository.findByProductoNombre("Laptop HP Pavilion").isEmpty()) {
                Producto laptop = new Producto(
                        "Laptop HP Pavilion",
                        "Laptop con procesador Intel Core i5, 8GB RAM, 512GB SSD",
                        899.99,
                        10,
                        tipoElectronica,
                        vendedor
                );
                productoRepository.save(laptop);

                // Crear imagen para el producto
                ProductoImagen laptopImagen = new ProductoImagen(
                        "https://example.com/images/laptop.jpg",
                        "Imagen principal de laptop",
                        true,
                        laptop
                );
                productoImagenRepository.save(laptopImagen);
            }

            if (productoRepository.findByProductoNombre("Smartphone Samsung Galaxy").isEmpty()) {
                Producto smartphone = new Producto(
                        "Smartphone Samsung Galaxy",
                        "Smartphone con pantalla AMOLED de 6.5\", 128GB almacenamiento",
                        699.99,
                        15,
                        tipoElectronica,
                        vendedor
                );
                productoRepository.save(smartphone);

                ProductoImagen smartphoneImagen = new ProductoImagen(
                        "https://example.com/images/smartphone.jpg",
                        "Imagen principal de smartphone",
                        true,
                        smartphone
                );
                productoImagenRepository.save(smartphoneImagen);
            }

            if (productoRepository.findByProductoNombre("Camiseta Algodón").isEmpty()) {
                Producto camiseta = new Producto(
                        "Camiseta Algodón",
                        "Camiseta 100% algodón, disponible en varios colores",
                        19.99,
                        50,
                        tipoRopa,
                        vendedor
                );
                productoRepository.save(camiseta);

                ProductoImagen camisetaImagen = new ProductoImagen(
                        "https://example.com/images/camiseta.jpg",
                        "Imagen principal de camiseta",
                        true,
                        camiseta
                );
                productoImagenRepository.save(camisetaImagen);
            }

            System.out.println("Base de datos inicializada con datos de prueba");
        };
    }
}