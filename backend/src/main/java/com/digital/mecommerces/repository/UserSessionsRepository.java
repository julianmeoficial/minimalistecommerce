package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.UserSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionsRepository extends JpaRepository<UserSessions, Long> {

    Optional<UserSessions> findByToken(String token);

    List<UserSessions> findByUsuarioId(Long usuarioId);

    List<UserSessions> findByUsuarioIdAndActiveTrue(Long usuarioId);

    @Query("SELECT us FROM UserSessions us WHERE us.expiresAt < :now")
    List<UserSessions> findExpiredSessions(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSessions us WHERE us.expiresAt < :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    void deleteByUsuarioId(Long usuarioId);

    boolean existsByToken(String token);
}