package com.javadelivery;

/**
 * Concrete observer that notifies customers about order status changes.
 * Simulates sending notifications via various channels (SMS, Email, Push).
 */
public class CustomerNotifier implements OrderObserver {

    private final String customerId;
    private final String customerName;
    private final String email;
    private final String phone;
    private int notificationCount;

    public CustomerNotifier(String customerId, String customerName, String email, String phone) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.notificationCount = 0;
    }

    @Override
    public void onOrderStatusChanged(String orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        notificationCount++;
        
        System.out.println("\n📱 NOTIFICATION for " + customerName);
        System.out.println("-".repeat(40));
        System.out.printf("Order #%s: %s → %s%n", orderId, oldStatus.getDisplayName(), newStatus.getDisplayName());
        System.out.println("📧 " + newStatus.getDescription());
        
        // Simulate different notification channels based on status
        switch (newStatus) {
            case PLACED, CONFIRMED -> sendEmail(orderId, newStatus);
            case PREPARING, READY -> sendPushNotification(orderId, newStatus);
            case OUT_FOR_DELIVERY -> {
                sendPushNotification(orderId, newStatus);
                sendSMS(orderId, newStatus);
            }
            case DELIVERED -> {
                sendEmail(orderId, newStatus);
                sendPushNotification(orderId, newStatus);
            }
            case CANCELLED -> {
                sendEmail(orderId, newStatus);
                sendSMS(orderId, newStatus);
            }
        }
    }

    private void sendEmail(String orderId, OrderStatus status) {
        System.out.printf("  → Email sent to %s%n", email);
    }

    private void sendSMS(String orderId, OrderStatus status) {
        System.out.printf("  → SMS sent to %s%n", phone);
    }

    private void sendPushNotification(String orderId, OrderStatus status) {
        System.out.println("  → Push notification sent");
    }

    @Override
    public String getObserverName() {
        return "CustomerNotifier-" + customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getNotificationCount() {
        return notificationCount;
    }
}
