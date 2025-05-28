package com.digital.mecommerces.config;

import com.digital.mecommerces.enums.TipoCategoria;
import com.digital.mecommerces.enums.TipoUsuario;
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
import java.util.List;

/**
 * Inicializador de datos optimizado para el sistema completo
 * Sistema medbcommerce 3.0
 */
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
                log.info("🚀 Iniciando carga de datos optimizada para medbcommerce 3.0...");

                // 1. Crear permisos del sistema
                crearPermisosDelSistema(permisoRepository);

                // 2. Crear roles del sistema
                crearRolesDelSistema(rolUsuarioRepository);

                // 3. Asignar permisos a roles
                asignarPermisosARoles(rolUsuarioRepository, permisoRepository, rolPermisoRepository);

                // 4. Crear categorías del sistema
                crearCategoriasDelSistema(categoriaProductoRepository);

                // 5. Crear usuarios de prueba
                crearUsuariosDePrueba(usuarioRepository, rolUsuarioRepository, passwordEncoder,
                        adminDetallesRepository, compradorDetallesRepository, vendedorDetallesRepository);

                // 6. Crear productos de prueba
                crearProductosDePrueba(productoRepository, categoriaProductoRepository,
                        usuarioRepository, productoImagenRepository);

                log.info("✅ Datos de prueba cargados exitosamente para medbcommerce 3.0");

            } catch (Exception e) {
                log.error("❌ Error al cargar datos de prueba: {}", e.getMessage(), e);
                throw new RuntimeException("Error crítico en inicialización de datos", e);
            }
        };
    }

    private void crearPermisosDelSistema(PermisoRepository permisoRepository) {
        log.info("🔑 Creando permisos del sistema...");

        // Definir permisos del sistema con jerarquía
        Object[][] permisos = {
                {"ADMIN_TOTAL", "Administración total del sistema", 1, null, "SISTEMA"},
                {"GESTIONAR_USUARIOS", "Gestionar usuarios del sistema", 2, "ADMIN_TOTAL", "USUARIOS"},
                {"GESTIONAR_CATEGORIAS", "Gestionar categorías de productos", 2, "ADMIN_TOTAL", "CATEGORIAS"},
                {"VENDER_PRODUCTOS", "Vender y gestionar productos", 3, null, "VENTAS"},
                {"COMPRAR_PRODUCTOS", "Comprar productos del sistema", 4, null, "COMPRAS"},
                {"GESTIONAR_ORDENES", "Gestionar órdenes y pedidos", 3, "VENDER_PRODUCTOS", "VENTAS"},
                {"VER_ESTADISTICAS", "Ver estadísticas y reportes", 3, null, "REPORTES"},
                {"GESTIONAR_INVENTARIO", "Gestionar inventario de productos", 3, "VENDER_PRODUCTOS", "INVENTARIO"},
                {"PROCESAR_PAGOS", "Procesar pagos y transacciones", 2, "ADMIN_TOTAL", "PAGOS"},
                {"CONFIGURAR_SISTEMA", "Configurar parámetros del sistema", 1, "ADMIN_TOTAL", "CONFIGURACION"},
                {"MODERAR_CONTENIDO", "Moderar contenido y reseñas", 2, "ADMIN_TOTAL", "MODERACION"}
        };

        for (Object[] permisoData : permisos) {
            String codigo = (String) permisoData[0];
            String descripcion = (String) permisoData[1];
            Integer nivel = (Integer) permisoData[2];
            String codigoPadre = (String) permisoData[3];
            String categoria = (String) permisoData[4];

            if (!permisoRepository.existsByCodigo(codigo)) {
                Permiso permiso = new Permiso();
                permiso.setCodigo(codigo);
                permiso.setDescripcion(descripcion);
                permiso.setNivel(nivel);
                permiso.setCategoria(categoria);
                permiso.setActivo(true);

                if (codigoPadre != null) {
                    permisoRepository.findByCodigo(codigoPadre)
                            .ifPresent(permiso::setPermisoPadre);
                }

                permisoRepository.save(permiso);
                log.debug("🔑 Permiso creado: {}", codigo);
            }
        }

        log.info("✅ {} permisos del sistema verificados/creados", permisos.length);
    }

    private void crearRolesDelSistema(RolUsuarioRepository rolUsuarioRepository) {
        log.info("👥 Creando roles del sistema...");

        // Definir roles del sistema
        Object[][] roles = {
                {"ADMINISTRADOR", "Administrador del sistema con acceso total"},
                {"VENDEDOR", "Usuario vendedor de productos"},
                {"COMPRADOR", "Usuario comprador de productos"}
        };

        for (Object[] rolData : roles) {
            String nombre = (String) rolData[0];
            String descripcion = (String) rolData[1];

            if (!rolUsuarioRepository.existsByNombre(nombre)) {
                RolUsuario rol = new RolUsuario();
                rol.setNombre(nombre);
                rol.setDescripcion(descripcion);
                rolUsuarioRepository.save(rol);
                log.debug("👥 Rol creado: {}", nombre);
            }
        }

        log.info("✅ {} roles del sistema verificados/creados", roles.length);
    }

    private void asignarPermisosARoles(RolUsuarioRepository rolUsuarioRepository,
                                       PermisoRepository permisoRepository,
                                       RolPermisoRepository rolPermisoRepository) {
        log.info("🔗 Asignando permisos a roles...");

        // Permisos para ADMINISTRADOR (todos los permisos)
        RolUsuario rolAdmin = rolUsuarioRepository.findByNombre("ADMINISTRADOR").orElse(null);
        if (rolAdmin != null && rolPermisoRepository.findByRol_RolId(rolAdmin.getRolId()).isEmpty()) {
            List<Permiso> todosLosPermisos = permisoRepository.findAll();
            for (Permiso permiso : todosLosPermisos) {
                RolPermiso rolPermiso = new RolPermiso();
                rolPermiso.setRol(rolAdmin);
                rolPermiso.setPermiso(permiso);
                rolPermisoRepository.save(rolPermiso);
            }
            log.info("🔗 {} permisos asignados al rol ADMINISTRADOR", todosLosPermisos.size());
        }

        // Permisos para VENDEDOR
        RolUsuario rolVendedor = rolUsuarioRepository.findByNombre("VENDEDOR").orElse(null);
        if (rolVendedor != null && rolPermisoRepository.findByRol_RolId(rolVendedor.getRolId()).isEmpty()) {
            String[] permisosVendedor = {"VENDER_PRODUCTOS", "GESTIONAR_ORDENES", "VER_ESTADISTICAS", "GESTIONAR_INVENTARIO"};
            asignarPermisosEspecificos(rolVendedor, permisosVendedor, permisoRepository, rolPermisoRepository);
            log.info("🔗 {} permisos asignados al rol VENDEDOR", permisosVendedor.length);
        }

        // Permisos para COMPRADOR
        RolUsuario rolComprador = rolUsuarioRepository.findByNombre("COMPRADOR").orElse(null);
        if (rolComprador != null && rolPermisoRepository.findByRol_RolId(rolComprador.getRolId()).isEmpty()) {
            String[] permisosComprador = {"COMPRAR_PRODUCTOS"};
            asignarPermisosEspecificos(rolComprador, permisosComprador, permisoRepository, rolPermisoRepository);
            log.info("🔗 {} permisos asignados al rol COMPRADOR", permisosComprador.length);
        }
    }

    private void asignarPermisosEspecificos(RolUsuario rol, String[] codigosPermisos,
                                            PermisoRepository permisoRepository,
                                            RolPermisoRepository rolPermisoRepository) {
        for (String codigo : codigosPermisos) {
            permisoRepository.findByCodigo(codigo).ifPresent(permiso -> {
                RolPermiso rolPermiso = new RolPermiso();
                rolPermiso.setRol(rol);
                rolPermiso.setPermiso(permiso);
                rolPermisoRepository.save(rolPermiso);
            });
        }
    }

    private void crearCategoriasDelSistema(CategoriaProductoRepository categoriaProductoRepository) {
        log.info("📦 Creando categorías del sistema...");

        // Crear categorías principales del sistema
        for (TipoCategoria tipoCategoria : TipoCategoria.values()) {
            if (!categoriaProductoRepository.existsBySlug(tipoCategoria.getSlug())) {
                CategoriaProducto categoria = new CategoriaProducto();
                categoria.setNombre(tipoCategoria.getDescripcion());
                categoria.setDescripcion(tipoCategoria.getDescripcion());
                categoria.setSlug(tipoCategoria.getSlug());
                categoria.setActivo(tipoCategoria.isEsCategoriaPrincipal());
                categoria.setImagen("/images/categorias/" + tipoCategoria.getSlug() + ".jpg");
                categoriaProductoRepository.save(categoria);
                log.debug("📦 Categoría creada: {}", categoria.getNombre());
            }
        }

        // Crear algunas subcategorías de ejemplo
        crearSubcategorias(categoriaProductoRepository);

        log.info("✅ Categorías del sistema verificadas/creadas");
    }

    private void crearSubcategorias(CategoriaProductoRepository categoriaProductoRepository) {
        // Subcategorías para Tecnología
        CategoriaProducto tecnologia = categoriaProductoRepository.findBySlug("tecnologia").orElse(null);
        if (tecnologia != null) {
            crearSubcategoriasSiNoExisten(categoriaProductoRepository, tecnologia, new String[][]{
                    {"Laptops", "Computadoras portátiles y notebooks", "laptops"},
                    {"Smartphones", "Teléfonos inteligentes", "smartphones"},
                    {"Tablets", "Tabletas y dispositivos móviles", "tablets"}
            });
        }

        // Subcategorías para Ropa
        CategoriaProducto ropa = categoriaProductoRepository.findBySlug("ropa").orElse(null);
        if (ropa != null) {
            crearSubcategoriasSiNoExisten(categoriaProductoRepository, ropa, new String[][]{
                    {"Camisetas", "Camisetas para hombre y mujer", "camisetas"},
                    {"Pantalones", "Pantalones y jeans", "pantalones"},
                    {"Zapatos", "Calzado deportivo y casual", "zapatos"}
            });
        }
    }

    private void crearSubcategoriasSiNoExisten(CategoriaProductoRepository repository,
                                               CategoriaProducto padre, String[][] subcategorias) {
        for (String[] subcat : subcategorias) {
            if (!repository.existsBySlug(subcat[2])) {
                CategoriaProducto subcategoria = new CategoriaProducto();
                subcategoria.setNombre(subcat[0]);
                subcategoria.setDescripcion(subcat[1]);
                subcategoria.setSlug(subcat[2]);
                subcategoria.setCategoriaPadre(padre);
                subcategoria.setActivo(true);
                repository.save(subcategoria);
            }
        }
    }

    private void crearUsuariosDePrueba(UsuarioRepository usuarioRepository,
                                       RolUsuarioRepository rolUsuarioRepository,
                                       PasswordEncoder passwordEncoder,
                                       AdminDetallesRepository adminDetallesRepository,
                                       CompradorDetallesRepository compradorDetallesRepository,
                                       VendedorDetallesRepository vendedorDetallesRepository) {
        log.info("👤 Creando usuarios de prueba...");

        // Usuario Administrador
        if (!usuarioRepository.existsByEmail("admin@mecommerces.com")) {
            crearUsuarioConDetalles("admin@mecommerces.com", "Administrador", "admin123",
                    "ADMINISTRADOR", usuarioRepository, rolUsuarioRepository,
                    passwordEncoder, adminDetallesRepository, compradorDetallesRepository,
                    vendedorDetallesRepository);
        }

        // Usuario Comprador
        if (!usuarioRepository.existsByEmail("comprador@mecommerces.com")) {
            crearUsuarioConDetalles("comprador@mecommerces.com", "Comprador Demo", "comprador123",
                    "COMPRADOR", usuarioRepository, rolUsuarioRepository,
                    passwordEncoder, adminDetallesRepository, compradorDetallesRepository,
                    vendedorDetallesRepository);
        }

        // Usuario Vendedor
        if (!usuarioRepository.existsByEmail("vendedor@mecommerces.com")) {
            crearUsuarioConDetalles("vendedor@mecommerces.com", "Vendedor Demo", "vendedor123",
                    "VENDEDOR", usuarioRepository, rolUsuarioRepository,
                    passwordEncoder, adminDetallesRepository, compradorDetallesRepository,
                    vendedorDetallesRepository);
        }

        log.info("✅ Usuarios de prueba verificados/creados");
    }

    private void crearUsuarioConDetalles(String email, String nombre, String password, String rolNombre,
                                         UsuarioRepository usuarioRepository,
                                         RolUsuarioRepository rolUsuarioRepository,
                                         PasswordEncoder passwordEncoder,
                                         AdminDetallesRepository adminDetallesRepository,
                                         CompradorDetallesRepository compradorDetallesRepository,
                                         VendedorDetallesRepository vendedorDetallesRepository) {

        RolUsuario rol = rolUsuarioRepository.findByNombre(rolNombre).orElse(null);
        if (rol == null) return;

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setUsuarioNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setUpdatedAt(LocalDateTime.now());
        usuario = usuarioRepository.saveAndFlush(usuario);

        // Crear detalles específicos según el rol
        switch (rolNombre) {
            case "ADMINISTRADOR":
                crearDetallesAdmin(usuario, adminDetallesRepository);
                break;
            case "COMPRADOR":
                crearDetallesComprador(usuario, compradorDetallesRepository);
                break;
            case "VENDEDOR":
                crearDetallesVendedor(usuario, vendedorDetallesRepository);
                break;
        }

        log.debug("👤 Usuario creado: {} ({})", email, rolNombre);
    }

    private void crearDetallesAdmin(Usuario usuario, AdminDetallesRepository repository) {
        AdminDetalles detalles = new AdminDetalles();
        detalles.setUsuario(usuario);
        detalles.setUsuarioId(usuario.getUsuarioId());
        detalles.setRegion("Global");
        detalles.setNivelAcceso("SUPER");
        detalles.setUltimaAccion("Registro inicial del sistema");
        detalles.setUltimoLogin(LocalDateTime.now());
        repository.saveAndFlush(detalles);
    }

    private void crearDetallesComprador(Usuario usuario, CompradorDetallesRepository repository) {
        CompradorDetalles detalles = new CompradorDetalles();
        detalles.setUsuario(usuario);
        detalles.setUsuarioId(usuario.getUsuarioId());
        detalles.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        detalles.setDireccionEnvio("Calle Principal 123, Ciudad Demo");
        detalles.setTelefono("555-123-4567");
        detalles.setNotificacionEmail(true);
        detalles.setNotificacionSms(false);
        detalles.setCalificacion(new BigDecimal("5.00"));
        detalles.setTotalCompras(0);
        repository.saveAndFlush(detalles);
    }

    private void crearDetallesVendedor(Usuario usuario, VendedorDetallesRepository repository) {
        VendedorDetalles detalles = new VendedorDetalles();
        detalles.setUsuario(usuario);
        detalles.setUsuarioId(usuario.getUsuarioId());
        detalles.setNumRegistroFiscal("VEND12345678");
        detalles.setEspecialidad("Electrónica");
        detalles.setDireccionComercial("Avenida Comercial 456, Ciudad Demo");
        detalles.setVerificado(true);
        detalles.setFechaVerificacion(LocalDateTime.now());
        repository.saveAndFlush(detalles);
    }

    private void crearProductosDePrueba(ProductoRepository productoRepository,
                                        CategoriaProductoRepository categoriaProductoRepository,
                                        UsuarioRepository usuarioRepository,
                                        ProductoImagenRepository productoImagenRepository) {
        log.info("🛍️ Creando productos de prueba...");

        Usuario vendedor = usuarioRepository.findByEmail("vendedor@mecommerces.com").orElse(null);
        if (vendedor == null) return;

        // Productos de muestra con diferentes categorías
        Object[][] productos = {
                {"Laptop HP Pavilion", "Laptop con procesador Intel Core i5, 8GB RAM", 899.99, 10, "tecnologia", "laptop-hp-pavilion"},
                {"Smartphone Samsung Galaxy", "Smartphone con pantalla AMOLED", 699.99, 15, "tecnologia", "smartphone-samsung-galaxy"},
                {"Camiseta Algodón", "Camiseta 100% algodón, disponible en varios colores", 19.99, 50, "ropa", "camiseta-algodon"},
                {"Auriculares Bluetooth", "Auriculares inalámbricos con cancelación de ruido", 129.99, 25, "tecnologia", "auriculares-bluetooth"},
                {"Zapatillas Deportivas", "Zapatillas cómodas para running y ejercicio", 89.99, 30, "ropa", "zapatillas-deportivas"}
        };

        for (Object[] productoData : productos) {
            String nombre = (String) productoData[0];

            if (productoRepository.findByProductoNombre(nombre).isEmpty()) {
                String descripcion = (String) productoData[1];
                Double precio = (Double) productoData[2];
                Integer stock = (Integer) productoData[3];
                String categoriaSlug = (String) productoData[4];
                String slug = (String) productoData[5];

                CategoriaProducto categoria = categoriaProductoRepository.findBySlug(categoriaSlug).orElse(null);
                if (categoria != null) {
                    Producto producto = new Producto();
                    producto.setProductoNombre(nombre);
                    producto.setDescripcion(descripcion);
                    producto.setPrecio(precio);
                    producto.setStock(stock);
                    producto.setCategoria(categoria);
                    producto.setVendedor(vendedor);
                    producto.setSlug(slug);
                    producto.setActivo(true);
                    producto.setDestacado(stock > 20); // Destacar productos con más stock
                    producto.setCreatedAt(LocalDateTime.now());
                    producto.setUpdatedAt(LocalDateTime.now());
                    producto = productoRepository.save(producto);

                    // Crear imagen para el producto
                    crearImagenProducto(producto, productoImagenRepository);

                    log.debug("🛍️ Producto creado: {}", nombre);
                }
            }
        }

        log.info("✅ Productos de prueba verificados/creados");
    }

    private void crearImagenProducto(Producto producto, ProductoImagenRepository repository) {
        ProductoImagen imagen = new ProductoImagen();
        imagen.setUrl("https://via.placeholder.com/400x300/007bff/ffffff?text=" +
                producto.getProductoNombre().replace(" ", "+"));
        imagen.setDescripcion("Imagen principal de " + producto.getProductoNombre());
        imagen.setEsPrincipal(true);
        imagen.setProducto(producto);
        imagen.setTipo("principal");
        imagen.setActiva(true);
        imagen.setOrden(1);
        imagen.setCreatedAt(LocalDateTime.now());
        repository.save(imagen);
    }
}
