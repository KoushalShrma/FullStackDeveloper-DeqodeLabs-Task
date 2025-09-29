package com.deqode.financeresearch.service;

import com.deqode.financeresearch.dto.*;
import com.deqode.financeresearch.entity.*;
import com.deqode.financeresearch.repository.MessageRepository;
import com.deqode.financeresearch.repository.ThreadRepository;
import com.deqode.financeresearch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ThreadService {
    
    private static final Logger logger = LoggerFactory.getLogger(ThreadService.class);
    
    @Autowired
    private ThreadRepository threadRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GroqService groqService;
    
    @Autowired
    private ResearchService researchService;
    
    public ThreadDto createThread(Long userId, CreateThreadRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.deqode.financeresearch.entity.Thread thread = new com.deqode.financeresearch.entity.Thread();
        thread.setUser(user);
        thread.setTitle(request.getTitle());
        
        com.deqode.financeresearch.entity.Thread savedThread = threadRepository.save(thread);
        
        // Add initial message if provided
        if (request.getInitialMessage() != null && !request.getInitialMessage().trim().isEmpty()) {
            Message initialMessage = new Message(savedThread, Message.MessageRole.USER, request.getInitialMessage());
            messageRepository.save(initialMessage);
            savedThread.incrementMessageCount();
            threadRepository.save(savedThread);
        }
        
        logger.info("Created new thread {} for user {}", savedThread.getId(), user.getEmail());
        return ThreadDto.fromEntity(savedThread);
    }
    
    public List<ThreadDto> getUserThreads(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<com.deqode.financeresearch.entity.Thread> threads = threadRepository.findActiveThreadsByUser(user, pageable);
        
        return threads.getContent().stream()
            .map(ThreadDto::fromEntity)
            .collect(Collectors.toList());
    }
    
    public ThreadDto getThread(Long userId, Long threadId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.deqode.financeresearch.entity.Thread thread = threadRepository.findByIdAndUser(threadId, user)
            .orElseThrow(() -> new RuntimeException("Thread not found"));
        
        return ThreadDto.fromEntity(thread);
    }
    
    public List<MessageDto> getThreadMessages(Long userId, Long threadId, int page, int size) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.deqode.financeresearch.entity.Thread thread = threadRepository.findByIdAndUser(threadId, user)
            .orElseThrow(() -> new RuntimeException("Thread not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Message> messages = messageRepository.findMessagesByThread(thread, pageable);
        
        return messages.getContent().stream()
            .map(MessageDto::fromEntity)
            .collect(Collectors.toList());
    }
    
    public MessageDto addMessage(Long userId, Long threadId, SendMessageRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.deqode.financeresearch.entity.Thread thread = threadRepository.findByIdAndUser(threadId, user)
            .orElseThrow(() -> new RuntimeException("Thread not found"));
        
        // Create user message
        Message userMessage = new Message(thread, Message.MessageRole.USER, request.getContent());
        Message savedUserMessage = messageRepository.save(userMessage);
        
        // Update thread message count and timestamp
        thread.incrementMessageCount();
        threadRepository.save(thread);
        
        logger.info("Added message to thread {} for user {}", threadId, user.getEmail());
        
        // Generate AI response asynchronously
        generateAIResponse(thread, request.getContent());
        
        return MessageDto.fromEntity(savedUserMessage);
    }
    
    public MessageDto addMessageWithStreaming(Long userId, Long threadId, SendMessageRequest request, 
                                            String connectionId, StreamCallback streamCallback) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.deqode.financeresearch.entity.Thread thread = threadRepository.findByIdAndUser(threadId, user)
            .orElseThrow(() -> new RuntimeException("Thread not found"));
        
        // Create user message
        Message userMessage = new Message(thread, Message.MessageRole.USER, request.getContent());
        Message savedUserMessage = messageRepository.save(userMessage);
        
        // Update thread message count and timestamp
        thread.incrementMessageCount();
        threadRepository.save(thread);
        
        logger.info("Added message to thread {} for user {} with streaming", threadId, user.getEmail());
        
        // Generate AI response with streaming
        generateAIResponseWithStreaming(thread, request.getContent(), connectionId, streamCallback);
        
        return MessageDto.fromEntity(savedUserMessage);
    }
    
    private void generateAIResponse(com.deqode.financeresearch.entity.Thread thread, String userMessage) {
        try {
            // Get conversation history for context
            List<GroqService.ChatMessage> conversationHistory = buildConversationHistory(thread);
            
            // Check if this is a finance research query
            if (isFinanceResearchQuery(userMessage)) {
                // Use deep research service for finance queries
                researchService.conductFinanceResearch(userMessage, thread, conversationHistory)
                    .subscribe(
                        researchResult -> {
                            // Save AI response with research context
                            Message aiMessage = new Message(thread, Message.MessageRole.ASSISTANT, researchResult.aiResponse);
                            messageRepository.save(aiMessage);
                            
                            // Update thread message count
                            thread.incrementMessageCount();
                            threadRepository.save(thread);
                            
                            logger.info("Generated research-based AI response for thread {}", thread.getId());
                        },
                        error -> {
                            logger.error("Error generating research response for thread " + thread.getId(), error);
                            // Fallback to regular AI response
                            generateRegularAIResponse(thread, userMessage, conversationHistory);
                        }
                    );
            } else {
                logger.info("Using regular AI response for non-finance query...");
                // Use regular AI response for non-finance queries
                generateRegularAIResponse(thread, userMessage, conversationHistory);
            }
        } catch (Exception e) {
            logger.error("Error generating AI response for thread " + thread.getId(), e);
        }
    }
    
    private void generateRegularAIResponse(com.deqode.financeresearch.entity.Thread thread, String userMessage, List<GroqService.ChatMessage> conversationHistory) {
        // Generate response using Groq
        groqService.generateFinanceResponse(userMessage, conversationHistory)
            .subscribe(
                aiResponse -> {
                    // Save AI response as a new message
                    Message aiMessage = new Message(thread, Message.MessageRole.ASSISTANT, aiResponse);
                    messageRepository.save(aiMessage);
                    
                    // Update thread message count
                    thread.incrementMessageCount();
                    threadRepository.save(thread);
                    
                    logger.info("Generated AI response for thread {}", thread.getId());
                },
                error -> {
                    logger.error("Failed to generate AI response for thread {}", thread.getId(), error);
                    
                    // Save error message
                    String errorMessage = "I apologize, but I'm having trouble generating a response right now. Please try again.";
                    Message aiMessage = new Message(thread, Message.MessageRole.ASSISTANT, errorMessage);
                    messageRepository.save(aiMessage);
                    
                    thread.incrementMessageCount();
                    threadRepository.save(thread);
                }
            );
    }
    
    @Async
    private void generateAIResponseWithStreaming(com.deqode.financeresearch.entity.Thread thread, String userMessage, 
                                               String connectionId, StreamCallback streamCallback) {
        try {
            // Send initial status
            streamCallback.sendChunk(connectionId, "started", "{\"status\":\"started\",\"message\":\"Processing your request...\"}");
            
            // Get conversation history for context
            List<GroqService.ChatMessage> conversationHistory = buildConversationHistory(thread);
            
            // Check if this is a finance research query
            if (isFinanceResearchQuery(userMessage)) {
                // For now, use regular streaming since we need to implement research streaming
                generateRegularAIResponseWithStreaming(thread, userMessage, conversationHistory, connectionId, streamCallback);
            } else {
                // Use regular AI response for non-finance queries with streaming
                generateRegularAIResponseWithStreaming(thread, userMessage, conversationHistory, connectionId, streamCallback);
            }
        } catch (Exception e) {
            logger.error("Error generating streaming AI response for thread " + thread.getId(), e);
            streamCallback.sendChunk(connectionId, "error", 
                "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    private void generateRegularAIResponseWithStreaming(com.deqode.financeresearch.entity.Thread thread, String userMessage, 
                                                      List<GroqService.ChatMessage> conversationHistory, 
                                                      String connectionId, StreamCallback streamCallback) {
        // For now, simulate streaming by generating response normally but sending chunks
        groqService.generateFinanceResponse(userMessage, conversationHistory)
            .subscribe(
                aiResponse -> {
                    // Simulate streaming by breaking response into chunks
                    simulateStreamingResponse(aiResponse, connectionId, streamCallback);
                    
                    // Save AI response as a new message
                    Message aiMessage = new Message(thread, Message.MessageRole.ASSISTANT, aiResponse);
                    messageRepository.save(aiMessage);
                    
                    // Update thread message count
                    thread.incrementMessageCount();
                    threadRepository.save(thread);
                    
                    logger.info("Generated streaming AI response for thread {}", thread.getId());
                },
                error -> {
                    logger.error("Failed to generate streaming AI response for thread {}", thread.getId(), error);
                    
                    // Save error message
                    String errorMessage = "I apologize, but I'm having trouble generating a response right now. Please try again.";
                    Message aiMessage = new Message(thread, Message.MessageRole.ASSISTANT, errorMessage);
                    messageRepository.save(aiMessage);
                    
                    thread.incrementMessageCount();
                    threadRepository.save(thread);
                    
                    // Send error event
                    streamCallback.sendChunk(connectionId, "error", 
                        "{\"status\":\"error\",\"message\":\"" + errorMessage + "\"}");
                }
            );
    }
    
    private void simulateStreamingResponse(String fullResponse, String connectionId, StreamCallback streamCallback) {
        // Break response into word chunks and send them progressively
        String[] words = fullResponse.split(" ");
        StringBuilder currentChunk = new StringBuilder();
        
        try {
            for (int i = 0; i < words.length; i++) {
                currentChunk.append(words[i]);
                if (i < words.length - 1) {
                    currentChunk.append(" ");
                }
                
                // Send chunk every 3-5 words
                if (i % 4 == 0 || i == words.length - 1) {
                    String chunkData = "{\"chunk\":\"" + currentChunk.toString().replace("\"", "\\\"") + "\",\"isComplete\":" + (i == words.length - 1) + "}";
                    streamCallback.sendChunk(connectionId, "chunk", chunkData);
                    
                    if (i != words.length - 1) {
                        currentChunk = new StringBuilder();
                        java.lang.Thread.sleep(100); // Small delay to simulate real streaming
                    }
                }
            }
            
            // Send completion event
            streamCallback.sendChunk(connectionId, "completed", "{\"status\":\"completed\",\"message\":\"Response completed\"}");
            
        } catch (InterruptedException e) {
            logger.error("Error in streaming simulation: ", e);
            java.lang.Thread.currentThread().interrupt();
        }
    }
    
    private boolean isFinanceResearchQuery(String message) {
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("bank") || 
               lowerMessage.contains("stock") || 
               lowerMessage.contains("financial") || 
               lowerMessage.contains("valuation") || 
               lowerMessage.contains("p/e") || 
               lowerMessage.contains("roe") || 
               lowerMessage.contains("revenue") || 
               lowerMessage.contains("profit") || 
               lowerMessage.contains("earnings") || 
               lowerMessage.contains("investment") || 
               lowerMessage.contains("analysis") || 
               lowerMessage.contains("comparison") || 
               lowerMessage.contains("undervalued") || 
               lowerMessage.contains("overvalued") ||
               lowerMessage.contains("quarter") ||
               lowerMessage.contains("performance");
    }
    
    private List<GroqService.ChatMessage> buildConversationHistory(com.deqode.financeresearch.entity.Thread thread) {
        // Get recent messages for context (last 20 messages)
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        Page<Message> recentMessages = messageRepository.findMessagesByThread(thread, pageable);
        
        // Convert to ChatMessage format and reverse order (oldest first)
        List<GroqService.ChatMessage> history = recentMessages.getContent().stream()
            .sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
            .map(msg -> new GroqService.ChatMessage(
                msg.getRole() == Message.MessageRole.USER ? "user" : "assistant",
                msg.getContent()
            ))
            .collect(Collectors.toList());
        
        return history;
    }
    
    public ThreadDto updateThreadTitle(Long userId, Long threadId, String title) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.deqode.financeresearch.entity.Thread thread = threadRepository.findByIdAndUser(threadId, user)
            .orElseThrow(() -> new RuntimeException("Thread not found"));
        
        thread.setTitle(title);
        com.deqode.financeresearch.entity.Thread savedThread = threadRepository.save(thread);
        
        return ThreadDto.fromEntity(savedThread);
    }
    
    public void archiveThread(Long userId, Long threadId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.deqode.financeresearch.entity.Thread thread = threadRepository.findByIdAndUser(threadId, user)
            .orElseThrow(() -> new RuntimeException("Thread not found"));
        
        thread.setIsArchived(true);
        threadRepository.save(thread);
        
        logger.info("Archived thread {} for user {}", threadId, user.getEmail());
    }
    
    public void deleteThread(Long userId, Long threadId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        com.deqode.financeresearch.entity.Thread thread = threadRepository.findByIdAndUser(threadId, user)
            .orElseThrow(() -> new RuntimeException("Thread not found"));
        
        threadRepository.delete(thread);
        
        logger.info("Deleted thread {} for user {}", threadId, user.getEmail());
    }
    
    public long getUserThreadCount(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return threadRepository.countActiveThreadsByUser(user);
    }
}