package com.javadelivery;

import java.util.UUID;

/**
 * Credit card payment strategy implementation.
 * Simulates credit card payment processing.
 */
public class CreditCardPayment implements PaymentStrategy {

    private final String cardNumber;
    private final String cardHolderName;
    private final String expiryDate;
    private final String cvv;

    public CreditCardPayment(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        if (!isValid()) {
            return PaymentResult.failure("Invalid credit card details");
        }

        if (amount <= 0) {
            return PaymentResult.failure("Invalid payment amount");
        }

        // Simulate payment processing
        String transactionId = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        System.out.printf("Processing credit card payment of %.2f€...%n", amount);
        System.out.printf("Card: **** **** **** %s%n", getLastFourDigits());
        System.out.printf("Holder: %s%n", cardHolderName);
        
        return PaymentResult.success(transactionId, amount);
    }

    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }

    @Override
    public boolean isValid() {
        return cardNumber != null && !cardNumber.isBlank() 
            && cardHolderName != null && !cardHolderName.isBlank()
            && expiryDate != null && !expiryDate.isBlank()
            && cvv != null && cvv.length() >= 3;
    }

    /**
     * Returns the last 4 digits of the card number for display.
     */
    public String getLastFourDigits() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleaned = cardNumber.replaceAll("[^0-9]", "");
        return cleaned.substring(Math.max(0, cleaned.length() - 4));
    }
}
