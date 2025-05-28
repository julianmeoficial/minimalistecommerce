package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.PasswordResetTokens;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokensRepository extends JpaRepository<PasswordResetTokens, Long> {

    // Búsqueda básica por token
    Optional<PasswordResetTokens> findByToken(String token);

    // Verificar existencia de token
    boolean existsByToken(String token);

    // Búsquedas por usuario
    List<PasswordResetTokens> findByUsuarioUsuarioId(Long usuarioId);

    List<PasswordResetTokens> findByUsuario(Usuario usuario);

    // Token activo más reciente por usuario
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.usuario.usuarioId = :usuarioId AND prt.activo = true ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensActivosPorUsuario(@Param("usuarioId") Long usuarioId);

    // Último token de un usuario
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.usuario.usuarioId = :usuarioId ORDER BY prt.createdAt DESC LIMIT 1")
    Optional<PasswordResetTokens> findUltimoTokenDeUsuario(@Param("usuarioId") Long usuarioId);

    // Tokens válidos por usuario (activos y no expirados)
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.usuario.usuarioId = :usuarioId AND prt.activo = true AND prt.usado = false AND prt.fechaExpiracion > :ahora ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensValidosPorUsuario(@Param("usuarioId") Long usuarioId, @Param("ahora") LocalDateTime ahora);

    // Búsquedas por estado
    List<PasswordResetTokens> findByUsadoTrue();

    List<PasswordResetTokens> findByUsadoFalse();

    List<PasswordResetTokens> findByActivoTrue();

    List<PasswordResetTokens> findByActivoFalse();

    // Tokens no usados y activos
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.usado = false AND prt.activo = true ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensNoUsadosYActivos();

    // Búsquedas por fecha de expiración
    List<PasswordResetTokens> findByFechaExpiracionBefore(LocalDateTime fecha);

    List<PasswordResetTokens> findByFechaExpiracionAfter(LocalDateTime fecha);

    // Tokens expirados
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.fechaExpiracion < :ahora ORDER BY prt.fechaExpiracion ASC")
    List<PasswordResetTokens> findTokensExpirados(@Param("ahora") LocalDateTime ahora);

    // Tokens válidos (no expirados, no usados, activos)
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.fechaExpiracion > :ahora AND prt.usado = false AND prt.activo = true ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensValidos(@Param("ahora") LocalDateTime ahora);

    // Tokens que expiran pronto (en los próximos X minutos)
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.fechaExpiracion BETWEEN :ahora AND :limite AND prt.usado = false AND prt.activo = true")
    List<PasswordResetTokens> findTokensQueExpiranPronto(@Param("ahora") LocalDateTime ahora, @Param("limite") LocalDateTime limite);

    // Búsquedas por fecha de creación
    List<PasswordResetTokens> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.createdAt >= :fecha ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensCreadosDesde(@Param("fecha") LocalDateTime fecha);

    // Tokens creados hoy
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE DATE(prt.createdAt) = CURRENT_DATE ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensDeHoy();

    // Búsquedas por IP solicitante
    List<PasswordResetTokens> findByIpSolicitante(String ipSolicitante);

    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.ipSolicitante = :ip AND prt.createdAt >= :desde ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensPorIpDesde(@Param("ip") String ip, @Param("desde") LocalDateTime desde);

    // Detectar posible abuso por IP
    @Query("SELECT COUNT(prt) FROM PasswordResetTokens prt WHERE prt.ipSolicitante = :ip AND prt.createdAt >= :desde")
    long countTokensPorIpDesde(@Param("ip") String ip, @Param("desde") LocalDateTime desde);

    // Búsquedas por intentos
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.intentos >= :maxIntentos AND prt.activo = true")
    List<PasswordResetTokens> findTokensConDemasiadosIntentos(@Param("maxIntentos") Integer maxIntentos);

    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.intentos = 0 AND prt.usado = false AND prt.activo = true")
    List<PasswordResetTokens> findTokensSinIntentos();

    // Búsquedas por tipo de usuario usando roles optimizados
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.usuario.rol.nombre = 'COMPRADOR' ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensDeCompradores();

    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.usuario.rol.nombre = 'VENDEDOR' ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensDeVendedores();

    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.usuario.rol.nombre = 'ADMINISTRADOR' ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensDeAdministradores();

    // Búsquedas para seguridad y auditoría
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.usuario.usuarioId = :usuarioId AND prt.createdAt >= :desde ORDER BY prt.createdAt DESC")
    List<PasswordResetTokens> findTokensPorUsuarioDesde(@Param("usuarioId") Long usuarioId, @Param("desde") LocalDateTime desde);

    // Detectar posible abuso por usuario
    @Query("SELECT COUNT(prt) FROM PasswordResetTokens prt WHERE prt.usuario.usuarioId = :usuarioId AND prt.createdAt >= :desde")
    long countTokensPorUsuarioDesde(@Param("usuarioId") Long usuarioId, @Param("desde") LocalDateTime desde);

    // Tokens por usuario en las últimas 24 horas
    @Query("SELECT COUNT(prt) FROM PasswordResetTokens prt WHERE prt.usuario.usuarioId = :usuarioId AND prt.createdAt >= :ayer")
    long countTokensUsuarioUltimas24Horas(@Param("usuarioId") Long usuarioId, @Param("ayer") LocalDateTime ayer);

    // Estadísticas de tokens
    @Query("SELECT COUNT(prt) FROM PasswordResetTokens prt WHERE prt.usado = true")
    long countTokensUsados();

    @Query("SELECT COUNT(prt) FROM PasswordResetTokens prt WHERE prt.fechaExpiracion < :ahora AND prt.usado = false")
    long countTokensExpiradosNoUsados(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT AVG(prt.intentos) FROM PasswordResetTokens prt WHERE prt.usado = true")
    Double findPromedioIntentosPorToken();

    // Tokens para limpieza automática
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE (prt.usado = true OR prt.fechaExpiracion < :fecha) AND prt.createdAt < :fechaLimite")
    List<PasswordResetTokens> findTokensParaLimpiar(@Param("fecha") LocalDateTime fecha, @Param("fechaLimite") LocalDateTime fechaLimite);

    // Tokens antiguos sin usar (posibles tokens perdidos)
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.usado = false AND prt.activo = true AND prt.createdAt < :fechaAntigua")
    List<PasswordResetTokens> findTokensAntiguosSinUsar(@Param("fechaAntigua") LocalDateTime fechaAntigua);

    // Verificar token específico con validaciones
    @Query("SELECT prt FROM PasswordResetTokens prt WHERE prt.token = :token AND prt.usado = false AND prt.activo = true AND prt.fechaExpiracion > :ahora")
    Optional<PasswordResetTokens> findTokenValidoParaUsar(@Param("token") String token, @Param("ahora") LocalDateTime ahora);

    // Desactivar tokens antiguos de un usuario
    @Query("UPDATE PasswordResetTokens prt SET prt.activo = false WHERE prt.usuario.usuarioId = :usuarioId AND prt.activo = true AND prt.usado = false")
    void desactivarTokensActivosDeUsuario(@Param("usuarioId") Long usuarioId);

    // Estadísticas por periodo
    @Query("SELECT DATE(prt.createdAt) as fecha, COUNT(prt) as total FROM PasswordResetTokens prt WHERE prt.createdAt BETWEEN :inicio AND :fin GROUP BY DATE(prt.createdAt) ORDER BY fecha DESC")
    List<Object[]> findEstadisticasPorFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Análisis de patrones de uso
    @Query("SELECT HOUR(prt.createdAt) as hora, COUNT(prt) as total FROM PasswordResetTokens prt GROUP BY HOUR(prt.createdAt) ORDER BY total DESC")
    List<Object[]> findPatronesDeUso();

    // Tokens más utilizados por User Agent
    @Query("SELECT prt.userAgent, COUNT(prt) as total FROM PasswordResetTokens prt WHERE prt.userAgent IS NOT NULL GROUP BY prt.userAgent ORDER BY total DESC")
    List<Object[]> findEstadisticasPorUserAgent();

    // Verificar rate limiting
    @Query("SELECT COUNT(prt) FROM PasswordResetTokens prt WHERE prt.ipSolicitante = :ip AND prt.createdAt >= :desde")
    long countTokensRecientesPorIp(@Param("ip") String ip, @Param("desde") LocalDateTime desde);

    @Query("SELECT COUNT(prt) FROM PasswordResetTokens prt WHERE prt.usuario.email = :email AND prt.createdAt >= :desde")
    long countTokensRecientesPorEmail(@Param("email") String email, @Param("desde") LocalDateTime desde);
}
