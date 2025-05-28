package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el registro de nuevos usuarios en el sistema
 * Optimizado para el sistema medbcommerce 3.0 con soporte para múltiples roles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @JsonProperty("nombre")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email es inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    @JsonProperty("password")
    private String password;

    @NotNull(message = "El rol de usuario es obligatorio")
    @JsonProperty("rolId")
    private Long rolId;

    // === CAMPOS ESPECÍFICOS PARA ADMINISTRADOR ===
    @JsonProperty("region")
    private String region;

    @JsonProperty("nivelAcceso")
    private String nivelAcceso;

    // === CAMPOS ESPECÍFICOS PARA COMPRADOR ===
    @JsonProperty("direccionEnvio")
    private String direccionEnvio;

    @JsonProperty("telefono")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @JsonProperty("direccionAlternativa")
    private String direccionAlternativa;

    @JsonProperty("telefonoAlternativo")
    @Size(max = 20, message = "El teléfono alternativo no puede exceder 20 caracteres")
    private String telefonoAlternativo;

    // === CAMPOS ESPECÍFICOS PARA VENDEDOR ===
    @JsonProperty("numRegistroFiscal")
    @Size(max = 50, message = "El número de registro fiscal no puede exceder 50 caracteres")
    private String numRegistroFiscal;

    @JsonProperty("especialidad")
    @Size(max = 100, message = "La especialidad no puede exceder 100 caracteres")
    private String especialidad;

    @JsonProperty("direccionComercial")
    private String direccionComercial;

    @JsonProperty("rfc")
    @Size(max = 20, message = "El RFC no puede exceder 20 caracteres")
    private String rfc;

    // === CAMPOS ADICIONALES ===
    @JsonProperty("aceptaTerminos")
    private Boolean aceptaTerminos = false;

    @JsonProperty("aceptaPrivacidad")
    private Boolean aceptaPrivacidad = false;

    @JsonProperty("recibirNotificaciones")
    private Boolean recibirNotificaciones = true;

    // Constructor para registro básico
    public RegistroDTO(String nombre, String email, String password, Long rolId) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rolId = rolId;
        this.aceptaTerminos = false;
        this.aceptaPrivacidad = false;
        this.recibirNotificaciones = true;
    }

    // Constructor para registro de comprador
    public RegistroDTO(String nombre, String email, String password, Long rolId,
                       String direccionEnvio, String telefono) {
        this(nombre, email, password, rolId);
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
    }

    // Constructor para registro de vendedor
    public RegistroDTO(String nombre, String email, String password, Long rolId,
                       String numRegistroFiscal, String especialidad, String direccionComercial) {
        this(nombre, email, password, rolId);
        this.numRegistroFiscal = numRegistroFiscal;
        this.especialidad = especialidad;
        this.direccionComercial = direccionComercial;
    }

    // Métodos de validación personalizada
    public boolean isValidForRole(String rolNombre) {
        switch (rolNombre.toUpperCase()) {
            case "ADMINISTRADOR":
                return isValidBasic();
            case "COMPRADOR":
                return isValidBasic() &&
                        (direccionEnvio != null && !direccionEnvio.trim().isEmpty());
            case "VENDEDOR":
                return isValidBasic() &&
                        (especialidad != null && !especialidad.trim().isEmpty());
            default:
                return isValidBasic();
        }
    }

    private boolean isValidBasic() {
        return nombre != null && !nombre.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() && email.contains("@") &&
                password != null && password.length() >= 6 &&
                rolId != null && rolId > 0 &&
                Boolean.TRUE.equals(aceptaTerminos) &&
                Boolean.TRUE.equals(aceptaPrivacidad);
    }

    public boolean tieneInformacionCompleta() {
        return isValidBasic() &&
                ((direccionEnvio != null && telefono != null) || // Comprador
                        (numRegistroFiscal != null && especialidad != null) || // Vendedor
                        (region != null)); // Admin
    }

    // Método para limpiar datos sensibles en logs
    public RegistroDTO toSafeLog() {
        RegistroDTO safeDto = new RegistroDTO();
        safeDto.setNombre(this.nombre);
        safeDto.setEmail(this.email);
        safeDto.setPassword("***"); // Ocultar contraseña
        safeDto.setRolId(this.rolId);
        safeDto.setRegion(this.region);
        safeDto.setEspecialidad(this.especialidad);
        // No incluir información sensible como RFC, teléfonos, etc.
        return safeDto;
    }

    @Override
    public String toString() {
        return "RegistroDTO{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", password='***'" + // Nunca mostrar la contraseña
                ", rolId=" + rolId +
                ", region='" + region + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", aceptaTerminos=" + aceptaTerminos +
                ", aceptaPrivacidad=" + aceptaPrivacidad +
                '}';
    }
}
