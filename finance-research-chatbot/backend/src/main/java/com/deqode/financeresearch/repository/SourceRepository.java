package com.deqode.financeresearch.repository;

import com.deqode.financeresearch.entity.Source;
import com.deqode.financeresearch.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {
    
    @Query("SELECT s FROM Source s WHERE s.thread = :thread AND s.isRelevant = true ORDER BY s.relevanceScore DESC, s.crawledAt DESC")
    List<Source> findRelevantSourcesByThread(@Param("thread") Thread thread);
    
    @Query("SELECT s FROM Source s WHERE s.thread = :thread ORDER BY s.crawledAt DESC")
    List<Source> findAllSourcesByThread(@Param("thread") Thread thread);
    
    @Query("SELECT s FROM Source s WHERE s.contentHash = :contentHash")
    Optional<Source> findByContentHash(@Param("contentHash") String contentHash);
    
    @Query("SELECT s FROM Source s WHERE s.url = :url AND s.thread = :thread")
    Optional<Source> findByUrlAndThread(@Param("url") String url, @Param("thread") Thread thread);
    
    @Query("SELECT DISTINCT s.domain FROM Source s WHERE s.thread = :thread")
    List<String> findDistinctDomainsByThread(@Param("thread") Thread thread);
    
    @Query("SELECT COUNT(s) FROM Source s WHERE s.thread = :thread AND s.isRelevant = true")
    long countRelevantSourcesByThread(@Param("thread") Thread thread);
}