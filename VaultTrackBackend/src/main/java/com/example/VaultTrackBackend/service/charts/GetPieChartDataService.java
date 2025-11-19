package com.example.VaultTrackBackend.service.charts;

import com.example.VaultTrackBackend.dto.charts.PieChartDataResponseDTO;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GetPieChartDataService {

    private final GetCurrentUserService getCurrentUserService;
    private final TransactionRepository transactionRepository;

    public GetPieChartDataService(
            GetCurrentUserService getCurrentUserService,
            TransactionRepository transactionRepository
    ) {
        this.getCurrentUserService = getCurrentUserService;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<PieChartDataResponseDTO> execute
            (
            LocalDateTime start,
            LocalDateTime end,
            TransactionCategory transactionCategory,
            TransactionType transactionType,
            String transactionName,
            UUID accountId
    ) {

        User currentUser = getCurrentUserService.execute();

        List<Transaction> transactions;


        if (start != null && end != null) {
            transactions = transactionRepository.findTransactionsByFiltersWithDate(
                    currentUser.getUserId(),
                    transactionName,
                    transactionType,
                    transactionCategory,
                    accountId,
                    start,
                    end
            );
        } else {
            transactions = transactionRepository.findTransactionsByFiltersNoDate(
                    currentUser.getUserId(),
                    transactionName,
                    transactionType,
                    transactionCategory,
                    accountId
            );
        }


        // Filter out INCOME transactions
        transactions = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
                .toList();

        // Calculate total amount
        BigDecimal totalAmount = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate per category
        BigDecimal foodAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.FOOD)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal utilitiesAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.UTILITIES)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal entertainmentAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.ENTERTAINMENT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal transportationAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.TRANSPORTATION)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal healthcareAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.HEALTHCARE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal educationAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.EDUCATION)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal groceriesAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.GROCERIES)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal rentAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.RENT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal salaryAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.SALARY)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal investmentsAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.INVESTMENTS)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal miscellaneousAmount = transactions.stream()
                .filter(t -> t.getTransactionCategory() == TransactionCategory.MISCELLANEOUS)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Build response
        PieChartDataResponseDTO response = new PieChartDataResponseDTO();
        response.setTotalAmount(totalAmount);
        response.setFoodAmount(foodAmount);
        response.setUtilitiesAmount(utilitiesAmount);
        response.setEntertainmentAmount(entertainmentAmount);
        response.setTransportationAmount(transportationAmount);
        response.setHealthcareAmount(healthcareAmount);
        response.setEducationAmount(educationAmount);
        response.setGroceriesAmount(groceriesAmount);
        response.setRentAmount(rentAmount);
        response.setSalaryAmount(salaryAmount);
        response.setInvestmentsAmount(investmentsAmount);
        response.setMiscellaneousAmount(miscellaneousAmount);

        return ResponseEntity.ok(response);



    }
}
