package com.rafael.todo.module.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.rafael.todo.module.auth.repository.entity.RefreshTokenEntity;

public interface IRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    List<RefreshTokenEntity> findAllByUserId(UUID userId);

    @Modifying
    @Transactional
    void deleteByUserId(UUID userId);

    @Query("""
        SELECT r FROM RefreshTokenEntity r
        WHERE (:userId IS NULL OR r.userId = :userId)
          AND (:isRevoked IS NULL OR r.isRevoked = :isRevoked)
    """)
    List<RefreshTokenEntity> findAllFiltered(
        @Param("userId") UUID userId,
        @Param("isRevoked") Boolean isRevoked
    );

}
