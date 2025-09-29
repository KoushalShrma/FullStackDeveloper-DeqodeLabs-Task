package com.deqode.financeresearch.repository;

import com.deqode.financeresearch.entity.Session;
import com.deqode.financeresearch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    Optional<Session> findBySessionToken(String sessionToken);
    
    @Query("SELECT s FROM Session s WHERE s.sessionToken = :sessionToken AND s.isRevoked = false AND s.expiresAt > :now")
    Optional<Session> findValidSessionByToken(@Param("sessionToken") String sessionToken, @Param("now") LocalDateTime now);
    
    @Query("SELECT s FROM Session s WHERE s.user = :user AND s.isRevoked = false")
    List<Session> findActiveSessionsByUser(@Param("user") User user);
    
    @Modifying
    @Query("UPDATE Session s SET s.isRevoked = true WHERE s.user = :user")
    void revokeAllUserSessions(@Param("user") User user);
    
    @Modifying
    @Query("UPDATE Session s SET s.isRevoked = true WHERE s.expiresAt < :now")
    void revokeExpiredSessions(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM Session s WHERE s.expiresAt < :cutoffDate")
    void deleteOldSessions(@Param("cutoffDate") LocalDateTime cutoffDate);
}