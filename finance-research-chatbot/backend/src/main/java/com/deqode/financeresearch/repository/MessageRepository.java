package com.deqode.financeresearch.repository;

import com.deqode.financeresearch.entity.Message;
import com.deqode.financeresearch.entity.Thread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("SELECT m FROM Message m WHERE m.thread = :thread ORDER BY m.createdAt ASC")
    Page<Message> findMessagesByThread(@Param("thread") Thread thread, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.thread = :thread ORDER BY m.createdAt DESC")
    List<Message> findRecentMessagesByThread(@Param("thread") Thread thread, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.thread = :thread")
    long countMessagesByThread(@Param("thread") Thread thread);
    
    @Query("SELECT m FROM Message m WHERE m.thread = :thread AND m.role = 'ASSISTANT' ORDER BY m.createdAt DESC")
    List<Message> findAssistantMessagesByThread(@Param("thread") Thread thread, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.thread = :thread AND m.reasoningTrace IS NOT NULL ORDER BY m.createdAt DESC")
    List<Message> findMessagesWithReasoningTrace(@Param("thread") Thread thread);
}