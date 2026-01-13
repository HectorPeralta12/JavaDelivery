package com.javadelivery;

/**
 * Test class for Phase 6 Singleton and Command Patterns.
 */
public class SingletonCommandTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=== Phase 6: Singleton & Command Pattern Tests ===\n");

        // Reset singleton for clean test state
        AppConfig.resetInstance();

        testSingletonPattern();
        testAppConfigValues();
        testPlaceOrderCommand();
        testPlaceOrderValidation();
        testCancelOrderCommand();
        testCommandUndo();

        System.out.println("\n" + "=".repeat(40));
        System.out.printf("Results: %d/%d tests passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
            System.exit(1);
        }
    }

    private static void testSingletonPattern() {
        System.out.println("Testing Singleton Pattern...");
        
        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();
        
        assertTest("Singleton returns same instance", config1 == config2);
        assertTest("Instance is not null", config1 != null);
        
        // Modify through one reference, check through another
        config1.setAppName("TestApp");
        assertTest("Changes visible through both references", 
            "TestApp".equals(config2.getAppName()));
        
        // Reset for other tests
        config1.setAppName("JavaDelivery");
    }

    private static void testAppConfigValues() {
        System.out.println("\nTesting AppConfig values...");
        
        AppConfig config = AppConfig.getInstance();
        
        assertTest("Default app name", "JavaDelivery".equals(config.getAppName()));
        assertTest("Default version", "1.0.0".equals(config.getVersion()));
        assertTest("Default currency", "€".equals(config.getCurrency()));
        assertTest("Default delivery fee", config.getDeliveryFee() == 2.50);
        assertTest("Default min order", config.getMinOrderAmount() == 10.00);
        assertTest("Default max toppings", config.getMaxToppingsPerItem() == 5);
        assertTest("Default debug mode", !config.isDebugMode());
        
        // Test setters
        config.setDeliveryFee(3.00);
        assertTest("Delivery fee updated", config.getDeliveryFee() == 3.00);
        config.setDeliveryFee(2.50); // Reset
    }

    private static void testPlaceOrderCommand() {
        System.out.println("\nTesting PlaceOrderCommand...");
        
        // Create order with items
        Order order = new Order.Builder()
                .setOrderId("CMD-001")
                .setCustomerName("Test Customer")
                .addItem(DishFactory.createPizza("Margherita", 12.00, DishCategory.VEG, "Test"))
                .build();
        
        PaymentStrategy payment = new CreditCardPayment("4111111111111111", "Test User", "12/25", "123");
        PlaceOrderCommand placeCmd = new PlaceOrderCommand(order, payment);
        
        assertTest("Command description", placeCmd.getDescription().contains("CMD-001"));
        assertTest("Not executed initially", !placeCmd.isExecuted());
        
        boolean result = placeCmd.execute();
        
        assertTest("Order placed successfully", result);
        assertTest("Command marked as executed", placeCmd.isExecuted());
        assertTest("Order status is CONFIRMED", order.getStatus() == OrderStatus.CONFIRMED);
        assertTest("Payment result exists", placeCmd.getPaymentResult() != null);
        assertTest("Payment was successful", placeCmd.getPaymentResult().isSuccess());
    }

    private static void testPlaceOrderValidation() {
        System.out.println("\nTesting PlaceOrderCommand validation...");
        
        AppConfig config = AppConfig.getInstance();
        
        // Order below minimum amount
        Order smallOrder = new Order.Builder()
                .setOrderId("CMD-002")
                .setCustomerName("Test")
                .addItem(DishFactory.createPizza("Small", 5.00, DishCategory.VEG, "Too cheap"))
                .build();
        
        PaymentStrategy payment = new CreditCardPayment("4111111111111111", "Test", "12/25", "123");
        PlaceOrderCommand cmd1 = new PlaceOrderCommand(smallOrder, payment);
        
        boolean result1 = cmd1.execute();
        assertTest("Order below minimum rejected", !result1);
        
        // Invalid payment method
        Order validOrder = new Order.Builder()
                .setOrderId("CMD-003")
                .setCustomerName("Test")
                .addItem(DishFactory.createPizza("Large", 15.00, DishCategory.VEG, "Good"))
                .build();
        
        PaymentStrategy invalidPayment = new CreditCardPayment(null, "Test", "12/25", "123");
        PlaceOrderCommand cmd2 = new PlaceOrderCommand(validOrder, invalidPayment);
        
        boolean result2 = cmd2.execute();
        assertTest("Invalid payment rejected", !result2);
        
        // Double execution
        Order order3 = new Order.Builder()
                .setOrderId("CMD-004")
                .setCustomerName("Test")
                .addItem(DishFactory.createPizza("Test", 15.00, DishCategory.VEG, "Test"))
                .build();
        
        PlaceOrderCommand cmd3 = new PlaceOrderCommand(order3, payment);
        cmd3.execute();
        boolean doubleExec = cmd3.execute();
        assertTest("Double execution rejected", !doubleExec);
    }

    private static void testCancelOrderCommand() {
        System.out.println("\nTesting CancelOrderCommand...");
        
        // Create and place an order first
        Order order = new Order.Builder()
                .setOrderId("CMD-005")
                .setCustomerName("Cancel Test")
                .addItem(DishFactory.createBurger("Burger", 12.00, DishCategory.NON_VEG, "Test"))
                .build();
        
        PaymentStrategy payment = new CreditCardPayment("4111111111111111", "Test", "12/25", "123");
        PlaceOrderCommand placeCmd = new PlaceOrderCommand(order, payment);
        placeCmd.execute();
        
        assertTest("Order is CONFIRMED before cancel", order.getStatus() == OrderStatus.CONFIRMED);
        
        CancelOrderCommand cancelCmd = new CancelOrderCommand(order, "Changed my mind");
        assertTest("Cancel description includes reason", cancelCmd.getDescription().contains("Changed my mind"));
        
        boolean cancelResult = cancelCmd.execute();
        assertTest("Cancel executed successfully", cancelResult);
        assertTest("Order status is CANCELLED", order.getStatus() == OrderStatus.CANCELLED);
        
        // Try to cancel already cancelled order
        CancelOrderCommand cancelCmd2 = new CancelOrderCommand(order);
        boolean doubleCancel = cancelCmd2.execute();
        assertTest("Double cancel rejected", !doubleCancel);
    }

    private static void testCommandUndo() {
        System.out.println("\nTesting Command undo...");
        
        // Create and place an order
        Order order = new Order.Builder()
                .setOrderId("CMD-006")
                .setCustomerName("Undo Test")
                .addItem(DishFactory.createSushi("Salmon", 15.00, DishCategory.NON_VEG, "Test"))
                .build();
        
        PaymentStrategy payment = new CreditCardPayment("4111111111111111", "Test", "12/25", "123");
        PlaceOrderCommand placeCmd = new PlaceOrderCommand(order, payment);
        
        // Cannot undo before execution
        boolean undoBeforeExec = placeCmd.undo();
        assertTest("Cannot undo unexecuted command", !undoBeforeExec);
        
        placeCmd.execute();
        
        // Undo the place order (cancels the order)
        boolean undoResult = placeCmd.undo();
        assertTest("Undo successful", undoResult);
        assertTest("Order cancelled after undo", order.getStatus() == OrderStatus.CANCELLED);
        
        // Cancel command undo (not allowed)
        Order order2 = new Order.Builder()
                .setOrderId("CMD-007")
                .setCustomerName("Undo Test 2")
                .addItem(DishFactory.createPizza("Test", 15.00, DishCategory.VEG, "Test"))
                .build();
        
        order2.updateStatus(OrderStatus.CONFIRMED);
        CancelOrderCommand cancelCmd = new CancelOrderCommand(order2);
        cancelCmd.execute();
        
        boolean cancelUndo = cancelCmd.undo();
        assertTest("Cancel cannot be undone", !cancelUndo);
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
}
