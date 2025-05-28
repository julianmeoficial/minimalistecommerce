package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para detalles específicos de vendedores
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendedorDetallesDTO {

    @JsonProperty("usuarioId")
    private Long usuarioId;

    @JsonProperty("rfc")
    @Size(max = 20, message = "El RFC no puede exceder 20 caracteres")
    private String rfc;

    @JsonProperty("especialidad")
    @Size(max = 100, message = "La especialidad no puede exceder 100 caracteres")
    private String especialidad;

    @JsonProperty("direccionComercial")
    private String direccionComercial;

    @JsonProperty("numRegistroFiscal")
    @Size(max = 50, message = "El número de registro fiscal no puede exceder 50 caracteres")
    private String numRegistroFiscal;

    @JsonProperty("verificado")
    private Boolean verificado = false;

    @JsonProperty("fechaVerificacion")
    private LocalDateTime fechaVerificacion;

    @JsonProperty("documentoComercial")
    private String documentoComercial;

    @JsonProperty("tipoDocumento")
    @Size(max = 50, message = "El tipo de documento no puede exceder 50 caracteres")
    private String tipoDocumento;

    @JsonProperty("banco")
    @Size(max = 100, message = "El banco no puede exceder 100 caracteres")
    private String banco;

    @JsonProperty("tipoCuenta")
    @Size(max = 50, message = "El tipo de cuenta no puede exceder 50 caracteres")
    private String tipoCuenta;

    @JsonProperty("numeroCuenta")
    @Size(max = 100, message = "El número de cuenta no puede exceder 100 caracteres")
    private String numeroCuenta;

    @JsonProperty("comision")
    private BigDecimal comision;

    @JsonProperty("ventasTotales")
    private Integer ventasTotales = 0;

    @JsonProperty("calificacion")
    private BigDecimal calificacion;

    @JsonProperty("activo")
    private Boolean activo = true;

    // Información del usuario asociado
    @JsonProperty("usuarioNombre")
    private String usuarioNombre;

    @JsonProperty("usuarioEmail")
    private String usuarioEmail;

    // Campos calculados
    @JsonProperty("nivelVendedor")
    private String nivelVendedor;

    @JsonProperty("estadoVerificacion")
    private String estadoVerificacion;

    @JsonProperty("fechaRegistro")
    private LocalDateTime fechaRegistro;

    @JsonProperty("ultimaVenta")
    private LocalDateTime ultimaVenta;

    @JsonProperty("tieneProductos")
    private Boolean tieneProductos;

    @JsonProperty("numeroProductos")
    private Integer numeroProductos;

    // Constructor básico para creación
    public VendedorDetallesDTO(String numRegistroFiscal, String especialidad, String direccionComercial) {
        this.numRegistroFiscal = numRegistroFiscal;
        this.especialidad = especialidad;
        this.direccionComercial = direccionComercial;
        this.verificado = false;
        this.ventasTotales = 0;
        this.calificacion = new BigDecimal("5.00");
        this.activo = true;
    }

    // Constructor con usuario
    public VendedorDetallesDTO(Long usuarioId, String numRegistroFiscal, String especialidad, String direccionComercial) {
        this.usuarioId = usuarioId;
        this.numRegistroFiscal = numRegistroFiscal;
        this.especialidad = especialidad;
        this.direccionComercial = direccionComercial;
        this.verificado = false;
        this.ventasTotales = 0;
        this.calificacion = new BigDecimal("5.00");
        this.activo = true;
    }

    // Constructor completo con datos de venta
    public VendedorDetallesDTO(Long usuarioId, String numRegistroFiscal, String especialidad,
                               String direccionComercial, Boolean verificado, Integer ventasTotales) {
        this.usuarioId = usuarioId;
        this.numRegistroFiscal = numRegistroFiscal;
        this.especialidad = especialidad;
        this.direccionComercial = direccionComercial;
        this.verificado = verificado;
        this.ventasTotales = ventasTotales;
        this.calificacion = new BigDecimal("5.00");
        this.activo = true;
    }

    // Métodos de validación
    public boolean isValid() {
        return numRegistroFiscal != null && !numRegistroFiscal.trim().isEmpty() &&
                especialidad != null && !especialidad.trim().isEmpty() &&
                direccionComercial != null && !direccionComercial.trim().isEmpty();
    }

    public boolean tieneInformacionCompleta() {
        return isValid() &&
                rfc != null && !rfc.trim().isEmpty() &&
                tieneInformacionBancaria() &&
                tieneDocumentacion();
    }

    public boolean tieneInformacionBancaria() {
        return banco != null && !banco.trim().isEmpty() &&
                numeroCuenta != null && !numeroCuenta.trim().isEmpty() &&
                tipoCuenta != null && !tipoCuenta.trim().isEmpty();
    }

    public boolean tieneDocumentacion() {
        return documentoComercial != null && !documentoComercial.trim().isEmpty() &&
                tipoDocumento != null && !tipoDocumento.trim().isEmpty();
    }

    // Métodos de negocio
    public String calcularNivelVendedor() {
        if (ventasTotales == null || ventasTotales == 0) {
            return "NUEVO";
        } else if (ventasTotales <= 10) {
            return "BRONCE";
        } else if (ventasTotales <= 50) {
            return "PLATA";
        } else if (ventasTotales <= 100) {
            return "ORO";
        } else {
            return "PLATINO";
        }
    }

    public String obtenerEstadoVerificacion() {
        if (!Boolean.TRUE.equals(activo)) {
            return "INACTIVO";
        } else if (Boolean.TRUE.equals(verificado)) {
            return "VERIFICADO";
        } else if (tieneInformacionCompleta()) {
            return "PENDIENTE_VERIFICACION";
        } else {
            return "INFORMACION_INCOMPLETA";
        }
    }

    public boolean puedeVender() {
        return Boolean.TRUE.equals(activo) &&
                Boolean.TRUE.equals(verificado) &&
                tieneInformacionBasica();
    }

    public boolean tieneInformacionBasica() {
        return especialidad != null && !especialidad.trim().isEmpty() &&
                direccionComercial != null && !direccionComercial.trim().isEmpty() &&
                numRegistroFiscal != null && !numRegistroFiscal.trim().isEmpty();
    }

    public boolean esVendedorExperimentado() {
        return ventasTotales != null && ventasTotales >= 20;
    }

    public boolean esVendedorDestacado() {
        return Boolean.TRUE.equals(verificado) &&
                ventasTotales != null && ventasTotales >= 50 &&
                calificacion != null && calificacion.compareTo(new BigDecimal("4.5")) >= 0;
    }

    public boolean requiereVerificacion() {
        return !Boolean.TRUE.equals(verificado) && tieneInformacionCompleta();
    }

    public boolean tieneComisionPersonalizada() {
        return comision != null && comision.compareTo(BigDecimal.ZERO) > 0;
    }

    // Método para crear desde entidad
    public static VendedorDetallesDTO fromEntity(com.digital.mecommerces.model.VendedorDetalles vendedorDetalles) {
        if (vendedorDetalles == null) return null;

        VendedorDetallesDTO dto = new VendedorDetallesDTO();
        dto.setUsuarioId(vendedorDetalles.getUsuarioId());
        dto.setRfc(vendedorDetalles.getRfc());
        dto.setEspecialidad(vendedorDetalles.getEspecialidad());
        dto.setDireccionComercial(vendedorDetalles.getDireccionComercial());
        dto.setNumRegistroFiscal(vendedorDetalles.getNumRegistroFiscal());
        dto.setVerificado(vendedorDetalles.getVerificado());
        dto.setFechaVerificacion(vendedorDetalles.getFechaVerificacion());
        dto.setDocumentoComercial(vendedorDetalles.getDocumentoComercial());
        dto.setTipoDocumento(vendedorDetalles.getTipoDocumento());
        dto.setBanco(vendedorDetalles.getBanco());
        dto.setTipoCuenta(vendedorDetalles.getTipoCuenta());
        dto.setNumeroCuenta(vendedorDetalles.getNumeroCuenta());
        dto.setComision(vendedorDetalles.getComision());
        dto.setVentasTotales(vendedorDetalles.getVentasTotales());
        dto.setCalificacion(vendedorDetalles.getCalificacion());
        dto.setActivo(vendedorDetalles.getActivo());

        if (vendedorDetalles.getUsuario() != null) {
            dto.setUsuarioNombre(vendedorDetalles.getUsuario().getUsuarioNombre());
            dto.setUsuarioEmail(vendedorDetalles.getUsuario().getEmail());
            dto.setFechaRegistro(vendedorDetalles.getUsuario().getCreatedAt());
        }

        // Calcular campos derivados
        dto.setNivelVendedor(dto.calcularNivelVendedor());
        dto.setEstadoVerificacion(dto.obtenerEstadoVerificacion());

        return dto;
    }

    // Método para crear para vista pública (sin información sensible)
    public VendedorDetallesDTO toPublic() {
        VendedorDetallesDTO publicDto = new VendedorDetallesDTO();
        publicDto.setUsuarioId(this.usuarioId);
        publicDto.setUsuarioNombre(this.usuarioNombre);
        publicDto.setEspecialidad(this.especialidad);
        publicDto.setVerificado(this.verificado);
        publicDto.setVentasTotales(this.ventasTotales);
        publicDto.setCalificacion(this.calificacion);
        publicDto.setNivelVendedor(this.nivelVendedor);
        publicDto.setEstadoVerificacion(this.estadoVerificacion);
        publicDto.setActivo(this.activo);
        publicDto.setTieneProductos(this.tieneProductos);
        publicDto.setNumeroProductos(this.numeroProductos);
        // No incluir información sensible como RFC, cuenta bancaria, etc.
        return publicDto;
    }

    // Método para crear versión con información fiscal (solo para admin)
    public VendedorDetallesDTO toAdminView() {
        VendedorDetallesDTO adminDto = new VendedorDetallesDTO();
        // Incluir toda la información incluyendo datos fiscales
        adminDto.setUsuarioId(this.usuarioId);
        adminDto.setUsuarioNombre(this.usuarioNombre);
        adminDto.setUsuarioEmail(this.usuarioEmail);
        adminDto.setRfc(this.rfc);
        adminDto.setEspecialidad(this.especialidad);
        adminDto.setDireccionComercial(this.direccionComercial);
        adminDto.setNumRegistroFiscal(this.numRegistroFiscal);
        adminDto.setVerificado(this.verificado);
        adminDto.setFechaVerificacion(this.fechaVerificacion);
        adminDto.setDocumentoComercial(this.documentoComercial);
        adminDto.setTipoDocumento(this.tipoDocumento);
        adminDto.setBanco(this.banco);
        adminDto.setTipoCuenta(this.tipoCuenta);
        adminDto.setNumeroCuenta(this.numeroCuenta);
        adminDto.setComision(this.comision);
        adminDto.setVentasTotales(this.ventasTotales);
        adminDto.setCalificacion(this.calificacion);
        adminDto.setActivo(this.activo);
        adminDto.setNivelVendedor(this.nivelVendedor);
        adminDto.setEstadoVerificacion(this.estadoVerificacion);
        return adminDto;
    }

    // Validaciones específicas para el sistema
    public boolean validarRfc() {
        if (rfc == null || rfc.trim().isEmpty()) {
            return false;
        }
        // RFC de persona física: 13 caracteres
        // RFC de persona moral: 12 caracteres
        String rfcLimpio = rfc.trim().toUpperCase();
        return rfcLimpio.length() == 12 || rfcLimpio.length() == 13;
    }

    public boolean esEspecialidadValida() {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            return false;
        }

        // Lista de especialidades válidas del sistema
        String[] especialidadesValidas = {
                "Electrónica", "Ropa", "Hogar", "Deportes", "Libros",
                "Juguetes", "Belleza", "Salud", "Automotriz", "Jardín", "General"
        };

        for (String esp : especialidadesValidas) {
            if (esp.equalsIgnoreCase(especialidad.trim())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "VendedorDetallesDTO{" +
                "usuarioId=" + usuarioId +
                ", usuarioEmail='" + usuarioEmail + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", verificado=" + verificado +
                ", ventasTotales=" + ventasTotales +
                ", nivelVendedor='" + nivelVendedor + '\'' +
                ", estadoVerificacion='" + estadoVerificacion + '\'' +
                ", activo=" + activo +
                '}';
    }
}
