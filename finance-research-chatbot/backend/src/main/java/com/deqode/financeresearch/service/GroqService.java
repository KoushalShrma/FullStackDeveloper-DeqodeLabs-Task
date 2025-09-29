package com.deqode.financeresearch.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GroqService {
    
    private static final Logger logger = LoggerFactory.getLogger(GroqService.class);
    private static final String GROQ_BASE_URL = "https://api.groq.com/openai/v1";
    
    private final WebClient webClient;
    private final String groqApiKey;
    
    public GroqService(@Value("${app.llm.groq.api-key}") String groqApiKey) {
        this.groqApiKey = groqApiKey;
        this.webClient = WebClient.builder()
            .baseUrl(GROQ_BASE_URL)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
        
        logger.info("Groq service initialized with API key: {}", 
            groqApiKey != null && !groqApiKey.isEmpty() ? "***configured***" : "***NOT SET***");
    }
    
    public Mono<String> generateResponse(List<ChatMessage> messages) {
        if (groqApiKey == null || groqApiKey.trim().isEmpty()) {
            logger.warn("Groq API key not configured, returning default response");
            return Mono.just("I apologize, but I'm not properly configured yet. Please set up the Groq API key in the environment variables.");
        }
        
        ChatCompletionRequest request = new ChatCompletionRequest(
            "llama-3.1-8b-instant", // Using current available Llama 3.1 8B model
            messages,
            0.7,
            2048,
            false
        );
        
        return webClient.post()
            .uri("/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ChatCompletionResponse.class)
            .map(response -> {
                if (response.choices != null && !response.choices.isEmpty()) {
                    return response.choices.get(0).message.content;
                }
                return "I'm sorry, I couldn't generate a response at the moment. Please try again.";
            })
            .doOnSuccess(response -> logger.debug("Generated response: {}", response.substring(0, Math.min(100, response.length())) + "..."))
            .doOnError(error -> {
                if (error instanceof WebClientResponseException) {
                    WebClientResponseException webEx = (WebClientResponseException) error;
                    logger.error("Groq API error: {} - {}", webEx.getStatusCode(), webEx.getResponseBodyAsString());
                } else {
                    logger.error("Error generating response from Groq", error);
                }
            })
            .onErrorReturn("I'm experiencing technical difficulties. Please try again in a moment.");
    }
    
    public Mono<String> generateFinanceResponse(String userMessage, List<ChatMessage> conversationHistory) {
        // Add system prompt for finance research
        ChatMessage systemMessage = new ChatMessage(
            "system",
            "You are a **Financial Research Assistant**. Provide **short, well-formatted answers** with proper markdown formatting.\n\n" +
            "**Format Requirements:**\n" +
            "• Use **bold** for important metrics and numbers\n" +
            "• Use proper line breaks and spacing\n" +
            "• Keep responses **concise and focused**\n" +
            "• Use bullet points for key information\n" +
            "• Bold financial metrics like **P/E: 15.2**, **ROE: 18%**\n\n" +
            "**Response Style:**\n" +
            "• **Brief and direct** - no long explanations\n" +
            "• **2-3 key points maximum**\n" +
            "• **Bold numbers and percentages**\n" +
            "• Include a short disclaimer\n\n" +
            "Example format:\n" +
            "**HDFC Bank vs ICICI Bank:**\n\n" +
            "• **P/E Ratio:** HDFC **12.5x** vs ICICI **15.2x** ✓\n" +
            "• **Revenue Growth:** HDFC **+15%** vs ICICI **+10%** ✓\n" +
            "• **Verdict:** HDFC appears **undervalued**\n\n" +
            "*Verify with latest financial statements*"
        );
        
        // Combine system message with conversation history and new user message
        List<ChatMessage> messages = new java.util.ArrayList<>();
        messages.add(systemMessage);
        if (conversationHistory != null) {
            messages.addAll(conversationHistory);
        }
        messages.add(new ChatMessage("user", userMessage));
        
        return generateResponse(messages);
    }
    
    // DTOs for Groq API
    public static class ChatMessage {
        public String role;
        public String content;
        
        public ChatMessage() {}
        
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    
    public static class ChatCompletionRequest {
        public String model;
        public List<ChatMessage> messages;
        public double temperature;
        @JsonProperty("max_tokens")
        public int maxTokens;
        public boolean stream;
        
        public ChatCompletionRequest() {}
        
        public ChatCompletionRequest(String model, List<ChatMessage> messages, double temperature, int maxTokens, boolean stream) {
            this.model = model;
            this.messages = messages;
            this.temperature = temperature;
            this.maxTokens = maxTokens;
            this.stream = stream;
        }
    }
    
    public static class ChatCompletionResponse {
        public String id;
        public String object;
        public long created;
        public String model;
        public List<Choice> choices;
        public Usage usage;
        
        public static class Choice {
            public int index;
            public ChatMessage message;
            @JsonProperty("finish_reason")
            public String finishReason;
        }
        
        public static class Usage {
            @JsonProperty("prompt_tokens")
            public int promptTokens;
            @JsonProperty("completion_tokens")
            public int completionTokens;
            @JsonProperty("total_tokens")
            public int totalTokens;
        }
    }
}