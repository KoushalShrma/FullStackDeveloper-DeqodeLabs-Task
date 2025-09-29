package com.deqode.financeresearch.controller;

import com.deqode.financeresearch.entity.Source;
import com.deqode.financeresearch.entity.Thread;
import com.deqode.financeresearch.entity.User;
import com.deqode.financeresearch.repository.SourceRepository;
import com.deqode.financeresearch.repository.ThreadRepository;
import com.deqode.financeresearch.repository.UserRepository;
import com.deqode.financeresearch.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sources")
@CrossOrigin(origins = "http://localhost:3000")
public class SourceController {
    
    @Autowired
    private SourceRepository sourceRepository;
    
    @Autowired
    private ThreadRepository threadRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @GetMapping("/thread/{threadId}")
    public ResponseEntity<List<SourceDto>> getSourcesByThread(
            @PathVariable Long threadId,
            @RequestHeader("Authorization") String authHeader) {
        
        // Extract user email from JWT
        String token = authHeader.substring(7);
        String email = jwtTokenProvider.getEmailFromToken(token);
        
        // Find user
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify thread belongs to user
        Thread thread = threadRepository.findByIdAndUser(threadId, user)
            .orElseThrow(() -> new RuntimeException("Thread not found"));
        
        // Get sources for the thread
        List<Source> sources = sourceRepository.findRelevantSourcesByThread(thread);
        
        // Convert to DTOs
        List<SourceDto> sourceDtos = sources.stream()
            .map(SourceDto::fromEntity)
            .toList();
        
        return ResponseEntity.ok(sourceDtos);
    }
    
    // DTO for Source response
    public static class SourceDto {
        private Long id;
        private String title;
        private String url;
        private String snippet;
        private String domain;
        private Float relevanceScore;
        
        public SourceDto() {}
        
        public static SourceDto fromEntity(Source source) {
            SourceDto dto = new SourceDto();
            dto.id = source.getId();
            dto.title = source.getTitle();
            dto.url = source.getUrl();
            dto.snippet = source.getSnippet();
            dto.domain = source.getDomain();
            dto.relevanceScore = source.getRelevanceScore() != null ? 
                source.getRelevanceScore().floatValue() : null;
            return dto;
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getSnippet() { return snippet; }
        public void setSnippet(String snippet) { this.snippet = snippet; }
        
        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }
        
        public Float getRelevanceScore() { return relevanceScore; }
        public void setRelevanceScore(Float relevanceScore) { this.relevanceScore = relevanceScore; }
    }
}