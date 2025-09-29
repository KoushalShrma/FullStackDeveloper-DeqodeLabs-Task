package com.deqode.financeresearch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sources")
public class Source {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private Thread thread;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;
    
    @NotBlank
    @Size(max = 2048)
    @Column(nullable = false)
    private String url;
    
    @Size(max = 500)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String snippet;
    
    @Size(max = 64)
    @Column(name = "content_hash")
    private String contentHash;
    
    @Size(max = 255)
    private String domain;
    
    @CreationTimestamp
    @Column(name = "crawled_at", nullable = false, updatable = false)
    private LocalDateTime crawledAt;
    
    @Column(name = "is_relevant", nullable = false)
    private Boolean isRelevant = true;
    
    @Column(name = "relevance_score", precision = 3, scale = 2)
    private BigDecimal relevanceScore;
    
    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Citation> citations = new ArrayList<>();
    
    // Constructors
    public Source() {}
    
    public Source(Thread thread, String url, String title, String snippet) {
        this.thread = thread;
        this.url = url;
        this.title = title;
        this.snippet = snippet;
        this.domain = extractDomain(url);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Thread getThread() { return thread; }
    public void setThread(Thread thread) { this.thread = thread; }
    
    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { 
        this.url = url;
        this.domain = extractDomain(url);
    }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }
    
    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }
    
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    
    public LocalDateTime getCrawledAt() { return crawledAt; }
    public void setCrawledAt(LocalDateTime crawledAt) { this.crawledAt = crawledAt; }
    
    public Boolean getIsRelevant() { return isRelevant; }
    public void setIsRelevant(Boolean isRelevant) { this.isRelevant = isRelevant; }
    
    public BigDecimal getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(BigDecimal relevanceScore) { this.relevanceScore = relevanceScore; }
    
    public List<Citation> getCitations() { return citations; }
    public void setCitations(List<Citation> citations) { this.citations = citations; }
    
    private String extractDomain(String url) {
        try {
            return new java.net.URL(url).getHost();
        } catch (Exception e) {
            return null;
        }
    }
}