package com.javadelivery;

/**
 * Test class for Phase 5 Observer Pattern.
 * Tests order status notifications and observer management.
 */
public class ObserverPatternTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=== Phase 5: Observer Pattern Tests ===\n");

        testOrderStatus();
        testOrderStatusTransitions();
        testObserverRegistration();
        testStatusChangeNotifications();
        testMultipleObservers();
        testCustomerNotifier();

        System.out.println("\n" + "=".repeat(40));
        System.out.printf("Results: %d/%d tests passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
            System.exit(1);
        }
    }

    private static void testOrderStatus() {
        System.out.println("Testing OrderStatus enum...");
        
        assertTest("PLACED has display name", "Order Placed".equals(OrderStatus.PLACED.getDisplayName()));
        assertTest("DELIVERED has description", OrderStatus.DELIVERED.getDescription().contains("delivered"));
        assertTest("CANCELLED exists", OrderStatus.CANCELLED != null);
        assertTest("Total statuses count", OrderStatus.values().length == 7);
    }

    private static void testOrderStatusTransitions() {
        System.out.println("\nTesting status transitions...");
        
        // Valid transitions
        assertTest("PLACED -> CONFIRMED allowed", OrderStatus.PLACED.canTransitionTo(OrderStatus.CONFIRMED));
        assertTest("CONFIRMED -> PREPARING allowed", OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.PREPARING));
        assertTest("PREPARING -> READY allowed", OrderStatus.PREPARING.canTransitionTo(OrderStatus.READY));
        assertTest("READY -> OUT_FOR_DELIVERY allowed", OrderStatus.READY.canTransitionTo(OrderStatus.OUT_FOR_DELIVERY));
        assertTest("OUT_FOR_DELIVERY -> DELIVERED allowed", OrderStatus.OUT_FOR_DELIVERY.canTransitionTo(OrderStatus.DELIVERED));
        
        // Cancel allowed from most states
        assertTest("PLACED -> CANCELLED allowed", OrderStatus.PLACED.canTransitionTo(OrderStatus.CANCELLED));
        assertTest("PREPARING -> CANCELLED allowed", OrderStatus.PREPARING.canTransitionTo(OrderStatus.CANCELLED));
        
        // Invalid transitions
        assertTest("PLACED -> DELIVERED not allowed", !OrderStatus.PLACED.canTransitionTo(OrderStatus.DELIVERED));
        assertTest("DELIVERED -> PLACED not allowed", !OrderStatus.DELIVERED.canTransitionTo(OrderStatus.PLACED));
        assertTest("CANCELLED -> CONFIRMED not allowed", !OrderStatus.CANCELLED.canTransitionTo(OrderStatus.CONFIRMED));
    }

    private static void testObserverRegistration() {
        System.out.println("\nTesting observer registration...");
        
        Order order = new Order.Builder()
                .setOrderId("OBS-001")
                .setCustomerName("Test User")
                .build();
        
        assertTest("Initial observer count is 0", order.getObserverCount() == 0);
        
        TestObserver observer1 = new TestObserver("Observer1");
        order.addObserver(observer1);
        assertTest("Observer count after add", order.getObserverCount() == 1);
        
        // Adding same observer again shouldn't increase count
        order.addObserver(observer1);
        assertTest("No duplicate observers", order.getObserverCount() == 1);
        
        // Adding null shouldn't cause issues
        order.addObserver(null);
        assertTest("Null observer ignored", order.getObserverCount() == 1);
        
        order.removeObserver(observer1);
        assertTest("Observer removed", order.getObserverCount() == 0);
    }

    private static void testStatusChangeNotifications() {
        System.out.println("\nTesting status change notifications...");
        
        Order order = new Order.Builder()
                .setOrderId("OBS-002")
                .setCustomerName("John Doe")
                .build();
        
        TestObserver observer = new TestObserver("TestObserver");
        order.addObserver(observer);
        
        assertTest("Initial status is PLACED", order.getStatus() == OrderStatus.PLACED);
        assertTest("Initial notification count", observer.notificationCount == 0);
        
        // Update to CONFIRMED
        boolean updated = order.updateStatus(OrderStatus.CONFIRMED);
        assertTest("Update to CONFIRMED succeeded", updated);
        assertTest("Status is now CONFIRMED", order.getStatus() == OrderStatus.CONFIRMED);
        assertTest("Observer notified once", observer.notificationCount == 1);
        assertTest("Observer received correct old status", observer.lastOldStatus == OrderStatus.PLACED);
        assertTest("Observer received correct new status", observer.lastNewStatus == OrderStatus.CONFIRMED);
        
        // Invalid transition
        boolean invalidUpdate = order.updateStatus(OrderStatus.DELIVERED);
        assertTest("Invalid transition rejected", !invalidUpdate);
        assertTest("Status unchanged after invalid", order.getStatus() == OrderStatus.CONFIRMED);
        assertTest("No notification for invalid transition", observer.notificationCount == 1);
    }

    private static void testMultipleObservers() {
        System.out.println("\nTesting multiple observers...");
        
        Order order = new Order.Builder()
                .setOrderId("OBS-003")
                .setCustomerName("Jane Doe")
                .build();
        
        TestObserver observer1 = new TestObserver("First");
        TestObserver observer2 = new TestObserver("Second");
        TestObserver observer3 = new TestObserver("Third");
        
        order.addObserver(observer1);
        order.addObserver(observer2);
        order.addObserver(observer3);
        
        assertTest("Three observers registered", order.getObserverCount() == 3);
        
        order.updateStatus(OrderStatus.CONFIRMED);
        
        assertTest("Observer1 notified", observer1.notificationCount == 1);
        assertTest("Observer2 notified", observer2.notificationCount == 1);
        assertTest("Observer3 notified", observer3.notificationCount == 1);
    }

    private static void testCustomerNotifier() {
        System.out.println("\nTesting CustomerNotifier...");
        
        CustomerNotifier notifier = new CustomerNotifier("C001", "Alice", "alice@email.com", "+123456");
        
        assertTest("Notifier name contains ID", notifier.getObserverName().contains("C001"));
        assertTest("Customer name correct", "Alice".equals(notifier.getCustomerName()));
        assertTest("Initial notification count", notifier.getNotificationCount() == 0);
        
        Order order = new Order.Builder()
                .setOrderId("ORD-CUST-001")
                .setCustomerName("Alice")
                .build();
        
        order.addObserver(notifier);
        
        // Simulate full order lifecycle
        order.updateStatus(OrderStatus.CONFIRMED);
        order.updateStatus(OrderStatus.PREPARING);
        order.updateStatus(OrderStatus.READY);
        order.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.updateStatus(OrderStatus.DELIVERED);
        
        assertTest("Customer received 5 notifications", notifier.getNotificationCount() == 5);
        assertTest("Order status is DELIVERED", order.getStatus() == OrderStatus.DELIVERED);
    }

    private static void assertTest(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  ✓ " + testName);
        } else {
            System.out.println("  ✗ " + testName + " - FAILED");
        }
    }

    /**
     * Simple test observer to track notifications.
     */
    private static class TestObserver implements OrderObserver {
        private final String name;
        int notificationCount = 0;
        OrderStatus lastOldStatus;
        OrderStatus lastNewStatus;

        TestObserver(String name) {
            this.name = name;
        }

        @Override
        public void onOrderStatusChanged(String orderId, OrderStatus oldStatus, OrderStatus newStatus) {
            notificationCount++;
            lastOldStatus = oldStatus;
            lastNewStatus = newStatus;
        }

        @Override
        public String getObserverName() {
            return "TestObserver-" + name;
        }
    }
}
