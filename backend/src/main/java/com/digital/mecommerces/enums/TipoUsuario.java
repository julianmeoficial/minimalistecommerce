package com.digital.mecommerces.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum para tipos de usuario del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
public enum TipoUsuario {

    ADMINISTRADOR("ADMINISTRADOR", "Administrador del sistema con acceso total", 1, true, true),
    VENDEDOR("VENDEDOR", "Usuario vendedor de productos", 2, true, false),
    COMPRADOR("COMPRADOR", "Usuario comprador de productos", 3, false, false);

    private final String codigo;
    private final String descripcion;
    private final int nivel;
    private final boolean puedeGestionarUsuarios;
    private final boolean esAdministrativo;

    TipoUsuario(String codigo, String descripcion, int nivel, boolean puedeGestionarUsuarios, boolean esAdministrativo) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.nivel = nivel;
        this.puedeGestionarUsuarios = puedeGestionarUsuarios;
        this.esAdministrativo = esAdministrativo;
    }

    // === GETTERS ===

    @JsonValue
    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getNivel() {
        return nivel;
    }

    public boolean isPuedeGestionarUsuarios() {
        return puedeGestionarUsuarios;
    }

    public boolean isEsAdministrativo() {
        return esAdministrativo;
    }

    // === MÉTODOS ESTÁTICOS ===

    @JsonCreator
    public static TipoUsuario fromCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de usuario no puede ser nulo o vacío");
        }

        for (TipoUsuario tipo : values()) {
            if (tipo.codigo.equalsIgnoreCase(codigo.trim())) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de usuario no válido: " + codigo);
    }

    public static TipoUsuario fromNivel(int nivel) {
        for (TipoUsuario tipo : values()) {
            if (tipo.nivel == nivel) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Nivel de usuario no válido: " + nivel);
    }

    public static List<TipoUsuario> obtenerTiposAdministrativos() {
        return Arrays.stream(values())
                .filter(TipoUsuario::isEsAdministrativo)
                .collect(Collectors.toList());
    }

    public static List<TipoUsuario> obtenerTiposOperativos() {
        return Arrays.stream(values())
                .filter(tipo -> !tipo.isEsAdministrativo())
                .collect(Collectors.toList());
    }

    public static List<TipoUsuario> obtenerTiposConPermisoGestion() {
        return Arrays.stream(values())
                .filter(TipoUsuario::isPuedeGestionarUsuarios)
                .collect(Collectors.toList());
    }

    public static List<String> obtenerCodigos() {
        return Arrays.stream(values())
                .map(TipoUsuario::getCodigo)
                .collect(Collectors.toList());
    }

    public static List<String> obtenerDescripciones() {
        return Arrays.stream(values())
                .map(TipoUsuario::getDescripcion)
                .collect(Collectors.toList());
    }

    public static boolean existe(String codigo) {
        try {
            fromCodigo(codigo);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // === MÉTODOS DE VALIDACIÓN Y UTILIDAD ===

    public boolean esAdministrador() {
        return this == ADMINISTRADOR;
    }

    public boolean esVendedor() {
        return this == VENDEDOR;
    }

    public boolean esComprador() {
        return this == COMPRADOR;
    }

    public boolean tieneNivelSuperiorA(TipoUsuario otro) {
        return this.nivel < otro.nivel; // Menor número = mayor nivel
    }

    public boolean tieneNivelInferiorA(TipoUsuario otro) {
        return this.nivel > otro.nivel;
    }

    public boolean tieneNivelIgualA(TipoUsuario otro) {
        return this.nivel == otro.nivel;
    }

    public boolean puedeGestionar(TipoUsuario otroTipo) {
        return this.esAdministrador() ||
                (this.puedeGestionarUsuarios && this.tieneNivelSuperiorA(otroTipo));
    }

    public boolean puedeCrearTipo(TipoUsuario tipoACrear) {
        if (this.esAdministrador()) {
            return true; // Admin puede crear cualquier tipo
        }

        if (this.esVendedor()) {
            return false; // Vendedores no pueden crear usuarios
        }

        return false; // Por defecto, no se permite
    }

    public String obtenerPermisoBase() {
        return switch (this) {
            case ADMINISTRADOR -> "ADMIN_TOTAL";
            case VENDEDOR -> "VENDER_PRODUCTOS";
            case COMPRADOR -> "COMPRAR_PRODUCTOS";
        };
    }

    public List<String> obtenerPermisosRequeridos() {
        return switch (this) {
            case ADMINISTRADOR -> List.of("ADMIN_TOTAL", "GESTIONAR_USUARIOS", "GESTIONAR_CATEGORIAS", "VENDER_PRODUCTOS", "COMPRAR_PRODUCTOS");
            case VENDEDOR -> List.of("VENDER_PRODUCTOS");
            case COMPRADOR -> List.of("COMPRAR_PRODUCTOS");
        };
    }

    public String obtenerRutaDashboard() {
        return switch (this) {
            case ADMINISTRADOR -> "/admin/dashboard";
            case VENDEDOR -> "/vendedor/dashboard";
            case COMPRADOR -> "/comprador/dashboard";
        };
    }

    public String obtenerColorTema() {
        return switch (this) {
            case ADMINISTRADOR -> "#dc2626"; // Rojo
            case VENDEDOR -> "#059669"; // Verde
            case COMPRADOR -> "#2563eb"; // Azul
        };
    }

    public String obtenerIcono() {
        return switch (this) {
            case ADMINISTRADOR -> "👑";
            case VENDEDOR -> "🏪";
            case COMPRADOR -> "🛒";
        };
    }

    // === MÉTODOS PARA VALIDACIONES DE NEGOCIO ===

    public boolean puedeAccederAAdministracion() {
        return this.esAdministrador();
    }

    public boolean puedeVenderProductos() {
        return this.esAdministrador() || this.esVendedor();
    }

    public boolean puedeComprarProductos() {
        return !this.esAdministrador(); // Todos excepto admin pueden comprar
    }

    public boolean puedeGestionarCategorias() {
        return this.esAdministrador();
    }

    public boolean puedeVerEstadisticas() {
        return this.esAdministrador() || this.esVendedor();
    }

    @Override
    public String toString() {
        return String.format("TipoUsuario{codigo='%s', descripcion='%s', nivel=%d}",
                codigo, descripcion, nivel);
    }
}
