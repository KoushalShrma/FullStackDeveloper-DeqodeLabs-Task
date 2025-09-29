package com.deqode.financeresearch.dto;

import jakarta.validation.constraints.NotBlank;

public class SendMessageRequest {
    
    @NotBlank(message = "Message content is required")
    private String content;
    
    private Boolean showThinking = false;
    
    // Constructors
    public SendMessageRequest() {}
    
    public SendMessageRequest(String content, Boolean showThinking) {
        this.content = content;
        this.showThinking = showThinking;
    }
    
    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Boolean getShowThinking() { return showThinking; }
    public void setShowThinking(Boolean showThinking) { this.showThinking = showThinking; }
}