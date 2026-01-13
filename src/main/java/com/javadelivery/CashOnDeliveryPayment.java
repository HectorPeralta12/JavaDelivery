package com.javadelivery;

import java.util.UUID;

/**
 * Cash on Delivery payment strategy implementation.
 * Payment is collected when the order is delivered.
 */
public class CashOnDeliveryPayment implements PaymentStrategy {

    private final String deliveryAddress;
    private final String contactPhone;

    public CashOnDeliveryPayment(String deliveryAddress, String contactPhone) {
        this.deliveryAddress = deliveryAddress;
        this.contactPhone = contactPhone;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        if (!isValid()) {
            return PaymentResult.failure("Invalid delivery details for cash on delivery");
        }

        if (amount <= 0) {
            return PaymentResult.failure("Invalid payment amount");
        }

        // Cash on delivery creates a pending payment
        String transactionId = "COD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        System.out.printf("Cash on Delivery order created for %.2f€%n", amount);
        System.out.printf("Delivery Address: %s%n", deliveryAddress);
        System.out.printf("Contact Phone: %s%n", contactPhone);
        System.out.println("Payment will be collected upon delivery.");
        
        return PaymentResult.success(transactionId, amount);
    }

    @Override
    public String getPaymentMethodName() {
        return "Cash on Delivery";
    }

    @Override
    public boolean isValid() {
        return deliveryAddress != null && !deliveryAddress.isBlank() 
            && contactPhone != null && !contactPhone.isBlank();
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }
}
