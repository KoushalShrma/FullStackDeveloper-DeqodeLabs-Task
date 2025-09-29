package com.deqode.financeresearch.controller;

import com.deqode.financeresearch.dto.*;
import com.deqode.financeresearch.security.CustomUserDetailsService;
import com.deqode.financeresearch.service.ThreadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/threads")
public class ThreadController {
    
    @Autowired
    private ThreadService threadService;
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.deqode.financeresearch.security.UserPrincipal) {
            com.deqode.financeresearch.security.UserPrincipal userPrincipal = 
                (com.deqode.financeresearch.security.UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId();
        }
        throw new RuntimeException("User not authenticated");
    }
    
    @PostMapping
    public ResponseEntity<?> createThread(@Valid @RequestBody CreateThreadRequest request) {
        try {
            Long userId = getCurrentUserId();
            ThreadDto thread = threadService.createThread(userId, request);
            return ResponseEntity.ok(thread);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getUserThreads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = getCurrentUserId();
            List<ThreadDto> threads = threadService.getUserThreads(userId, page, size);
            return ResponseEntity.ok(threads);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{threadId}")
    public ResponseEntity<?> getThread(@PathVariable Long threadId) {
        try {
            Long userId = getCurrentUserId();
            ThreadDto thread = threadService.getThread(userId, threadId);
            return ResponseEntity.ok(thread);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{threadId}/messages")
    public ResponseEntity<?> getThreadMessages(
            @PathVariable Long threadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Long userId = getCurrentUserId();
            List<MessageDto> messages = threadService.getThreadMessages(userId, threadId, page, size);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{threadId}/messages")
    public ResponseEntity<?> sendMessage(
            @PathVariable Long threadId,
            @Valid @RequestBody SendMessageRequest request) {
        try {
            Long userId = getCurrentUserId();
            MessageDto message = threadService.addMessage(userId, threadId, request);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{threadId}/title")
    public ResponseEntity<?> updateThreadTitle(
            @PathVariable Long threadId,
            @RequestBody Map<String, String> request) {
        try {
            Long userId = getCurrentUserId();
            String title = request.get("title");
            ThreadDto thread = threadService.updateThreadTitle(userId, threadId, title);
            return ResponseEntity.ok(thread);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{threadId}/archive")
    public ResponseEntity<?> archiveThread(@PathVariable Long threadId) {
        try {
            Long userId = getCurrentUserId();
            threadService.archiveThread(userId, threadId);
            return ResponseEntity.ok(Map.of("message", "Thread archived successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{threadId}")
    public ResponseEntity<?> deleteThread(@PathVariable Long threadId) {
        try {
            Long userId = getCurrentUserId();
            threadService.deleteThread(userId, threadId);
            return ResponseEntity.ok(Map.of("message", "Thread deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<?> getUserThreadCount() {
        try {
            Long userId = getCurrentUserId();
            long count = threadService.getUserThreadCount(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}