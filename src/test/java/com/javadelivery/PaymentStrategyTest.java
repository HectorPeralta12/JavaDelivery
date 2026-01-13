package com.javadelivery;

/**
 * Test class for Phase 4 Strategy Pattern - Payments.
 * Tests all payment strategy implementations.
 */
public class PaymentStrategyTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=== Phase 4: Payment Strategy Tests ===\n");

        testCreditCardPayment();
        testCreditCardValidation();
        testPayPalPayment();
        testPayPalValidation();
        testCashOnDeliveryPayment();
        testCashOnDeliveryValidation();
        testPaymentResult();
        testStrategyInterchangeability();

        System.out.println("\n" + "=".repeat(40));
        System.out.printf("Results: %d/%d tests passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
            System.exit(1);
        }
    }

    private static void testCreditCardPayment() {
        System.out.println("Testing Credit Card Payment...");
        
        CreditCardPayment cc = new CreditCardPayment("1234-5678-9012-3456", "John Doe", "12/25", "123");
        
        assertTest("CC payment method name", "Credit Card".equals(cc.getPaymentMethodName()));
        assertTest("CC is valid", cc.isValid());
        assertTest("CC last four digits", "3456".equals(cc.getLastFourDigits()));
        
        PaymentResult result = cc.processPayment(25.99);
        assertTest("CC payment success", result.isSuccess());
        assertTest("CC transaction ID starts with CC-", result.getTransactionId().startsWith("CC-"));
        assertTest("CC payment amount correct", Math.abs(result.getAmount() - 25.99) < 0.01);
    }

    private static void testCreditCardValidation() {
        System.out.println("\nTesting Credit Card validation...");
        
        // Invalid card - null card number
        CreditCardPayment invalidCC1 = new CreditCardPayment(null, "John", "12/25", "123");
        assertTest("CC invalid with null card number", !invalidCC1.isValid());
        
        // Invalid card - empty holder name
        CreditCardPayment invalidCC2 = new CreditCardPayment("1234", "", "12/25", "123");
        assertTest("CC invalid with empty holder", !invalidCC2.isValid());
        
        // Invalid card - short CVV
        CreditCardPayment invalidCC3 = new CreditCardPayment("1234", "John", "12/25", "12");
        assertTest("CC invalid with short CVV", !invalidCC3.isValid());
        
        // Test payment with invalid card
        PaymentResult failResult = invalidCC1.processPayment(10.00);
        assertTest("CC invalid payment fails", !failResult.isSuccess());
        
        // Test negative amount
        CreditCardPayment validCC = new CreditCardPayment("1234", "John", "12/25", "123");
        PaymentResult negativeResult = validCC.processPayment(-5.00);
        assertTest("CC negative amount fails", !negativeResult.isSuccess());
    }

    private static void testPayPalPayment() {
        System.out.println("\nTesting PayPal Payment...");
        
        PayPalPayment pp = new PayPalPayment("user@email.com", "password123");
        
        assertTest("PP payment method name", "PayPal".equals(pp.getPaymentMethodName()));
        assertTest("PP is valid", pp.isValid());
        assertTest("PP email correct", "user@email.com".equals(pp.getEmail()));
        assertTest("PP not authenticated initially", !pp.isAuthenticated());
        
        PaymentResult result = pp.processPayment(15.50);
        assertTest("PP payment success", result.isSuccess());
        assertTest("PP now authenticated", pp.isAuthenticated());
        assertTest("PP transaction ID starts with PP-", result.getTransactionId().startsWith("PP-"));
    }

    private static void testPayPalValidation() {
        System.out.println("\nTesting PayPal validation...");
        
        // Invalid - no @ in email
        PayPalPayment invalidPP1 = new PayPalPayment("useratemail.com", "password");
        assertTest("PP invalid without @ in email", !invalidPP1.isValid());
        
        // Invalid - empty password
        PayPalPayment invalidPP2 = new PayPalPayment("user@email.com", "");
        assertTest("PP invalid with empty password", !invalidPP2.isValid());
        
        // Test payment with invalid credentials
        PaymentResult failResult = invalidPP1.processPayment(10.00);
        assertTest("PP invalid payment fails", !failResult.isSuccess());
    }

    private static void testCashOnDeliveryPayment() {
        System.out.println("\nTesting Cash on Delivery Payment...");
        
        CashOnDeliveryPayment cod = new CashOnDeliveryPayment("123 Main St, City", "+1234567890");
        
        assertTest("COD payment method name", "Cash on Delivery".equals(cod.getPaymentMethodName()));
        assertTest("COD is valid", cod.isValid());
        assertTest("COD address correct", "123 Main St, City".equals(cod.getDeliveryAddress()));
        assertTest("COD phone correct", "+1234567890".equals(cod.getContactPhone()));
        
        PaymentResult result = cod.processPayment(30.00);
        assertTest("COD payment success", result.isSuccess());
        assertTest("COD transaction ID starts with COD-", result.getTransactionId().startsWith("COD-"));
    }

    private static void testCashOnDeliveryValidation() {
        System.out.println("\nTesting Cash on Delivery validation...");
        
        // Invalid - empty address
        CashOnDeliveryPayment invalidCOD1 = new CashOnDeliveryPayment("", "+1234567890");
        assertTest("COD invalid with empty address", !invalidCOD1.isValid());
        
        // Invalid - null phone
        CashOnDeliveryPayment invalidCOD2 = new CashOnDeliveryPayment("123 Main St", null);
        assertTest("COD invalid with null phone", !invalidCOD2.isValid());
    }

    private static void testPaymentResult() {
        System.out.println("\nTesting PaymentResult...");
        
        PaymentResult success = PaymentResult.success("TX-123", 50.00);
        assertTest("Success result isSuccess", success.isSuccess());
        assertTest("Success has transaction ID", "TX-123".equals(success.getTransactionId()));
        assertTest("Success has amount", success.getAmount() == 50.00);
        
        PaymentResult failure = PaymentResult.failure("Card declined");
        assertTest("Failure result not success", !failure.isSuccess());
        assertTest("Failure has no transaction ID", failure.getTransactionId() == null);
        assertTest("Failure has message", failure.getMessage().contains("Card declined"));
    }

    private static void testStrategyInterchangeability() {
        System.out.println("\nTesting strategy interchangeability...");
        
        double orderTotal = 45.99;
        
        // All strategies can be used through the same interface
        PaymentStrategy[] strategies = {
            new CreditCardPayment("4111-1111-1111-1111", "Jane Doe", "06/27", "999"),
            new PayPalPayment("jane@email.com", "secret"),
            new CashOnDeliveryPayment("456 Oak Ave", "+9876543210")
        };
        
        for (PaymentStrategy strategy : strategies) {
            PaymentResult result = strategy.processPayment(orderTotal);
            assertTest(strategy.getPaymentMethodName() + " processes payment", result.isSuccess());
        }
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
