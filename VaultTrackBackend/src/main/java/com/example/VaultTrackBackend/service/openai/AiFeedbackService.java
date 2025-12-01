package com.example.VaultTrackBackend.service.openai;

import com.example.VaultTrackBackend.dto.openai.AiFeedbackRequestDTO;
import com.example.VaultTrackBackend.dto.transaction.GetTransactionResponseDTO;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.service.transaction.GetTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AiFeedbackService {
    private final GetTransactionService getTransactionService;
    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    public AiFeedbackService(
            GetTransactionService getTransactionService,
            RestTemplate restTemplate
    ) {
        this.getTransactionService = getTransactionService;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> execute(
            AiFeedbackRequestDTO aiFeedbackRequestDTO,
            LocalDateTime start,
            LocalDateTime end,
            TransactionCategory transactionCategory,
            TransactionType transactionType,
            String transactionName,
            UUID accountId
    ) {
        List<GetTransactionResponseDTO> transactions = getTransactionService.execute(
                start,
                end,
                transactionCategory,
                transactionType,
                transactionName,
                accountId
        ).getBody();

        if (transactions == null || transactions.isEmpty()) {
            return ResponseEntity.ok("No transactions found for the given criteria.");
        }

        String systemContext = """
        You are a professional financial advisor with expertise in personal finance management.
        Analyze the provided transactions carefully and provide:
        1. Clear, actionable insights about spending patterns
        2. Specific recommendations for improvement
        3. Budget optimization suggestions
        4. Answers to any user questions based on the transaction data
        
        Be concise, friendly, and practical in your advice.
        Your first and most important task is to answer the user's specific question based on the transaction data provided before providing any additional insights or recommendations.
        """;

        String transactionContext = """
        Here are the user's transactions:
        
        %s
        
        """.formatted(transactions);

        String userQuestion = """
        User's Question: %s
        
        Please provide a detailed analysis and answer based on the transactions above.
        """.formatted(aiFeedbackRequestDTO.getUserPrompt());

        String fullPrompt = systemContext + transactionContext + userQuestion;

        String aiResponse = callOpenAiApi(fullPrompt);

        return ResponseEntity.ok(aiResponse);
    }

    private String callOpenAiApi(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);


        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            return (String) message.get("content");

        } catch (Exception e) {
            log.error("Error calling OpenAI API: ", e);
            return "Error getting AI response: " + e.getMessage();
        }
    }
}
