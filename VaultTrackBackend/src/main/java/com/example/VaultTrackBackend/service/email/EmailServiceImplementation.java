package com.example.VaultTrackBackend.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImplementation {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendBudgetAlertEmail(
            String to,
            String firstName,
            String accountName,
            BigDecimal budgetAmount,
            BigDecimal currentSpent,
            Integer alertThreshold,
            String periodType
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Budget Alert - " + accountName);
            helper.setFrom("noreply@vaulttrack.com");

            // Calculate values
            BigDecimal remainingAmount = budgetAmount.subtract(currentSpent);
            BigDecimal percentageUsed = currentSpent
                    .divide(budgetAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP);

            // Create Thymeleaf context
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("accountName", accountName);
            context.setVariable("budgetAmount", formatCurrency(budgetAmount));
            context.setVariable("currentSpent", formatCurrency(currentSpent));
            context.setVariable("remainingAmount", formatCurrency(remainingAmount));
            context.setVariable("alertThreshold", alertThreshold);
            context.setVariable("periodType", periodType.toLowerCase());
            context.setVariable("percentageUsed", percentageUsed.intValue());

            String htmlContent = templateEngine.process("budget-alert-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Budget alert email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send budget alert email to: {}", to, e);
            throw new RuntimeException("Failed to send budget alert email", e);
        }
    }

    /**
     * Send budget exceeded email when budget limit is surpassed
     */
    public void sendBudgetExceededEmail(
            String to,
            String firstName,
            String accountName,
            BigDecimal budgetAmount,
            BigDecimal currentSpent,
            String periodType
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Budget Exceeded - " + accountName);
            helper.setFrom("noreply@vaulttrack.com");

            // Calculate values
            BigDecimal overAmount = currentSpent.subtract(budgetAmount);
            BigDecimal percentageUsed = currentSpent
                    .divide(budgetAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP);

            // Create Thymeleaf context
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("accountName", accountName);
            context.setVariable("budgetAmount", formatCurrency(budgetAmount));
            context.setVariable("currentSpent", formatCurrency(currentSpent));
            context.setVariable("overAmount", formatCurrency(overAmount));
            context.setVariable("periodType", periodType.toLowerCase());
            context.setVariable("percentageUsed", percentageUsed.intValue());

            String htmlContent = templateEngine.process("budget-exceeded-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Budget exceeded email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send budget exceeded email to: {}", to, e);
            throw new RuntimeException("Failed to send budget exceeded email", e);
        }
    }


    private String formatCurrency(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toString();
    }
}