package com.javadelivery;

import java.util.UUID;

/**
 * PayPal payment strategy implementation.
 * Simulates PayPal payment processing.
 */
public class PayPalPayment implements PaymentStrategy {

    private final String email;
    private final String password;
    private boolean authenticated;

    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
        this.authenticated = false;
    }

    /**
     * Simulates PayPal authentication.
     */
    public boolean authenticate() {
        if (email != null && !email.isBlank() && password != null && !password.isBlank()) {
            this.authenticated = true;
            System.out.println("PayPal: Authenticated as " + email);
            return true;
        }
        return false;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        if (!isValid()) {
            return PaymentResult.failure("Invalid PayPal credentials");
        }

        if (!authenticated) {
            if (!authenticate()) {
                return PaymentResult.failure("PayPal authentication failed");
            }
        }

        if (amount <= 0) {
            return PaymentResult.failure("Invalid payment amount");
        }

        // Simulate payment processing
        String transactionId = "PP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        System.out.printf("Processing PayPal payment of %.2f€...%n", amount);
        System.out.printf("PayPal Account: %s%n", email);
        
        return PaymentResult.success(transactionId, amount);
    }

    @Override
    public String getPaymentMethodName() {
        return "PayPal";
    }

    @Override
    public boolean isValid() {
        return email != null && !email.isBlank() 
            && email.contains("@")
            && password != null && !password.isBlank();
    }

    public String getEmail() {
        return email;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
