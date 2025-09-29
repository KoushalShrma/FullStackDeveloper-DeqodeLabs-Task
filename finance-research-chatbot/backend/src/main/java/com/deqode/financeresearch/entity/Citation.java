package com.deqode.financeresearch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "citations")
public class Citation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;
    
    @Size(max = 1000)
    @Column(name = "citation_text")
    private String citationText;
    
    @Column(name = "position_start")
    private Integer positionStart;
    
    @Column(name = "position_end")
    private Integer positionEnd;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public Citation() {}
    
    public Citation(Message message, Source source, String citationText) {
        this.message = message;
        this.source = source;
        this.citationText = citationText;
    }
    
    public Citation(Message message, Source source, String citationText, Integer positionStart, Integer positionEnd) {
        this.message = message;
        this.source = source;
        this.citationText = citationText;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
    
    public Source getSource() { return source; }
    public void setSource(Source source) { this.source = source; }
    
    public String getCitationText() { return citationText; }
    public void setCitationText(String citationText) { this.citationText = citationText; }
    
    public Integer getPositionStart() { return positionStart; }
    public void setPositionStart(Integer positionStart) { this.positionStart = positionStart; }
    
    public Integer getPositionEnd() { return positionEnd; }
    public void setPositionEnd(Integer positionEnd) { this.positionEnd = positionEnd; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}