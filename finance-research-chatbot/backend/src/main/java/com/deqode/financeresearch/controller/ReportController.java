package com.deqode.financeresearch.controller;

import com.deqode.financeresearch.entity.Thread;
import com.deqode.financeresearch.entity.User;
import com.deqode.financeresearch.repository.ThreadRepository;
import com.deqode.financeresearch.repository.UserRepository;
import com.deqode.financeresearch.security.JwtTokenProvider;
import com.deqode.financeresearch.service.ReportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {
    
    @Autowired
    private ReportExportService reportExportService;
    
    @Autowired
    private ThreadRepository threadRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @GetMapping("/thread/{threadId}/markdown")
    public ResponseEntity<String> exportMarkdownReport(
            @PathVariable Long threadId,
            @RequestHeader("Authorization") String authHeader) {
        
        Thread thread = getThreadForUser(threadId, authHeader);
        String markdownReport = reportExportService.generateMarkdownReport(thread);
        
        String filename = "finance-research-report-" + threadId + ".md";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(markdownReport);
    }
    
    @GetMapping("/thread/{threadId}/html")
    public ResponseEntity<String> exportHtmlReport(
            @PathVariable Long threadId,
            @RequestHeader("Authorization") String authHeader) {
        
        Thread thread = getThreadForUser(threadId, authHeader);
        String htmlReport = reportExportService.generateHtmlReport(thread);
        
        String filename = "finance-research-report-" + threadId + ".html";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_HTML)
                .body(htmlReport);
    }
    
    @GetMapping("/thread/{threadId}/preview")
    public ResponseEntity<ReportPreview> getReportPreview(
            @PathVariable Long threadId,
            @RequestHeader("Authorization") String authHeader) {
        
        Thread thread = getThreadForUser(threadId, authHeader);
        String markdownReport = reportExportService.generateMarkdownReport(thread);
        
        ReportPreview preview = new ReportPreview();
        preview.threadId = threadId;
        preview.title = thread.getTitle() != null ? thread.getTitle() : "Untitled Research";
        preview.markdownContent = markdownReport;
        preview.wordCount = markdownReport.split("\\s+").length;
        preview.generatedAt = java.time.LocalDateTime.now();
        
        return ResponseEntity.ok(preview);
    }
    
    private Thread getThreadForUser(Long threadId, String authHeader) {
        // Extract user from JWT
        String token = authHeader.substring(7);
        String email = jwtTokenProvider.getEmailFromToken(token);
        
        // Find user
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify thread belongs to user
        return threadRepository.findByIdAndUser(threadId, user)
            .orElseThrow(() -> new RuntimeException("Thread not found or access denied"));
    }
    
    public static class ReportPreview {
        public Long threadId;
        public String title;
        public String markdownContent;
        public int wordCount;
        public java.time.LocalDateTime generatedAt;
    }
}