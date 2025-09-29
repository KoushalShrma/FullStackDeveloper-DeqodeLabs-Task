package com.deqode.financeresearch.controller;

import com.deqode.financeresearch.dto.SendMessageRequest;
import com.deqode.financeresearch.security.JwtTokenProvider;
import com.deqode.financeresearch.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/stream")
@CrossOrigin(origins = "http://localhost:3000")
public class StreamController {
    
    private static final Logger logger = LoggerFactory.getLogger(StreamController.class);
    
    @Autowired
    private ThreadService threadService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    // Store active SSE connections
    private final Map<String, SseEmitter> activeConnections = new ConcurrentHashMap<>();
    
    @PostMapping("/message/{threadId}")
    public ResponseEntity<?> sendMessageWithStreaming(
            @RequestHeader("Authorization") String authToken,
            @PathVariable Long threadId,
            @RequestBody SendMessageRequest request) {
        
        try {
            String token = authToken.substring(7); // Remove "Bearer " prefix
            String userEmail = jwtTokenProvider.getEmailFromToken(token);
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            
            logger.info("Starting streaming message for user {} in thread {}", userEmail, threadId);
            
            // Start streaming response
            String connectionId = userId + "_" + threadId + "_" + System.currentTimeMillis();
            threadService.addMessageWithStreaming(userId, threadId, request, connectionId, this::sendStreamChunk);
            
            return ResponseEntity.ok(Map.of(
                "message", "Message sent, streaming started",
                "connectionId", connectionId
            ));
            
        } catch (Exception e) {
            logger.error("Error in streaming message: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping(value = "/connect/{connectionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectToStream(@PathVariable String connectionId) {
        logger.info("New SSE connection established: {}", connectionId);
        
        SseEmitter emitter = new SseEmitter(300000L); // 5 minutes timeout
        activeConnections.put(connectionId, emitter);
        
        emitter.onCompletion(() -> {
            logger.info("SSE connection completed: {}", connectionId);
            activeConnections.remove(connectionId);
        });
        
        emitter.onTimeout(() -> {
            logger.info("SSE connection timed out: {}", connectionId);
            activeConnections.remove(connectionId);
        });
        
        emitter.onError(throwable -> {
            logger.error("SSE connection error for {}: ", connectionId, throwable);
            activeConnections.remove(connectionId);
        });
        
        // Send initial connection confirmation
        try {
            emitter.send(SseEmitter.event()
                .name("connected")
                .data("{\"status\":\"connected\",\"connectionId\":\"" + connectionId + "\"}"));
        } catch (IOException e) {
            logger.error("Error sending initial SSE event: ", e);
        }
        
        return emitter;
    }
    
    public void sendStreamChunk(String connectionId, String eventType, String data) {
        SseEmitter emitter = activeConnections.get(connectionId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name(eventType)
                    .data(data));
                    
                if ("completed".equals(eventType) || "error".equals(eventType)) {
                    emitter.complete();
                    activeConnections.remove(connectionId);
                }
            } catch (IOException e) {
                logger.error("Error sending SSE chunk for {}: ", connectionId, e);
                activeConnections.remove(connectionId);
            }
        }
    }
    
    @DeleteMapping("/disconnect/{connectionId}")
    public ResponseEntity<?> disconnectStream(@PathVariable String connectionId) {
        SseEmitter emitter = activeConnections.remove(connectionId);
        if (emitter != null) {
            emitter.complete();
            logger.info("SSE connection manually disconnected: {}", connectionId);
        }
        return ResponseEntity.ok(Map.of("message", "Disconnected"));
    }
}