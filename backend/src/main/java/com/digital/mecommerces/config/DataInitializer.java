package com.digital.mecommerces.config;

import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            RolUsuarioRepository rolUsuarioRepository,
            UsuarioRepository usuarioRepository,
            CategoriaProductoRepository categoriaProductoRepository,
            ProductoRepository productoRepository,
            ProductoImagenRepository productoImagenRepository,
            PasswordEncoder passwordEncoder,
            AdminDetallesRepository adminDetallesRepository,
            CompradorDetallesRepository compradorDetallesRepository,
            VendedorDetallesRepository vendedorDetallesRepository,
            PermisoRepository permisoRepository,
            RolPermisoRepository rolPermisoRepository
    ) {
        return args -> {
            // Roles de usuario
            RolUsuario rolAdmin = rolUsuarioRepository.findByNombre("ADMINISTRADOR")
                    .orElseGet(() -> {
                        RolUsuario nuevoRol = new RolUsuario("ADMINISTRADOR", "Administrador del sistema");
                        return rolUsuarioRepository.save(nuevoRol);
                    });

            RolUsuario rolComprador = rolUsuarioRepository.findByNombre("COMPRADOR")
                    .orElseGet(() -> {
                        RolUsuario nuevoRol = new RolUsuario("COMPRADOR", "Usuario que compra productos");
                        return rolUsuarioRepository.save(nuevoRol);
                    });

            RolUsuario rolVendedor = rolUsuarioRepository.findByNombre("VENDEDOR")
                    .orElseGet(() -> {
                        RolUsuario nuevoRol = new RolUsuario("VENDEDOR", "Usuario que vende productos");
                        return rolUsuarioRepository.save(nuevoRol);
                    });

            // Permisos
            Permiso permisoAdminTotal = permisoRepository.findByCodigo("ADMIN_TOTAL")
                    .orElseGet(() -> permisoRepository.save(new Permiso("ADMIN_TOTAL", "Control total del sistema")));

            Permiso permisoVenderProductos = permisoRepository.findByCodigo("VENDER_PRODUCTOS")
                    .orElseGet(() -> permisoRepository.save(new Permiso("VENDER_PRODUCTOS", "Puede vender productos")));

            Permiso permisoComprarProductos = permisoRepository.findByCodigo("COMPRAR_PRODUCTOS")
                    .orElseGet(() -> permisoRepository.save(new Permiso("COMPRAR_PRODUCTOS", "Puede comprar productos")));

            // Asignar permisos a roles
            if (rolPermisoRepository.findByRolRolId(rolAdmin.getRolId()).isEmpty()) {
                rolPermisoRepository.save(new RolPermiso(rolAdmin, permisoAdminTotal));
                rolPermisoRepository.save(new RolPermiso(rolAdmin, permisoVenderProductos));
                rolPermisoRepository.save(new RolPermiso(rolAdmin, permisoComprarProductos));
            }

            if (rolPermisoRepository.findByRolRolId(rolVendedor.getRolId()).isEmpty()) {
                rolPermisoRepository.save(new RolPermiso(rolVendedor, permisoVenderProductos));
            }

            if (rolPermisoRepository.findByRolRolId(rolComprador.getRolId()).isEmpty()) {
                rolPermisoRepository.save(new RolPermiso(rolComprador, permisoComprarProductos));
            }

            // Categorías de productos
            CategoriaProducto categoriaElectronica = categoriaProductoRepository.findByNombre("ELECTRONICA")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("ELECTRONICA", "Productos electrónicos");
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            CategoriaProducto categoriaRopa = categoriaProductoRepository.findByNombre("ROPA")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("ROPA", "Artículos de vestir");
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            CategoriaProducto categoriaHogar = categoriaProductoRepository.findByNombre("HOGAR")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("HOGAR", "Artículos para el hogar");
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            CategoriaProducto categoriaBelleza = categoriaProductoRepository.findByNombre("BELLEZA")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("BELLEZA", "Productos de belleza y cuidado personal");
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            CategoriaProducto categoriaDeportes = categoriaProductoRepository.findByNombre("DEPORTES")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("DEPORTES", "Artículos deportivos");
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            CategoriaProducto categoriaLibros = categoriaProductoRepository.findByNombre("LIBROS")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("LIBROS", "Libros y publicaciones");
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            CategoriaProducto categoriaJuguetes = categoriaProductoRepository.findByNombre("JUGUETES")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("JUGUETES", "Juguetes y juegos");
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            CategoriaProducto categoriaAlimentos = categoriaProductoRepository.findByNombre("ALIMENTOS")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("ALIMENTOS", "Productos alimenticios");
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            // Subcategorías
            CategoriaProducto subcategoriaLaptops = categoriaProductoRepository.findByNombre("Laptops")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("Laptops", "Computadoras portátiles", categoriaElectronica);
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            CategoriaProducto subcategoriaSmartphones = categoriaProductoRepository.findByNombre("Smartphones")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("Smartphones", "Teléfonos inteligentes", categoriaElectronica);
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            CategoriaProducto subcategoriaCamisetas = categoriaProductoRepository.findByNombre("Camisetas")
                    .orElseGet(() -> {
                        CategoriaProducto nuevaCategoria = new CategoriaProducto("Camisetas", "Camisetas y polos", categoriaRopa);
                        return categoriaProductoRepository.save(nuevaCategoria);
                    });

            // Usuarios
            if (!usuarioRepository.existsByEmail("admin@mecommerces.com")) {
                Usuario admin = new Usuario(
                        "Administrador",
                        "admin@mecommerces.com",
                        passwordEncoder.encode("admin123"),
                        rolAdmin
                );
                admin = usuarioRepository.save(admin);

                // Detalles de administrador
                AdminDetalles adminDetalles = new AdminDetalles();
                adminDetalles.setUsuario(admin);
                adminDetalles.setRegion("Global");
                adminDetalles.setNivelAcceso("SUPER");
                adminDetallesRepository.save(adminDetalles);
            }

            if (!usuarioRepository.existsByEmail("comprador@mecommerces.com")) {
                Usuario comprador = new Usuario(
                        "Comprador Demo",
                        "comprador@mecommerces.com",
                        passwordEncoder.encode("comprador123"),
                        rolComprador
                );
                comprador = usuarioRepository.save(comprador);

                // Detalles de comprador
                CompradorDetalles compradorDetalles = new CompradorDetalles();
                compradorDetalles.setUsuario(comprador);
                compradorDetalles.setFechaNacimiento(LocalDate.of(1990, 1, 1));
                compradorDetalles.setDireccionEnvio("Calle Principal 123, Ciudad Demo");
                compradorDetalles.setTelefono("555-123-4567");
                compradorDetallesRepository.save(compradorDetalles);
            }

            if (!usuarioRepository.existsByEmail("vendedor@mecommerces.com")) {
                Usuario vendedor = new Usuario(
                        "Vendedor Demo",
                        "vendedor@mecommerces.com",
                        passwordEncoder.encode("vendedor123"),
                        rolVendedor
                );
                vendedor = usuarioRepository.save(vendedor);

                // Detalles de vendedor
                VendedorDetalles vendedorDetalles = new VendedorDetalles();
                vendedorDetalles.setUsuario(vendedor);
                vendedorDetalles.setNumRegistroFiscal("VEND12345678");
                vendedorDetalles.setEspecialidad("Electrónica");
                vendedorDetalles.setDireccionComercial("Avenida Comercial 456, Ciudad Demo");
                vendedorDetallesRepository.save(vendedorDetalles);

                // Obtener el usuario vendedor para asociarlo a los productos
                Usuario vendedorObj = usuarioRepository.findByEmail("vendedor@mecommerces.com")
                        .orElseThrow(() -> new RuntimeException("Usuario vendedor no encontrado"));

                // Verificar y crear productos si no existen por nombre
                if (productoRepository.findByProductoNombre("Laptop HP Pavilion").isEmpty()) {
                    Producto laptop = new Producto(
                            "Laptop HP Pavilion",
                            "Laptop con procesador Intel Core i5, 8GB RAM, 512GB SSD",
                            899.99,
                            10,
                            categoriaElectronica,
                            vendedorObj
                    );
                    laptop = productoRepository.save(laptop);

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
                            categoriaElectronica,
                            vendedorObj
                    );
                    smartphone = productoRepository.save(smartphone);

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
                            categoriaRopa,
                            vendedorObj
                    );
                    camiseta = productoRepository.save(camiseta);

                    ProductoImagen camisetaImagen = new ProductoImagen(
                            "https://example.com/images/camiseta.jpg",
                            "Imagen principal de camiseta",
                            true,
                            camiseta
                    );
                    productoImagenRepository.save(camisetaImagen);
                }
            }

            System.out.println("Base de datos inicializada con datos de prueba");
        };
    }
}
