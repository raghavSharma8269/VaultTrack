package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.openai.AiFeedbackRequestDTO;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.service.openai.AiFeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/ai")
public class OpenAIController {
    private final AiFeedbackService aiFeedbackService;

    public OpenAIController(
            AiFeedbackService aiFeedbackService
    ) {
        this.aiFeedbackService = aiFeedbackService;
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> getAiFeedback(
            @RequestBody AiFeedbackRequestDTO aiFeedbackRequestDTO,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end,
            @RequestParam(required = false) TransactionCategory transactionCategory,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) String transactionName,
            @RequestParam(required = false) UUID accountId
    ) {
        return aiFeedbackService.execute(
                aiFeedbackRequestDTO,
                start,
                end,
                transactionCategory,
                transactionType,
                transactionName,
                accountId
        );
    }
}
