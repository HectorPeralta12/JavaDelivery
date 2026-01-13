package com.javadelivery;

/**
 * Command to cancel an existing order.
 * Can only cancel orders that haven't been delivered.
 */
public class CancelOrderCommand implements OrderCommand {

    private final Order order;
    private final String reason;
    private OrderStatus previousStatus;
    private boolean executed;

    public CancelOrderCommand(Order order, String reason) {
        this.order = order;
        this.reason = reason;
        this.executed = false;
    }

    public CancelOrderCommand(Order order) {
        this(order, "Customer requested cancellation");
    }

    @Override
    public boolean execute() {
        if (executed) {
            System.out.println("Order already cancelled.");
            return false;
        }

        OrderStatus currentStatus = order.getStatus();
        
        // Check if order can be cancelled
        if (currentStatus == OrderStatus.DELIVERED) {
            System.out.println("Cannot cancel - order already delivered.");
            return false;
        }
        
        if (currentStatus == OrderStatus.CANCELLED) {
            System.out.println("Order is already cancelled.");
            return false;
        }

        previousStatus = currentStatus;
        
        System.out.println("\n" + "=".repeat(40));
        System.out.println("CANCELLING ORDER: " + order.getOrderId());
        System.out.println("Reason: " + reason);
        System.out.println("Current Status: " + currentStatus.getDisplayName());
        System.out.println("=".repeat(40));

        boolean statusUpdated = order.updateStatus(OrderStatus.CANCELLED);
        
        if (statusUpdated) {
            executed = true;
            System.out.println("\n✓ Order cancelled successfully.");
            
            // If payment was made, indicate refund
            if (previousStatus != OrderStatus.PLACED) {
                System.out.println("Refund will be processed within 3-5 business days.");
            }
            return true;
        } else {
            System.out.println("Failed to cancel order.");
            return false;
        }
    }

    @Override
    public boolean undo() {
        if (!executed) {
            System.out.println("Cannot undo - order was not cancelled by this command.");
            return false;
        }

        // Cancellation cannot be easily undone in real systems
        // This is a simplified implementation for demonstration
        System.out.println("Order cancellation cannot be undone. Please place a new order.");
        return false;
    }

    @Override
    public String getDescription() {
        return "Cancel Order #" + order.getOrderId() + " - " + reason;
    }

    public String getReason() {
        return reason;
    }

    public boolean isExecuted() {
        return executed;
    }
}
