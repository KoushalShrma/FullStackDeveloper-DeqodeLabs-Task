package com.deqode.financeresearch.dto;

import com.deqode.financeresearch.entity.Thread;

import java.time.LocalDateTime;

public class ThreadDto {
    
    private Long id;
    private String title;
    private Integer messageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isArchived;
    
    // Constructors
    public ThreadDto() {}
    
    public ThreadDto(Thread thread) {
        this.id = thread.getId();
        this.title = thread.getTitle();
        this.messageCount = thread.getMessageCount();
        this.createdAt = thread.getCreatedAt();
        this.updatedAt = thread.getUpdatedAt();
        this.isArchived = thread.getIsArchived();
    }
    
    // Static factory method
    public static ThreadDto fromEntity(Thread thread) {
        return new ThreadDto(thread);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }
}