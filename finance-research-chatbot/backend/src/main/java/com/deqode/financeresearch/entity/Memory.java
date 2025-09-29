package com.deqode.financeresearch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "memories")
public class Memory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id")
    private Thread thread;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "memory_type", nullable = false)
    private MemoryType memoryType;
    
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Size(max = 1000)
    @Column(name = "context_summary")
    private String contextSummary;
    
    @Column(name = "importance_score", precision = 3, scale = 2)
    private BigDecimal importanceScore = BigDecimal.valueOf(0.5);
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "accessed_at")
    private LocalDateTime accessedAt;
    
    @Column(name = "access_count", nullable = false)
    private Integer accessCount = 0;
    
    // Constructors
    public Memory() {}
    
    public Memory(User user, MemoryType memoryType, String content) {
        this.user = user;
        this.memoryType = memoryType;
        this.content = content;
    }
    
    public Memory(User user, Thread thread, MemoryType memoryType, String content, String contextSummary) {
        this.user = user;
        this.thread = thread;
        this.memoryType = memoryType;
        this.content = content;
        this.contextSummary = contextSummary;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Thread getThread() { return thread; }
    public void setThread(Thread thread) { this.thread = thread; }
    
    public MemoryType getMemoryType() { return memoryType; }
    public void setMemoryType(MemoryType memoryType) { this.memoryType = memoryType; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getContextSummary() { return contextSummary; }
    public void setContextSummary(String contextSummary) { this.contextSummary = contextSummary; }
    
    public BigDecimal getImportanceScore() { return importanceScore; }
    public void setImportanceScore(BigDecimal importanceScore) { this.importanceScore = importanceScore; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getAccessedAt() { return accessedAt; }
    public void setAccessedAt(LocalDateTime accessedAt) { this.accessedAt = accessedAt; }
    
    public Integer getAccessCount() { return accessCount; }
    public void setAccessCount(Integer accessCount) { this.accessCount = accessCount; }
    
    public void incrementAccessCount() {
        this.accessCount++;
    }
    
    public enum MemoryType {
        EPISODIC,    // Specific events and experiences
        SEMANTIC,    // General knowledge and facts
        PROCEDURAL   // How-to knowledge and procedures
    }
}