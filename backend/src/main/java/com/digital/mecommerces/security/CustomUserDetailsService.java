package com.digital.mecommerces.security;

import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.RolPermisoRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
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
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Cargar roles y permisos del usuario
        RolUsuario rol = usuario.getRol();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Agregar el rol como autoridad
        authorities.add(new SimpleGrantedAuthority(rol.getNombre()));

        // CORREGIDO: Cargar permisos asociados al rol usando el método correcto
        List<Permiso> permisos = rolPermisoRepository.findByRolId(rol.getRolId())
                .stream()
                .map(rolPermiso -> rolPermiso.getPermiso())
                .collect(Collectors.toList());

        // Agregar cada permiso como autoridad
        permisos.forEach(permiso ->
                authorities.add(new SimpleGrantedAuthority("PERM_" + permiso.getCodigo()))
        );

        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }
}
