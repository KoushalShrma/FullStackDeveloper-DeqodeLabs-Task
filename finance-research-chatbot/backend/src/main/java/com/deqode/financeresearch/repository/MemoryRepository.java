package com.deqode.financeresearch.repository;

import com.deqode.financeresearch.entity.Memory;
import com.deqode.financeresearch.entity.User;
import com.deqode.financeresearch.entity.Thread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
    
    @Query("SELECT m FROM Memory m WHERE m.user = :user ORDER BY m.importanceScore DESC, m.accessedAt DESC")
    Page<Memory> findMemoriesByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT m FROM Memory m WHERE m.user = :user AND m.memoryType = :memoryType ORDER BY m.importanceScore DESC")
    List<Memory> findMemoriesByUserAndType(@Param("user") User user, @Param("memoryType") Memory.MemoryType memoryType);
    
    @Query("SELECT m FROM Memory m WHERE m.thread = :thread ORDER BY m.createdAt DESC")
    List<Memory> findMemoriesByThread(@Param("thread") Thread thread);
    
    @Query("SELECT m FROM Memory m WHERE m.user = :user AND m.importanceScore >= :minImportance ORDER BY m.importanceScore DESC, m.accessedAt DESC")
    List<Memory> findImportantMemories(@Param("user") User user, @Param("minImportance") BigDecimal minImportance);
    
    @Query("SELECT m FROM Memory m WHERE m.user = :user AND (LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(m.contextSummary) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Memory> searchMemoriesByContent(@Param("user") User user, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(m) FROM Memory m WHERE m.user = :user")
    long countMemoriesByUser(@Param("user") User user);
}