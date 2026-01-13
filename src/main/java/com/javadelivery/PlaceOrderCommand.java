package com.javadelivery;

/**
 * Command to place an order.
 * Validates the order and transitions it to CONFIRMED status.
 */
public class PlaceOrderCommand implements OrderCommand {

    private final Order order;
    private final PaymentStrategy paymentStrategy;
    private PaymentResult paymentResult;
    private boolean executed;

    public PlaceOrderCommand(Order order, PaymentStrategy paymentStrategy) {
        this.order = order;
        this.paymentStrategy = paymentStrategy;
        this.executed = false;
    }

    @Override
    public boolean execute() {
        if (executed) {
            System.out.println("Order already placed.");
            return false;
        }

        // Validate minimum order amount
        AppConfig config = AppConfig.getInstance();
        if (order.getBaseTotal() < config.getMinOrderAmount()) {
            System.out.printf("Order total (%.2f%s) is below minimum (%.2f%s)%n",
                order.getBaseTotal(), config.getCurrency(),
                config.getMinOrderAmount(), config.getCurrency());
            return false;
        }

        // Validate payment method
        if (paymentStrategy == null || !paymentStrategy.isValid()) {
            System.out.println("Invalid payment method.");
            return false;
        }

        // Calculate total with delivery fee
        double totalWithDelivery = order.getBaseTotal() + config.getDeliveryFee();
        
        System.out.println("\n" + "=".repeat(40));
        System.out.println("PLACING ORDER: " + order.getOrderId());
        System.out.printf("Subtotal: %.2f%s%n", order.getBaseTotal(), config.getCurrency());
        System.out.printf("Delivery Fee: %.2f%s%n", config.getDeliveryFee(), config.getCurrency());
        System.out.printf("Total: %.2f%s%n", totalWithDelivery, config.getCurrency());
        System.out.println("=".repeat(40));

        // Process payment
        paymentResult = paymentStrategy.processPayment(totalWithDelivery);
        
        if (!paymentResult.isSuccess()) {
            System.out.println("Payment failed: " + paymentResult.getMessage());
            return false;
        }

        // Update order status
        boolean statusUpdated = order.updateStatus(OrderStatus.CONFIRMED);
        if (!statusUpdated) {
            System.out.println("Failed to update order status.");
            return false;
        }

        executed = true;
        System.out.println("\n✓ Order placed successfully!");
        System.out.println("Transaction ID: " + paymentResult.getTransactionId());
        
        return true;
    }

    @Override
    public boolean undo() {
        if (!executed) {
            System.out.println("Cannot undo - order was not placed.");
            return false;
        }

        // Can only cancel if not yet preparing
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            order.updateStatus(OrderStatus.CANCELLED);
            executed = false;
            System.out.println("Order cancelled. Refund initiated for transaction: " + 
                paymentResult.getTransactionId());
            return true;
        } else {
            System.out.println("Cannot cancel order in status: " + order.getStatus().getDisplayName());
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Place Order #" + order.getOrderId();
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    public boolean isExecuted() {
        return executed;
    }
}
