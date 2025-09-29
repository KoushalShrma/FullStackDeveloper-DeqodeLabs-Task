package com.deqode.financeresearch.service;

import com.deqode.financeresearch.entity.Source;
import com.deqode.financeresearch.entity.Thread;
import com.deqode.financeresearch.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(ResearchService.class);
    
    @Autowired
    private WebSearchService webSearchService;
    
    @Autowired
    private GroqService groqService;
    
    @Autowired
    private SourceRepository sourceRepository;
    
    public Mono<ResearchResult> conductFinanceResearch(String query, Thread thread, List<GroqService.ChatMessage> conversationHistory) {
        logger.info("Starting deep finance research for query: {}", query);
        
        return webSearchService.searchFinancialData(query)
            .map(searchResponse -> {
                // Save sources to database
                List<Source> sources = searchResponse.results.stream()
                    .map(result -> createSourceFromSearchResult(result, thread))
                    .collect(Collectors.toList());
                
                // Save sources
                sources = sourceRepository.saveAll(sources);
                
                // Build enhanced context for AI
                String enhancedContext = buildResearchContext(query, searchResponse);
                
                return new ResearchResult(enhancedContext, sources, searchResponse.results);
            })
            .flatMap(researchResult -> {
                // Generate AI response with research context
                GroqService.ChatMessage systemMessage = new GroqService.ChatMessage(
                    "system",
                    buildEnhancedSystemPrompt(researchResult.context, researchResult.searchResults)
                );
                
                // Add system message to conversation
                conversationHistory.add(0, systemMessage);
                
                return groqService.generateResponse(conversationHistory)
                    .map(aiResponse -> {
                        researchResult.aiResponse = aiResponse;
                        return researchResult;
                    });
            })
            .doOnSuccess(result -> logger.info("Research completed with {} sources", result.sources.size()))
            .doOnError(error -> logger.error("Research failed for query: " + query, error));
    }
    
    private Source createSourceFromSearchResult(WebSearchService.SearchResult result, Thread thread) {
        Source source = new Source();
        source.setThread(thread);
        source.setUrl(result.url);
        source.setTitle(result.title);
        source.setSnippet(result.content);
        source.setRelevanceScore(BigDecimal.valueOf(result.score));
        source.setIsRelevant(true);
        source.setDomain(extractDomain(result.url));
        return source;
    }
    
    private String extractDomain(String url) {
        try {
            java.net.URL urlObj = new java.net.URL(url);
            return urlObj.getHost();
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    private String buildResearchContext(String query, WebSearchService.SearchResponse searchResponse) {
        StringBuilder context = new StringBuilder();
        context.append("## Research Context for: ").append(query).append("\n\n");
        
        if (searchResponse.results.isEmpty()) {
            context.append("No recent web search results available. Using AI knowledge base.\n\n");
        } else {
            context.append("### Web Search Results:\n\n");
            int sourceNum = 1;
            for (WebSearchService.SearchResult result : searchResponse.results) {
                context.append(String.format("**[Source %d]** %s\n", sourceNum, result.title));
                context.append(String.format("URL: %s\n", result.url));
                context.append(String.format("Content: %s\n\n", result.content));
                sourceNum++;
            }
        }
        
        return context.toString();
    }
    
    private String buildEnhancedSystemPrompt(String researchContext, List<WebSearchService.SearchResult> searchResults) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are a **Deep Finance Research AI** with access to real-time financial data.\n\n");
        
        prompt.append("**RESEARCH CONTEXT:**\n");
        prompt.append(researchContext);
        prompt.append("\n");
        
        prompt.append("**RESPONSE FORMAT:**\n");
        prompt.append("## 🔍 Research Summary\n");
        prompt.append("Brief overview of findings from web research\n\n");
        
        prompt.append("## 📊 Financial Analysis\n");
        prompt.append("• **Key Metrics** in bold with specific numbers\n");
        prompt.append("• **Comparison Data** with peer analysis\n");
        prompt.append("• **Valuation Assessment** with clear verdict\n\n");
        
        prompt.append("## 📚 Sources Used\n");
        if (!searchResults.isEmpty()) {
            int sourceNum = 1;
            for (WebSearchService.SearchResult result : searchResults) {
                prompt.append(String.format("• **[%d]** %s - [Link](%s)\n", sourceNum, result.title, result.url));
                sourceNum++;
            }
        }
        prompt.append("\n");
        
        prompt.append("**INSTRUCTIONS:**\n");
        prompt.append("• Use **bold** for all financial metrics and numbers\n");
        prompt.append("• Reference sources as [1], [2], etc. in your analysis\n");
        prompt.append("• Keep response concise but comprehensive\n");
        prompt.append("• Include specific data points from the search results\n");
        prompt.append("• End with investment recommendation and disclaimer\n");
        
        return prompt.toString();
    }
    
    public static class ResearchResult {
        public String context;
        public List<Source> sources;
        public List<WebSearchService.SearchResult> searchResults;
        public String aiResponse;
        
        public ResearchResult(String context, List<Source> sources, List<WebSearchService.SearchResult> searchResults) {
            this.context = context;
            this.sources = sources;
            this.searchResults = searchResults;
        }
        
        // Constructor for direct responses without web search
        public ResearchResult(String aiResponse, List<Source> sources, List<WebSearchService.SearchResult> searchResults, String context) {
            this.aiResponse = aiResponse;
            this.sources = sources;
            this.searchResults = searchResults;
            this.context = context;
        }
    }
}