package com.javadelivery;

import com.javadelivery.simulation.OrderManager;
import com.javadelivery.tui.TuiApp;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the JavaDelivery application.
 * Provides a menu to launch the TUI or run a simple demo.
 */
public class Main {

    public static void main(String[] args) {
        AppConfig config = AppConfig.getInstance();
        
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   🍕 " + config.getAppName() + " v" + config.getVersion() + "                  ║");
        System.out.println("║   Food Delivery Management System        ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();

        // Check if running with --tui flag
        if (args.length > 0 && args[0].equals("--tui")) {
            launchTui();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Select an option:");
            System.out.println("  [1] Launch TUI Dashboard");
            System.out.println("  [2] Quick Demo (Console)");
            System.out.println("  [3] Run Tests");
            System.out.println("  [4] Show Config");
            System.out.println("  [Q] Quit");
            System.out.print("\n> ");

            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "1" -> launchTui();
                case "2" -> runQuickDemo();
                case "3" -> runTests();
                case "4" -> config.displayConfig();
                case "q", "quit", "exit" -> running = false;
                default -> System.out.println("Invalid option. Please try again.\n");
            }
        }

        System.out.println("\nGoodbye! 👋\n");
        scanner.close();
    }

    private static void launchTui() {
        System.out.println("\nLaunching TUI Dashboard...");
        System.out.println("(Press Q to exit the dashboard)\n");
        
        try {
            OrderManager orderManager = new OrderManager();
            orderManager.createSampleOrders();
            orderManager.startSimulation();

            TuiApp app = new TuiApp(orderManager);
            app.start();
            
        } catch (IOException e) {
            System.err.println("Error launching TUI: " + e.getMessage());
            System.err.println("Make sure you're running in a proper terminal.");
        }
    }

    private static void runQuickDemo() {
        System.out.println("\n=== Quick Demo ===\n");
        
        // Create restaurants
        List<Restaurant> restaurants = RestaurantFactory.getAllRestaurants();
        if (restaurants.isEmpty()) {
            System.out.println("No restaurants available.");
            return;
        }
        
        Restaurant pizzeria = restaurants.get(0);
        System.out.println("🏪 Using: " + pizzeria.getName());
        
        if (pizzeria.getMenu().isEmpty()) {
            System.out.println("Restaurant has no menu items.");
            return;
        }
        
        // Create an order with toppings (Decorator pattern)
        Dish dish = pizzeria.getMenu().get(0);
        Item pizzaWithBacon = new BaconTopping(dish);
        Item pizzaDeluxe = new CheeseTopping(pizzaWithBacon);
        
        System.out.println("\n📦 Creating order...");
        Order order = new Order.Builder()
                .setOrderId("DEMO-001")
                .setCustomerName("Demo Customer")
                .addItem(pizzaDeluxe)
                .build();
        
        // Add observer
        order.addObserver(new CustomerNotifier("C001", "Demo Customer", "demo@test.com", "+1234567890"));
        
        System.out.println(order);
        System.out.println("\nItems:");
        for (Item item : order.getItems()) {
            System.out.println("  - " + item.getName() + " - " + 
                    String.format("%.2f€", item.getPrice()));
        }
        
        // Process payment (Strategy pattern)
        System.out.println("\n💳 Processing payment...");
        PaymentStrategy payment = new CreditCardPayment(
                "4111111111111111", "Demo User", "12/25", "123");
        PaymentResult result = payment.processPayment(order.getBaseTotal());
        System.out.println("Payment: " + (result.isSuccess() ? "✅ Success" : "❌ Failed"));
        
        // Update status (Observer pattern)
        System.out.println("\n📍 Order status updates:");
        order.updateStatus(OrderStatus.CONFIRMED);
        order.updateStatus(OrderStatus.PREPARING);
        order.updateStatus(OrderStatus.READY);
        order.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.updateStatus(OrderStatus.DELIVERED);
        
        System.out.println("\n=== Demo Complete ===\n");
    }

    private static void runTests() {
        System.out.println("\nTo run tests, execute:");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"com.javadelivery.DomainModelsTest\"");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"com.javadelivery.FactoryPatternTest\"");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"com.javadelivery.DecoratorPatternTest\"");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"com.javadelivery.PaymentStrategyTest\"");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"com.javadelivery.ObserverPatternTest\"");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"com.javadelivery.SingletonCommandTest\"");
        System.out.println();
    }
}
