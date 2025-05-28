package com.digital.mecommerces.security;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.RolPermisoRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final RolPermisoRepository rolPermisoRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository,
                                    RolPermisoRepository rolPermisoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolPermisoRepository = rolPermisoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("🔍 Cargando usuario por email: {}", email);

        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("⚠️ Usuario no encontrado: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        // Verificar que el usuario esté activo
        if (!usuario.getActivo()) {
            log.warn("❌ Usuario inactivo intentando autenticarse: {}", email);
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }

        // Cargar roles y permisos del usuario
        List<SimpleGrantedAuthority> authorities = cargarAutoridadesDelUsuario(usuario);

        log.info("✅ Usuario cargado exitosamente: {} con {} autoridades",
                email, authorities.size());

        // Crear UserDetails con las autoridades
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!usuario.getActivo())
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }

    private List<SimpleGrantedAuthority> cargarAutoridadesDelUsuario(Usuario usuario) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        RolUsuario rol = usuario.getRol();

        if (rol == null) {
            log.warn("⚠️ Usuario sin rol asignado: {}", usuario.getEmail());
            return authorities;
        }

        // Agregar el rol como autoridad principal
        String rolNombre = rol.getNombre();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + rolNombre));
        log.debug("📝 Rol agregado: ROLE_{}", rolNombre);

        // Verificar si es un rol del sistema optimizado
        try {
            TipoUsuario tipo = TipoUsuario.fromCodigo(rolNombre);
            log.debug("✅ Rol del sistema detectado: {} - {}",
                    tipo.getCodigo(), tipo.getDescripcion());

            // Agregar autoridades específicas del tipo de usuario
            agregarAutoridadesEspecificasPorTipo(authorities, tipo);
        } catch (IllegalArgumentException e) {
            log.debug("📝 Rol personalizado detectado: {}", rolNombre);
        }

        // Cargar permisos específicos asociados al rol
        List<RolPermiso> rolPermisos = rolPermisoRepository.findByRolIdOrderByPermisoNivel(rol.getRolId());

        for (RolPermiso rolPermiso : rolPermisos) {
            Permiso permiso = rolPermiso.getPermiso();
            if (permiso != null && permiso.getActivo()) {
                String autoridad = "PERM_" + permiso.getCodigo();
                authorities.add(new SimpleGrantedAuthority(autoridad));
                log.debug("🔑 Permiso agregado: {}", autoridad);
            }
        }

        // Agregar autoridades adicionales basadas en constantes del sistema
        agregarAutoridadesConstantes(authorities, rolNombre);

        log.info("🏆 Total de autoridades cargadas para {}: {}",
                usuario.getEmail(), authorities.size());

        return authorities;
    }

    private void agregarAutoridadesEspecificasPorTipo(List<SimpleGrantedAuthority> authorities, TipoUsuario tipo) {
        switch (tipo) {
            case ADMINISTRADOR -> {
                // Autoridades específicas de administrador
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_ADMIN_FULL"));
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_MANAGE_USERS"));
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_MANAGE_SYSTEM"));
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_VIEW_REPORTS"));
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_MANAGE_ROLES"));
                log.debug("👑 Autoridades de administrador agregadas");
            }
            case VENDEDOR -> {
                // Autoridades específicas de vendedor
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_SELL_PRODUCTS"));
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_MANAGE_INVENTORY"));
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_VIEW_SALES"));
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_MANAGE_CATEGORIES"));
                log.debug("🏪 Autoridades de vendedor agregadas");
            }
            case COMPRADOR -> {
                // Autoridades específicas de comprador
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_BUY_PRODUCTS"));
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_MANAGE_CART"));
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_VIEW_ORDERS"));
                authorities.add(new SimpleGrantedAuthority("AUTHORITY_MANAGE_PROFILE"));
                log.debug("🛒 Autoridades de comprador agregadas");
            }
        }
    }

    private void agregarAutoridadesConstantes(List<SimpleGrantedAuthority> authorities, String rolNombre) {
        // Mapear roles con constantes del sistema
        switch (rolNombre) {
            case RoleConstants.ROLE_ADMINISTRADOR -> {
                authorities.add(new SimpleGrantedAuthority(RoleConstants.PERM_ADMIN_TOTAL));
                authorities.add(new SimpleGrantedAuthority(RoleConstants.PERM_GESTIONAR_USUARIOS));
                authorities.add(new SimpleGrantedAuthority(RoleConstants.PERM_GESTIONAR_CATEGORIAS));
                authorities.add(new SimpleGrantedAuthority(RoleConstants.PERM_VENDER_PRODUCTOS));
                authorities.add(new SimpleGrantedAuthority(RoleConstants.PERM_COMPRAR_PRODUCTOS));
            }
            case RoleConstants.ROLE_VENDEDOR -> {
                authorities.add(new SimpleGrantedAuthority(RoleConstants.PERM_VENDER_PRODUCTOS));
                authorities.add(new SimpleGrantedAuthority(RoleConstants.PERM_GESTIONAR_CATEGORIAS));
            }
            case RoleConstants.ROLE_COMPRADOR -> {
                authorities.add(new SimpleGrantedAuthority(RoleConstants.PERM_COMPRAR_PRODUCTOS));
            }
        }
    }

    // Método auxiliar para verificar si un usuario tiene un permiso específico
    public boolean usuarioTienePermiso(String email, String codigoPermiso) {
        try {
            UserDetails userDetails = loadUserByUsername(email);
            return userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("PERM_" + codigoPermiso));
        } catch (UsernameNotFoundException e) {
            log.warn("⚠️ Usuario no encontrado al verificar permiso: {}", email);
            return false;
        }
    }

    // Método auxiliar para verificar si un usuario tiene un rol específico
    public boolean usuarioTieneRol(String email, String rolNombre) {
        try {
            UserDetails userDetails = loadUserByUsername(email);
            return userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + rolNombre));
        } catch (UsernameNotFoundException e) {
            log.warn("⚠️ Usuario no encontrado al verificar rol: {}", email);
            return false;
        }
    }

    // Método para obtener información del usuario autenticado
    public Usuario obtenerUsuarioAutenticado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    // Método para validar credenciales sin cargar UserDetails completo
    public boolean validarCredenciales(String email) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> usuario.getActivo() && usuario.getRol() != null)
                .orElse(false);
    }

    // Método para obtener lista de permisos como strings
    public List<String> obtenerPermisosUsuario(String email) {
        try {
            UserDetails userDetails = loadUserByUsername(email);
            return userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .filter(auth -> auth.startsWith("PERM_"))
                    .collect(Collectors.toList());
        } catch (UsernameNotFoundException e) {
            log.warn("⚠️ Usuario no encontrado al obtener permisos: {}", email);
            return new ArrayList<>();
        }
    }

    // Método para debug de autoridades
    public void debugAutoridadesUsuario(String email) {
        try {
            UserDetails userDetails = loadUserByUsername(email);
            log.info("🔍 Autoridades para usuario {}: {}", email,
                    userDetails.getAuthorities().stream()
                            .map(auth -> auth.getAuthority())
                            .collect(Collectors.joining(", ")));
        } catch (UsernameNotFoundException e) {
            log.warn("⚠️ No se pueden mostrar autoridades para usuario no encontrado: {}", email);
        }
    }
}
