package com.javadelivery;

/**
 * Observer interface for receiving order status updates.
 * 
 * Design Pattern: Observer
 * - Defines a one-to-many dependency between objects
 * - When the subject (Order) changes state, all observers are notified
 * - Promotes loose coupling between the subject and its observers
 */
public interface OrderObserver {

    /**
     * Called when the order status changes.
     * 
     * @param orderId The ID of the order that changed
     * @param oldStatus The previous status
     * @param newStatus The new status
     */
    void onOrderStatusChanged(String orderId, OrderStatus oldStatus, OrderStatus newStatus);

    /**
     * Returns the name/identifier of this observer.
     */
    String getObserverName();
}
