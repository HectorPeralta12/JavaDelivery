package com.javadelivery;

/**
 * Result of a payment processing attempt.
 * Contains success status, transaction ID, and any error message.
 */
public class PaymentResult {
    private final boolean success;
    private final String transactionId;
    private final String message;
    private final double amount;

    private PaymentResult(boolean success, String transactionId, String message, double amount) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
        this.amount = amount;
    }

    /**
     * Creates a successful payment result.
     */
    public static PaymentResult success(String transactionId, double amount) {
        return new PaymentResult(true, transactionId, "Payment successful", amount);
    }

    /**
     * Creates a failed payment result.
     */
    public static PaymentResult failure(String errorMessage) {
        return new PaymentResult(false, null, errorMessage, 0);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMessage() {
        return message;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        if (success) {
            return String.format("Payment SUCCESS - Transaction: %s - Amount: %.2f€", 
                transactionId, amount);
        } else {
            return String.format("Payment FAILED - %s", message);
        }
    }
}
