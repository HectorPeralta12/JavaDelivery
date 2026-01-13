package com.javadelivery;

/**
 * Enum representing the possible states of an order.
 * Used by the Observer pattern to notify customers of status changes.
 */
public enum OrderStatus {
    PLACED("Order Placed", "Your order has been received"),
    CONFIRMED("Order Confirmed", "Restaurant has confirmed your order"),
    PREPARING("Preparing", "Your food is being prepared"),
    READY("Ready for Pickup", "Your order is ready"),
    OUT_FOR_DELIVERY("Out for Delivery", "Your order is on its way"),
    DELIVERED("Delivered", "Your order has been delivered"),
    CANCELLED("Cancelled", "Your order has been cancelled");

    private final String displayName;
    private final String description;

    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks if the order can transition to the given status.
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PLACED -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == PREPARING || newStatus == CANCELLED;
            case PREPARING -> newStatus == READY || newStatus == CANCELLED;
            case READY -> newStatus == OUT_FOR_DELIVERY || newStatus == CANCELLED;
            case OUT_FOR_DELIVERY -> newStatus == DELIVERED || newStatus == CANCELLED;
            case DELIVERED, CANCELLED -> false; // Terminal states
        };
    }
}
