package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.VendedorDetalles;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendedorDetallesRepository extends JpaRepository<VendedorDetalles, Long> {

    // Búsquedas básicas por ID de usuario
    Optional<VendedorDetalles> findByUsuarioId(Long usuarioId);

    // Verificar existencia por ID de usuario
    boolean existsByUsuarioId(Long usuarioId);

    // Eliminar por ID de usuario
    void deleteByUsuarioId(Long usuarioId);

    // Búsqueda por usuario completo
    Optional<VendedorDetalles> findByUsuario(Usuario usuario);

    // Búsquedas por estado de verificación
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.usuario.activo = :activo")
    List<VendedorDetalles> findByUsuarioActivo(@Param("activo") Boolean activo);

    // Vendedores activos en el sistema
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.usuario.activo = true")
    List<VendedorDetalles> findVendedoresActivos();

    // Búsquedas por verificación
    List<VendedorDetalles> findByVerificadoTrue();

    List<VendedorDetalles> findByVerificadoFalse();

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.verificado = true AND vd.usuario.activo = true")
    List<VendedorDetalles> findVendedoresVerificadosActivos();

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.verificado = false AND vd.usuario.activo = true")
    List<VendedorDetalles> findVendedoresNoVerificadosActivos();

    // Búsquedas por fecha de verificación
    List<VendedorDetalles> findByFechaVerificacionBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.fechaVerificacion >= :fecha ORDER BY vd.fechaVerificacion DESC")
    List<VendedorDetalles> findVerificadosDesde(@Param("fecha") LocalDateTime fecha);

    // Vendedores verificados recientemente
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.verificado = true AND vd.fechaVerificacion >= :fecha ORDER BY vd.fechaVerificacion DESC")
    List<VendedorDetalles> findVerificacionesRecientes(@Param("fecha") LocalDateTime fecha);

    // Búsquedas por especialidad
    List<VendedorDetalles> findByEspecialidad(String especialidad);

    List<VendedorDetalles> findByEspecialidadContainingIgnoreCase(String especialidad);

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.especialidad = :especialidad AND vd.usuario.activo = true")
    List<VendedorDetalles> findByEspecialidadYActivo(@Param("especialidad") String especialidad);

    // Especialidades específicas del sistema
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.especialidad = 'Electrónica' AND vd.verificado = true AND vd.usuario.activo = true")
    List<VendedorDetalles> findVendedoresElectronica();

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.especialidad = 'Ropa' AND vd.verificado = true AND vd.usuario.activo = true")
    List<VendedorDetalles> findVendedoresRopa();

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.especialidad = 'Hogar' AND vd.verificado = true AND vd.usuario.activo = true")
    List<VendedorDetalles> findVendedoresHogar();

    // Búsquedas por información fiscal
    List<VendedorDetalles> findByRfc(String rfc);

    List<VendedorDetalles> findByNumRegistroFiscal(String numRegistroFiscal);

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.rfc IS NOT NULL AND vd.rfc != '' AND vd.usuario.activo = true")
    List<VendedorDetalles> findConRfcCompleto();

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.numRegistroFiscal IS NOT NULL AND vd.numRegistroFiscal != '' AND vd.usuario.activo = true")
    List<VendedorDetalles> findConRegistroFiscalCompleto();

    // Búsquedas por documentación
    List<VendedorDetalles> findByTipoDocumento(String tipoDocumento);

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.documentoComercial IS NOT NULL AND vd.documentoComercial != '' AND vd.usuario.activo = true")
    List<VendedorDetalles> findConDocumentacionCompleta();

    @Query("SELECT vd FROM VendedorDetalles vd WHERE (vd.documentoComercial IS NULL OR vd.documentoComercial = '') AND vd.usuario.activo = true")
    List<VendedorDetalles> findSinDocumentacion();

    // Búsquedas por información bancaria
    List<VendedorDetalles> findByBanco(String banco);

    List<VendedorDetalles> findByTipoCuenta(String tipoCuenta);

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.banco IS NOT NULL AND vd.numeroCuenta IS NOT NULL AND vd.usuario.activo = true")
    List<VendedorDetalles> findConInformacionBancariaCompleta();

    @Query("SELECT vd FROM VendedorDetalles vd WHERE (vd.banco IS NULL OR vd.numeroCuenta IS NULL) AND vd.usuario.activo = true")
    List<VendedorDetalles> findSinInformacionBancaria();

    // Búsquedas por ubicación
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.direccionComercial LIKE %:ciudad% AND vd.usuario.activo = true")
    List<VendedorDetalles> findPorCiudad(@Param("ciudad") String ciudad);

    List<VendedorDetalles> findByDireccionComercialContainingIgnoreCase(String direccion);

    // Búsquedas por estado de información completa
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.numRegistroFiscal IS NOT NULL AND vd.especialidad IS NOT NULL AND vd.direccionComercial IS NOT NULL AND vd.usuario.activo = true")
    List<VendedorDetalles> findConInformacionCompleta();

    @Query("SELECT vd FROM VendedorDetalles vd WHERE (vd.numRegistroFiscal IS NULL OR vd.especialidad IS NULL OR vd.direccionComercial IS NULL) AND vd.usuario.activo = true")
    List<VendedorDetalles> findConInformacionIncompleta();

    // Vendedores por rol específico del sistema
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.usuario.rol.nombre = 'VENDEDOR' AND vd.usuario.activo = true")
    List<VendedorDetalles> findVendedoresByRolSistema();

    // Vendedores con productos
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.usuario.activo = true AND EXISTS (SELECT p FROM Producto p WHERE p.vendedor.usuarioId = vd.usuario.usuarioId)")
    List<VendedorDetalles> findVendedoresConProductos();

    // Vendedores sin productos
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.usuario.activo = true AND NOT EXISTS (SELECT p FROM Producto p WHERE p.vendedor.usuarioId = vd.usuario.usuarioId)")
    List<VendedorDetalles> findVendedoresSinProductos();

    // Vendedores con productos activos
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.usuario.activo = true AND EXISTS (SELECT p FROM Producto p WHERE p.vendedor.usuarioId = vd.usuario.usuarioId AND p.activo = true)")
    List<VendedorDetalles> findVendedoresConProductosActivos();

    // Estadísticas de vendedores
    @Query("SELECT COUNT(vd) FROM VendedorDetalles vd WHERE vd.usuario.activo = true")
    long countVendedoresActivos();

    @Query("SELECT COUNT(vd) FROM VendedorDetalles vd WHERE vd.verificado = true AND vd.usuario.activo = true")
    long countVendedoresVerificados();

    @Query("SELECT COUNT(vd) FROM VendedorDetalles vd WHERE vd.verificado = false AND vd.usuario.activo = true")
    long countVendedoresNoVerificados();

    // Estadísticas por especialidad
    @Query("SELECT vd.especialidad, COUNT(vd) FROM VendedorDetalles vd WHERE vd.usuario.activo = true GROUP BY vd.especialidad ORDER BY COUNT(vd) DESC")
    List<Object[]> countVendedoresPorEspecialidad();

    @Query("SELECT vd.especialidad, COUNT(vd) FROM VendedorDetalles vd WHERE vd.verificado = true AND vd.usuario.activo = true GROUP BY vd.especialidad ORDER BY COUNT(vd) DESC")
    List<Object[]> countVendedoresVerificadosPorEspecialidad();

    // Estadísticas por tipo de cuenta bancaria
    @Query("SELECT vd.tipoCuenta, COUNT(vd) FROM VendedorDetalles vd WHERE vd.tipoCuenta IS NOT NULL AND vd.usuario.activo = true GROUP BY vd.tipoCuenta ORDER BY COUNT(vd) DESC")
    List<Object[]> countVendedoresPorTipoCuenta();

    // Estadísticas por banco
    @Query("SELECT vd.banco, COUNT(vd) FROM VendedorDetalles vd WHERE vd.banco IS NOT NULL AND vd.usuario.activo = true GROUP BY vd.banco ORDER BY COUNT(vd) DESC")
    List<Object[]> countVendedoresPorBanco();

    // Vendedores que necesitan verificación
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.verificado = false AND vd.numRegistroFiscal IS NOT NULL AND vd.especialidad IS NOT NULL AND vd.direccionComercial IS NOT NULL AND vd.usuario.activo = true")
    List<VendedorDetalles> findPendientesVerificacion();

    // Vendedores que requieren documentación adicional
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.usuario.activo = true AND (vd.documentoComercial IS NULL OR vd.banco IS NULL OR vd.numeroCuenta IS NULL)")
    List<VendedorDetalles> findQueRequierenDocumentacion();

    // Análisis de completitud de perfiles
    @Query("SELECT COUNT(vd) FROM VendedorDetalles vd WHERE vd.numRegistroFiscal IS NOT NULL AND vd.especialidad IS NOT NULL AND vd.direccionComercial IS NOT NULL AND vd.banco IS NOT NULL AND vd.numeroCuenta IS NOT NULL AND vd.usuario.activo = true")
    long countPerfilesCompletos();

    @Query("SELECT (COUNT(vd) * 100.0 / (SELECT COUNT(vd2) FROM VendedorDetalles vd2 WHERE vd2.usuario.activo = true)) FROM VendedorDetalles vd WHERE vd.verificado = true AND vd.usuario.activo = true")
    Double findPorcentajeVerificados();

    // Búsquedas para reportes
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.verificado = true AND vd.fechaVerificacion BETWEEN :inicio AND :fin ORDER BY vd.fechaVerificacion DESC")
    List<VendedorDetalles> findVerificadosEntreFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Vendedores por tipo de documento
    @Query("SELECT vd.tipoDocumento, COUNT(vd) FROM VendedorDetalles vd WHERE vd.tipoDocumento IS NOT NULL AND vd.usuario.activo = true GROUP BY vd.tipoDocumento ORDER BY COUNT(vd) DESC")
    List<Object[]> countVendedoresPorTipoDocumento();

    // Top vendedores por productos
    @Query("SELECT vd, COUNT(p) as totalProductos FROM VendedorDetalles vd LEFT JOIN Producto p ON p.vendedor.usuarioId = vd.usuario.usuarioId WHERE vd.usuario.activo = true GROUP BY vd.usuarioId ORDER BY totalProductos DESC")
    List<Object[]> findTopVendedoresPorProductos();

    // Vendedores con mayor diversidad de productos
    @Query("SELECT vd, COUNT(DISTINCT p.categoria.categoriaId) as categorias FROM VendedorDetalles vd LEFT JOIN Producto p ON p.vendedor.usuarioId = vd.usuario.usuarioId WHERE vd.usuario.activo = true AND p.activo = true GROUP BY vd.usuarioId ORDER BY categorias DESC")
    List<Object[]> findVendedoresPorDiversidadCategorias();

    // Búsquedas para auditoría
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.verificado = true AND vd.fechaVerificacion IS NULL AND vd.usuario.activo = true")
    List<VendedorDetalles> findVerificadosSinFecha();

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.verificado = false AND vd.fechaVerificacion IS NOT NULL AND vd.usuario.activo = true")
    List<VendedorDetalles> findNoVerificadosConFecha();

    // Vendedores inactivos recientemente
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.usuario.activo = false AND vd.usuario.updatedAt >= :fecha")
    List<VendedorDetalles> findDesactivadosDesde(@Param("fecha") LocalDateTime fecha);

    // Búsquedas por validación RFC
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.rfc IS NOT NULL AND LENGTH(vd.rfc) = 13 AND vd.usuario.activo = true")
    List<VendedorDetalles> findConRfcValido();

    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.rfc IS NOT NULL AND LENGTH(vd.rfc) != 13 AND vd.usuario.activo = true")
    List<VendedorDetalles> findConRfcInvalido();

    // Búsquedas para notificaciones
    @Query("SELECT vd FROM VendedorDetalles vd WHERE vd.verificado = false AND vd.usuario.createdAt < :fecha AND vd.usuario.activo = true")
    List<VendedorDetalles> findNoVerificadosAntiguos(@Param("fecha") LocalDateTime fecha);

    // Análisis geográfico
    @Query("SELECT SUBSTRING(vd.direccionComercial, LOCATE(',', vd.direccionComercial) + 1) as region, COUNT(vd) FROM VendedorDetalles vd WHERE vd.direccionComercial IS NOT NULL AND vd.usuario.activo = true GROUP BY region ORDER BY COUNT(vd) DESC")
    List<Object[]> findDistribucionGeografica();
}
