package com.javadelivery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a customer order in the food delivery system.
 * Uses Builder pattern for construction and Observer pattern for status notifications.
 * 
 * Design Patterns:
 * - Builder: Fluent construction of complex orders
 * - Observer: Notifies subscribers when order status changes
 */
public class Order {
    private final String orderId;
    private final String customerName;
    private final List<Item> items;
    private final double baseTotal;
    private OrderStatus status;
    private final List<OrderObserver> observers;

    private Order(Builder builder) {
        this.orderId = builder.orderId;
        this.customerName = builder.customerName;
        this.items = new ArrayList<>(builder.items);
        this.baseTotal = calculateBaseTotal();
        this.status = OrderStatus.PLACED;
        this.observers = new ArrayList<>();
    }

    private double calculateBaseTotal() {
        return items.stream()
                .mapToDouble(Item::getPrice)
                .sum();
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getBaseTotal() {
        return baseTotal;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public double applyDiscount(DiscountStrategy strategy) {
        return strategy.applyDiscount(baseTotal);
    }

    // Observer pattern methods
    
    /**
     * Registers an observer to receive status updates.
     */
    public void addObserver(OrderObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Removes an observer from receiving updates.
     */
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    /**
     * Returns the number of registered observers.
     */
    public int getObserverCount() {
        return observers.size();
    }

    /**
     * Updates the order status and notifies all observers.
     * 
     * @param newStatus The new status to set
     * @return true if status was updated, false if transition not allowed
     */
    public boolean updateStatus(OrderStatus newStatus) {
        if (newStatus == null || newStatus == status) {
            return false;
        }
        
        if (!status.canTransitionTo(newStatus)) {
            System.out.printf("Cannot transition from %s to %s%n", 
                status.getDisplayName(), newStatus.getDisplayName());
            return false;
        }
        
        OrderStatus oldStatus = this.status;
        this.status = newStatus;
        notifyObservers(oldStatus, newStatus);
        return true;
    }

    /**
     * Notifies all registered observers of a status change.
     */
    private void notifyObservers(OrderStatus oldStatus, OrderStatus newStatus) {
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChanged(orderId, oldStatus, newStatus);
        }
    }

    @Override
    public String toString() {
        return String.format("Order #%s - %s - %.2f€ - Status: %s", 
            orderId, customerName, baseTotal, status.getDisplayName());
    }

    /**
     * Builder for creating Order instances.
     */
    public static class Builder {
        private String orderId;
        private String customerName;
        private List<Item> items = new ArrayList<>();

        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder addItem(Item item) {
            if (item != null) {
                this.items.add(item);
            }
            return this;
        }

        public Builder addItems(List<Item> items) {
            if (items != null) {
                this.items.addAll(items);
            }
            return this;
        }

        public Order build() {
            if (orderId == null || orderId.isBlank()) {
                throw new IllegalStateException("Order ID is required");
            }
            if (customerName == null || customerName.isBlank()) {
                throw new IllegalStateException("Customer name is required");
            }
            return new Order(this);
        }
    }
}