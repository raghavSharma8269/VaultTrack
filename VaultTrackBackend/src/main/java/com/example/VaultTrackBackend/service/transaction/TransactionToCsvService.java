package com.example.VaultTrackBackend.service.transaction;

import com.example.VaultTrackBackend.dto.transaction.GetTransactionResponseDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Service
public class TransactionToCsvService {

    public byte[] convertToCsv(List<GetTransactionResponseDTO> transactions) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        // CSV header
        writer.println("Transaction ID,Transaction Name,Amount,Category,Type,Recurring Frequency,Recurring Date,Created At,Account ID,Account Name");

        // Write each transaction as a CSV row
        for (GetTransactionResponseDTO transaction : transactions) {
            writer.println(String.format("%s,%s,%.2f,%s,%s,%s,%s,%s,%s,%s",
                    transaction.getTransactionId(),
                    escapeCsv(transaction.getTransactionName()),
                    transaction.getAmount(),
                    transaction.getTransactionCategory(),
                    transaction.getTransactionType(),
                    transaction.getRecurringFrequency() != null ? transaction.getRecurringFrequency() : "",
                    transaction.getRecurringDate() != null ? transaction.getRecurringDate() : "",
                    transaction.getCreatedAt(),
                    transaction.getAccountId(),
                    escapeCsv(transaction.getAccountName())
            ));
        }

        writer.flush();
        writer.close();

        return outputStream.toByteArray();
    }

    // Escape commas and quotes in CSV values
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}