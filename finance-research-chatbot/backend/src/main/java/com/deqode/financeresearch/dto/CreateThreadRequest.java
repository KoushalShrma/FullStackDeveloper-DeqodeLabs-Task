package com.deqode.financeresearch.dto;

import jakarta.validation.constraints.Size;

public class CreateThreadRequest {
    
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    private String initialMessage;
    
    // Constructors
    public CreateThreadRequest() {}
    
    public CreateThreadRequest(String title, String initialMessage) {
        this.title = title;
        this.initialMessage = initialMessage;
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getInitialMessage() { return initialMessage; }
    public void setInitialMessage(String initialMessage) { this.initialMessage = initialMessage; }
}