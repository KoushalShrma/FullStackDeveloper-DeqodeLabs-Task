package com.deqode.financeresearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FinanceResearchChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceResearchChatbotApplication.class, args);
    }
}