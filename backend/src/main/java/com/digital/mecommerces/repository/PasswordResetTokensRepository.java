package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.PasswordResetTokens;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokensRepository extends JpaRepository<PasswordResetTokens, Long> {

    // Buscar token por string del token
    Optional<PasswordResetTokens> findByToken(String token);

    // Buscar tokens por usuario
    List<PasswordResetTokens> findByUsuario(Usuario usuario);

    // Buscar tokens por ID de usuario
    List<PasswordResetTokens> findByUsuarioUsuarioId(Long usuarioId);

    // Buscar token válido por usuario (no usado y no expirado)
    @Query("SELECT t FROM PasswordResetTokens t WHERE t.usuario = :usuario AND t.usado = false AND t.fechaExpiracion > :now ORDER BY t.createdat DESC")
    List<PasswordResetTokens> findValidTokensByUsuario(@Param("usuario") Usuario usuario, @Param("now") LocalDateTime now);

    // Buscar token específico válido
    @Query("SELECT t FROM PasswordResetTokens t WHERE t.token = :token AND t.usado = false AND t.fechaExpiracion > :now")
    Optional<PasswordResetTokens> findValidTokenByToken(@Param("token") String token, @Param("now") LocalDateTime now);

    // Verificar si existe token válido para usuario
    @Query("SELECT COUNT(t) > 0 FROM PasswordResetTokens t WHERE t.usuario = :usuario AND t.usado = false AND t.fechaExpiracion > :now")
    boolean existsValidTokenForUsuario(@Param("usuario") Usuario usuario, @Param("now") LocalDateTime now);

    // Buscar tokens expirados
    @Query("SELECT t FROM PasswordResetTokens t WHERE t.fechaExpiracion < :now")
    List<PasswordResetTokens> findExpiredTokens(@Param("now") LocalDateTime now);

    // Buscar tokens no usados
    List<PasswordResetTokens> findByUsadoFalse();

    // Buscar tokens usados
    List<PasswordResetTokens> findByUsadoTrue();

    // Buscar tokens creados en un rango de fechas
    List<PasswordResetTokens> findByCreatedatBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Marcar token como usado
    @Modifying
    @Query("UPDATE PasswordResetTokens t SET t.usado = true WHERE t.token = :token")
    void markTokenAsUsed(@Param("token") String token);

    // Marcar todos los tokens de un usuario como usados
    @Modifying
    @Query("UPDATE PasswordResetTokens t SET t.usado = true WHERE t.usuario = :usuario")
    void markAllUserTokensAsUsed(@Param("usuario") Usuario usuario);

    // Eliminar tokens expirados
    @Modifying
    @Query("DELETE FROM PasswordResetTokens t WHERE t.fechaExpiracion < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    // Eliminar tokens usados más antiguos que cierta fecha
    @Modifying
    @Query("DELETE FROM PasswordResetTokens t WHERE t.usado = true AND t.createdat < :fecha")
    void deleteOldUsedTokens(@Param("fecha") LocalDateTime fecha);

    // Eliminar todos los tokens de un usuario
    void deleteByUsuario(Usuario usuario);

    // Eliminar tokens por ID de usuario
    void deleteByUsuarioUsuarioId(Long usuarioId);

    // Contar tokens por usuario
    long countByUsuario(Usuario usuario);

    // Contar tokens válidos por usuario
    @Query("SELECT COUNT(t) FROM PasswordResetTokens t WHERE t.usuario = :usuario AND t.usado = false AND t.fechaExpiracion > :now")
    long countValidTokensByUsuario(@Param("usuario") Usuario usuario, @Param("now") LocalDateTime now);

    // Obtener último token creado por usuario
    Optional<PasswordResetTokens> findFirstByUsuarioOrderByCreatedatDesc(Usuario usuario);

    // Verificar si token existe
    boolean existsByToken(String token);
}
