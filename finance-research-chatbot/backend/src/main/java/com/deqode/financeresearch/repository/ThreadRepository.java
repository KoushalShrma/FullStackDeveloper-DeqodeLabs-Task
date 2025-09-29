package com.deqode.financeresearch.repository;

import com.deqode.financeresearch.entity.Thread;
import com.deqode.financeresearch.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {
    
    @Query("SELECT t FROM Thread t WHERE t.user = :user AND t.isArchived = false ORDER BY t.updatedAt DESC")
    Page<Thread> findActiveThreadsByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT t FROM Thread t WHERE t.user = :user ORDER BY t.updatedAt DESC")
    Page<Thread> findAllThreadsByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT t FROM Thread t WHERE t.id = :threadId AND t.user = :user")
    Optional<Thread> findByIdAndUser(@Param("threadId") Long threadId, @Param("user") User user);
    
    @Query("SELECT COUNT(t) FROM Thread t WHERE t.user = :user AND t.isArchived = false")
    long countActiveThreadsByUser(@Param("user") User user);
    
    @Query("SELECT t FROM Thread t WHERE t.user = :user AND t.messageCount > :minMessages ORDER BY t.updatedAt DESC")
    List<Thread> findThreadsWithMinimumMessages(@Param("user") User user, @Param("minMessages") int minMessages);
}