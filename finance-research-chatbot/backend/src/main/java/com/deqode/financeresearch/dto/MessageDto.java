package com.deqode.financeresearch.dto;

import com.deqode.financeresearch.entity.Message;

import java.time.LocalDateTime;

public class MessageDto {
    
    private Long id;
    private String role;
    private String content;
    private String reasoningTrace;
    private LocalDateTime createdAt;
    private Integer tokensUsed;
    private Integer processingTimeMs;
    
    // Constructors
    public MessageDto() {}
    
    public MessageDto(Message message) {
        this.id = message.getId();
        this.role = message.getRole().name();
        this.content = message.getContent();
        this.reasoningTrace = message.getReasoningTrace();
        this.createdAt = message.getCreatedAt();
        this.tokensUsed = message.getTokensUsed();
        this.processingTimeMs = message.getProcessingTimeMs();
    }
    
    // Static factory method
    public static MessageDto fromEntity(Message message) {
        return new MessageDto(message);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getReasoningTrace() { return reasoningTrace; }
    public void setReasoningTrace(String reasoningTrace) { this.reasoningTrace = reasoningTrace; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Integer getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(Integer tokensUsed) { this.tokensUsed = tokensUsed; }
    
    public Integer getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Integer processingTimeMs) { this.processingTimeMs = processingTimeMs; }
}