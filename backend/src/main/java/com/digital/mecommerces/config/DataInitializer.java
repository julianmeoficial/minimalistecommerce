package com.digital.mecommerces.config;

import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    @Transactional
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
            RolPermisoRepository rolPermisoRepository) {

        return args -> {
            try {
                log.info("Iniciando carga de datos de prueba...");

                // ========================================
                // CREAR ROLES SI NO EXISTEN
                // ========================================
                RolUsuario rolAdmin = rolUsuarioRepository.findByNombre("ADMINISTRADOR")
                        .orElseGet(() -> {
                            RolUsuario nuevoRol = new RolUsuario("ADMINISTRADOR", "Usuario administrador del sistema");
                            return rolUsuarioRepository.save(nuevoRol);
                        });

                RolUsuario rolComprador = rolUsuarioRepository.findByNombre("COMPRADOR")
                        .orElseGet(() -> {
                            RolUsuario nuevoRol = new RolUsuario("COMPRADOR", "Usuario comprador");
                            return rolUsuarioRepository.save(nuevoRol);
                        });

                RolUsuario rolVendedor = rolUsuarioRepository.findByNombre("VENDEDOR")
                        .orElseGet(() -> {
                            RolUsuario nuevoRol = new RolUsuario("VENDEDOR", "Usuario vendedor");
                            return rolUsuarioRepository.save(nuevoRol);
                        });

                // ========================================
                // CREAR PERMISOS SI NO EXISTEN
                // ========================================
                Permiso permisoAdminTotal = permisoRepository.findByCodigo("ADMIN_TOTAL")
                        .orElseGet(() -> permisoRepository.save(new Permiso("ADMIN_TOTAL", "Acceso total de administrador", 1)));

                Permiso permisoVenderProductos = permisoRepository.findByCodigo("VENDER_PRODUCTOS")
                        .orElseGet(() -> permisoRepository.save(new Permiso("VENDER_PRODUCTOS", "Permiso para vender productos", 2)));

                Permiso permisoComprarProductos = permisoRepository.findByCodigo("COMPRAR_PRODUCTOS")
                        .orElseGet(() -> permisoRepository.save(new Permiso("COMPRAR_PRODUCTOS", "Permiso para comprar productos", 3)));

                Permiso permisoGestionarUsuarios = permisoRepository.findByCodigo("GESTIONAR_USUARIOS")
                        .orElseGet(() -> permisoRepository.save(new Permiso("GESTIONAR_USUARIOS", "Gestionar usuarios del sistema", 4)));

                Permiso permisoGestionarCategorias = permisoRepository.findByCodigo("GESTIONAR_CATEGORIAS")
                        .orElseGet(() -> permisoRepository.save(new Permiso("GESTIONAR_CATEGORIAS", "Gestionar categorías", 5)));

                // ========================================
                // ASIGNAR PERMISOS A ROLES
                // ========================================
                if (rolPermisoRepository.findByRolId(rolAdmin.getRolId()).isEmpty()) {
                    // Crear relaciones para ADMINISTRADOR
                    RolPermiso rpAdmin1 = new RolPermiso(rolAdmin.getRolId(), permisoAdminTotal.getPermisoId());
                    rpAdmin1.setRol(rolAdmin);
                    rpAdmin1.setPermiso(permisoAdminTotal);
                    rpAdmin1.setCreatedby("SYSTEM");
                    rolPermisoRepository.save(rpAdmin1);

                    RolPermiso rpAdmin2 = new RolPermiso(rolAdmin.getRolId(), permisoVenderProductos.getPermisoId());
                    rpAdmin2.setRol(rolAdmin);
                    rpAdmin2.setPermiso(permisoVenderProductos);
                    rpAdmin2.setCreatedby("SYSTEM");
                    rolPermisoRepository.save(rpAdmin2);

                    RolPermiso rpAdmin3 = new RolPermiso(rolAdmin.getRolId(), permisoComprarProductos.getPermisoId());
                    rpAdmin3.setRol(rolAdmin);
                    rpAdmin3.setPermiso(permisoComprarProductos);
                    rpAdmin3.setCreatedby("SYSTEM");
                    rolPermisoRepository.save(rpAdmin3);

                    RolPermiso rpAdmin4 = new RolPermiso(rolAdmin.getRolId(), permisoGestionarUsuarios.getPermisoId());
                    rpAdmin4.setRol(rolAdmin);
                    rpAdmin4.setPermiso(permisoGestionarUsuarios);
                    rpAdmin4.setCreatedby("SYSTEM");
                    rolPermisoRepository.save(rpAdmin4);

                    RolPermiso rpAdmin5 = new RolPermiso(rolAdmin.getRolId(), permisoGestionarCategorias.getPermisoId());
                    rpAdmin5.setRol(rolAdmin);
                    rpAdmin5.setPermiso(permisoGestionarCategorias);
                    rpAdmin5.setCreatedby("SYSTEM");
                    rolPermisoRepository.save(rpAdmin5);

                    log.info("Permisos asignados al rol ADMINISTRADOR");
                }

                if (rolPermisoRepository.findByRolId(rolVendedor.getRolId()).isEmpty()) {
                    RolPermiso rpVendedor = new RolPermiso(rolVendedor.getRolId(), permisoVenderProductos.getPermisoId());
                    rpVendedor.setRol(rolVendedor);
                    rpVendedor.setPermiso(permisoVenderProductos);
                    rpVendedor.setCreatedby("SYSTEM");
                    rolPermisoRepository.save(rpVendedor);
                    log.info("Permisos asignados al rol VENDEDOR");
                }

                if (rolPermisoRepository.findByRolId(rolComprador.getRolId()).isEmpty()) {
                    RolPermiso rpComprador = new RolPermiso(rolComprador.getRolId(), permisoComprarProductos.getPermisoId());
                    rpComprador.setRol(rolComprador);
                    rpComprador.setPermiso(permisoComprarProductos);
                    rpComprador.setCreatedby("SYSTEM");
                    rolPermisoRepository.save(rpComprador);
                    log.info("Permisos asignados al rol COMPRADOR");
                }

                // ========================================
                // CREAR CATEGORÍAS DE PRODUCTOS
                // ========================================
                CategoriaProducto categoriaElectronica = categoriaProductoRepository.findByNombre("Electrónica")
                        .orElseGet(() -> {
                            CategoriaProducto nuevaCategoria = new CategoriaProducto();
                            nuevaCategoria.setNombre("Electrónica");
                            nuevaCategoria.setDescripcion("Productos electrónicos y tecnológicos");
                            nuevaCategoria.setActivo(true);
                            nuevaCategoria.setSlug("electronica");
                            return categoriaProductoRepository.save(nuevaCategoria);
                        });

                CategoriaProducto categoriaRopa = categoriaProductoRepository.findByNombre("Ropa")
                        .orElseGet(() -> {
                            CategoriaProducto nuevaCategoria = new CategoriaProducto();
                            nuevaCategoria.setNombre("Ropa");
                            nuevaCategoria.setDescripcion("Ropa y accesorios");
                            nuevaCategoria.setActivo(true);
                            nuevaCategoria.setSlug("ropa");
                            return categoriaProductoRepository.save(nuevaCategoria);
                        });

                CategoriaProducto categoriaHogar = categoriaProductoRepository.findByNombre("Hogar")
                        .orElseGet(() -> {
                            CategoriaProducto nuevaCategoria = new CategoriaProducto();
                            nuevaCategoria.setNombre("Hogar");
                            nuevaCategoria.setDescripcion("Artículos para el hogar");
                            nuevaCategoria.setActivo(true);
                            nuevaCategoria.setSlug("hogar");
                            return categoriaProductoRepository.save(nuevaCategoria);
                        });

                // Crear subcategorías
                CategoriaProducto subcategoriaLaptops = categoriaProductoRepository.findByNombre("Laptops")
                        .orElseGet(() -> {
                            CategoriaProducto nuevaCategoria = new CategoriaProducto();
                            nuevaCategoria.setNombre("Laptops");
                            nuevaCategoria.setDescripcion("Computadoras portátiles");
                            nuevaCategoria.setCategoriaPadre(categoriaElectronica);
                            nuevaCategoria.setCategoriapadreId(categoriaElectronica.getCategoriaId());
                            nuevaCategoria.setActivo(true);
                            nuevaCategoria.setSlug("laptops");
                            return categoriaProductoRepository.save(nuevaCategoria);
                        });

                CategoriaProducto subcategoriaSmartphones = categoriaProductoRepository.findByNombre("Smartphones")
                        .orElseGet(() -> {
                            CategoriaProducto nuevaCategoria = new CategoriaProducto();
                            nuevaCategoria.setNombre("Smartphones");
                            nuevaCategoria.setDescripcion("Teléfonos inteligentes");
                            nuevaCategoria.setCategoriaPadre(categoriaElectronica);
                            nuevaCategoria.setCategoriapadreId(categoriaElectronica.getCategoriaId());
                            nuevaCategoria.setActivo(true);
                            nuevaCategoria.setSlug("smartphones");
                            return categoriaProductoRepository.save(nuevaCategoria);
                        });

                // ========================================
                // CREAR USUARIOS DE PRUEBA SI NO EXISTEN
                // ========================================
                if (!usuarioRepository.existsByEmail("admin@mecommerces.com")) {
                    Usuario admin = new Usuario("Administrador", "admin@mecommerces.com",
                            passwordEncoder.encode("admin123"), rolAdmin);
                    admin = usuarioRepository.save(admin);

                    AdminDetalles adminDetalles = new AdminDetalles(admin);
                    adminDetalles.setRegion("Global");
                    adminDetalles.setNivelAcceso("SUPER");
                    adminDetalles.setUltimaAccion("Registro inicial del sistema");
                    adminDetalles.setUltimoLogin(LocalDateTime.now());
                    adminDetallesRepository.save(adminDetalles);

                    log.info("Usuario administrador creado: admin@mecommerces.com");
                }

                if (!usuarioRepository.existsByEmail("comprador@mecommerces.com")) {
                    Usuario comprador = new Usuario("Comprador Demo", "comprador@mecommerces.com",
                            passwordEncoder.encode("comprador123"), rolComprador);
                    comprador = usuarioRepository.save(comprador);

                    CompradorDetalles compradorDetalles = new CompradorDetalles(comprador);
                    compradorDetalles.setFechaNacimiento(LocalDate.of(1990, 1, 15));
                    compradorDetalles.setDireccionEnvio("Calle Principal 123, Ciudad");
                    compradorDetalles.setTelefono("555-123-4567");
                    compradorDetalles.setNotificacionEmail(true);
                    compradorDetalles.setNotificacionSms(false);
                    compradorDetalles.setCalificacion(new BigDecimal("5.00"));
                    compradorDetalles.setTotalCompras(0);
                    compradorDetallesRepository.save(compradorDetalles);

                    log.info("Usuario comprador creado: comprador@mecommerces.com");
                }

                if (!usuarioRepository.existsByEmail("vendedor@mecommerces.com")) {
                    Usuario vendedor = new Usuario("Vendedor Demo", "vendedor@mecommerces.com",
                            passwordEncoder.encode("vendedor123"), rolVendedor);
                    vendedor = usuarioRepository.save(vendedor);

                    VendedorDetalles vendedorDetalles = new VendedorDetalles(vendedor);
                    vendedorDetalles.setRut("12345678-9");
                    vendedorDetalles.setNumRegistroFiscal("VEND12345678");
                    vendedorDetalles.setEspecialidad("Electrónica");
                    vendedorDetalles.setDireccionComercial("Avenida Comercial 456");
                    vendedorDetalles.setVerificado(false);
                    vendedorDetallesRepository.save(vendedorDetalles);

                    log.info("Usuario vendedor creado: vendedor@mecommerces.com");
                }

                // ========================================
                // OBTENER EL USUARIO VENDEDOR PARA PRODUCTOS
                // ========================================
                Usuario vendedorObj = usuarioRepository.findByEmail("vendedor@mecommerces.com")
                        .orElseThrow(() -> new RuntimeException("Usuario vendedor no encontrado"));

                // ========================================
                // CREAR PRODUCTOS DE PRUEBA
                // ========================================
                if (productoRepository.findByProductoNombre("Laptop HP Pavilion").isEmpty()) {
                    Producto laptop = new Producto();
                    laptop.setProductoNombre("Laptop HP Pavilion");
                    laptop.setDescripcion("Laptop con procesador Intel Core i5, 8GB RAM, 256GB SSD");
                    laptop.setPrecio(899.99);
                    laptop.setStock(10);
                    laptop.setCategoria(categoriaElectronica);
                    laptop.setVendedor(vendedorObj);
                    laptop.setSlug("laptop-hp-pavilion-1");
                    laptop.setActivo(true);
                    laptop = productoRepository.save(laptop);

                    // Crear imagen para el producto
                    ProductoImagen laptopImagen = new ProductoImagen();
                    laptopImagen.setUrl("https://example.com/images/laptop.jpg");
                    laptopImagen.setDescripcion("Imagen principal de laptop");
                    laptopImagen.setEsPrincipal(true);
                    laptopImagen.setProducto(laptop);
                    laptopImagen.setTipo("imagen");
                    laptopImagen.setTamanio(1024);
                    productoImagenRepository.save(laptopImagen);

                    log.info("Producto creado: Laptop HP Pavilion");
                }

                if (productoRepository.findByProductoNombre("Smartphone Samsung Galaxy").isEmpty()) {
                    Producto smartphone = new Producto();
                    smartphone.setProductoNombre("Smartphone Samsung Galaxy");
                    smartphone.setDescripcion("Smartphone con pantalla AMOLED, 128GB almacenamiento");
                    smartphone.setPrecio(699.99);
                    smartphone.setStock(15);
                    smartphone.setCategoria(categoriaElectronica);
                    smartphone.setVendedor(vendedorObj);
                    smartphone.setSlug("smartphone-samsung-galaxy-2");
                    smartphone.setActivo(true);
                    smartphone = productoRepository.save(smartphone);

                    ProductoImagen smartphoneImagen = new ProductoImagen();
                    smartphoneImagen.setUrl("https://example.com/images/smartphone.jpg");
                    smartphoneImagen.setDescripcion("Imagen principal de smartphone");
                    smartphoneImagen.setEsPrincipal(true);
                    smartphoneImagen.setProducto(smartphone);
                    smartphoneImagen.setTipo("imagen");
                    smartphoneImagen.setTamanio(800);
                    productoImagenRepository.save(smartphoneImagen);

                    log.info("Producto creado: Smartphone Samsung Galaxy");
                }

                if (productoRepository.findByProductoNombre("Camiseta Algodón").isEmpty()) {
                    Producto camiseta = new Producto();
                    camiseta.setProductoNombre("Camiseta Algodón");
                    camiseta.setDescripcion("Camiseta 100% algodón, disponible en varios colores");
                    camiseta.setPrecio(19.99);
                    camiseta.setStock(50);
                    camiseta.setCategoria(categoriaRopa);
                    camiseta.setVendedor(vendedorObj);
                    camiseta.setSlug("camiseta-algodon-3");
                    camiseta.setActivo(true);
                    camiseta = productoRepository.save(camiseta);

                    ProductoImagen camisetaImagen = new ProductoImagen();
                    camisetaImagen.setUrl("https://example.com/images/camiseta.jpg");
                    camisetaImagen.setDescripcion("Imagen principal de camiseta");
                    camisetaImagen.setEsPrincipal(true);
                    camisetaImagen.setProducto(camiseta);
                    camisetaImagen.setTipo("imagen");
                    camisetaImagen.setTamanio(600);
                    productoImagenRepository.save(camisetaImagen);

                    log.info("Producto creado: Camiseta Algodón");
                }

                log.info("Datos de prueba cargados exitosamente");

            } catch (Exception e) {
                log.error("Error al cargar datos de prueba: {}", e.getMessage(), e);
            }
        };
    }
}
