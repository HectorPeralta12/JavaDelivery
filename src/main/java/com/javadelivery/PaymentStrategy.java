package com.javadelivery;

/**
 * Interface for payment processing strategies.
 * 
 * Design Pattern: Strategy
 * - Defines a family of algorithms (payment methods)
 * - Makes them interchangeable
 * - Lets the algorithm vary independently from clients that use it
 * 
 * Usage:
 * <pre>
 * PaymentStrategy payment = new CreditCardPayment("1234-5678-9012-3456", "John Doe");
 * PaymentResult result = payment.processPayment(25.99);
 * </pre>
 */
public interface PaymentStrategy {

    /**
     * Processes a payment for the given amount.
     * 
     * @param amount The amount to charge
     * @return PaymentResult indicating success or failure with details
     */
    PaymentResult processPayment(double amount);

    /**
     * Returns the name of this payment method.
     */
    String getPaymentMethodName();

    /**
     * Validates if this payment method is ready to process payments.
     */
    boolean isValid();
}
