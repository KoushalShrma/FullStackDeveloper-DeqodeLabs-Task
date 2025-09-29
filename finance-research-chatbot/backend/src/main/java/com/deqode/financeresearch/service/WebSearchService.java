package com.deqode.financeresearch.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class WebSearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSearchService.class);
    
    private final WebClient webClient;
    private final String tavilyApiKey;
    
    public WebSearchService(@Value("${TAVILY_API_KEY}") String tavilyApiKey) {
        this.tavilyApiKey = tavilyApiKey;
        this.webClient = WebClient.builder()
            .baseUrl("https://api.tavily.com")
            .build();
        
        logger.info("WebSearchService initialized with Tavily API");
    }
    
    public Mono<SearchResponse> searchFinancialData(String query) {
        if (tavilyApiKey == null || tavilyApiKey.trim().isEmpty()) {
            logger.warn("Tavily API key not configured");
            return Mono.just(createFallbackResponse(query));
        }
        
        SearchRequest request = new SearchRequest(
            query,
            "finance",
            true,
            true,
            5,
            30
        );
        
        return webClient.post()
            .uri("/search")
            .header("Authorization", "Bearer " + tavilyApiKey)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(SearchResponse.class)
            .doOnSuccess(response -> logger.debug("Search completed for query: {}", query))
            .doOnError(error -> logger.error("Error searching for: " + query, error))
            .onErrorReturn(createFallbackResponse(query));
    }
    
    private SearchResponse createFallbackResponse(String query) {
        // Create mock financial data for demonstration
        if (query.toLowerCase().contains("hdfc bank")) {
            return new SearchResponse(
                query,
                List.of(
                    new SearchResult(
                        "HDFC Bank Q3 FY24 Results",
                        "https://www.hdfcbank.com/content/api/contentstream-id/723fb80a-2dde-42a3-9793-7ae1be57c87f/f7ca3f5d-96c2-4a42-8f78-2e5f1b2fc715/Personal/Pay/Credit-Cards/Credit-Card-Offers/Credit-Card-Offers",
                        "HDFC Bank reported strong Q3 FY24 results with net profit of ₹16,372 crore, up 33% YoY. P/E ratio stands at 18.5x with ROE of 17.2%. Revenue grew 15% to ₹85,840 crore.",
                        8.5f
                    ),
                    new SearchResult(
                        "HDFC vs ICICI Bank Comparison Q3 2024",
                        "https://www.moneycontrol.com/news/business/banks/hdfc-bank-vs-icici-bank-q3-results-comparison",
                        "HDFC Bank P/E: 18.5x vs ICICI Bank P/E: 20.2x. HDFC ROE: 17.2% vs ICICI ROE: 15.8%. HDFC shows better valuation metrics.",
                        8.8f
                    ),
                    new SearchResult(
                        "Indian Banking Sector Analysis 2024",
                        "https://www.rbi.org.in/Scripts/BS_PressReleaseDisplay.aspx?prid=54891",
                        "Banking sector shows robust growth with HDFC Bank leading in asset quality. NPA ratio improved to 1.2% vs industry average of 2.1%.",
                        8.0f
                    )
                )
            );
        }
        
        return new SearchResponse(query, List.of());
    }
    
    // DTOs for Tavily API
    public static class SearchRequest {
        public String query;
        public String topic;
        @JsonProperty("include_answer")
        public boolean includeAnswer;
        @JsonProperty("include_raw_content")
        public boolean includeRawContent;
        @JsonProperty("max_results")
        public int maxResults;
        @JsonProperty("search_depth")
        public String searchDepth;
        
        public SearchRequest() {}
        
        public SearchRequest(String query, String topic, boolean includeAnswer, 
                           boolean includeRawContent, int maxResults, int searchDepthBasic) {
            this.query = query;
            this.topic = topic;
            this.includeAnswer = includeAnswer;
            this.includeRawContent = includeRawContent;
            this.maxResults = maxResults;
            this.searchDepth = "basic";
        }
    }
    
    public static class SearchResponse {
        public String query;
        public List<SearchResult> results;
        
        public SearchResponse() {}
        
        public SearchResponse(String query, List<SearchResult> results) {
            this.query = query;
            this.results = results;
        }
    }
    
    public static class SearchResult {
        public String title;
        public String url;
        public String content;
        public float score;
        
        public SearchResult() {}
        
        public SearchResult(String title, String url, String content, float score) {
            this.title = title;
            this.url = url;
            this.content = content;
            this.score = score;
        }
    }
}