package com.javadelivery;

/**
 * Singleton class for application-wide configuration.
 * 
 * Design Pattern: Singleton
 * - Ensures only one instance of the configuration exists
 * - Provides global access point
 * - Thread-safe using double-checked locking
 * 
 * Addresses requirement NF2: "only one instance of application configuration
 * that stores global settings, e.g., app name, version"
 */
public class AppConfig {

    private static volatile AppConfig instance;

    // Application settings
    private String appName;
    private String version;
    private String currency;
    private double deliveryFee;
    private double minOrderAmount;
    private int maxToppingsPerItem;
    private boolean debugMode;

    /**
     * Private constructor prevents external instantiation.
     */
    private AppConfig() {
        // Default configuration values
        this.appName = "JavaDelivery";
        this.version = "1.0.0";
        this.currency = "€";
        this.deliveryFee = 2.50;
        this.minOrderAmount = 10.00;
        this.maxToppingsPerItem = 5;
        this.debugMode = false;
    }

    /**
     * Returns the singleton instance of AppConfig.
     * Uses double-checked locking for thread safety.
     */
    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    /**
     * Resets the singleton instance (for testing purposes only).
     */
    public static void resetInstance() {
        synchronized (AppConfig.class) {
            instance = null;
        }
    }

    // Getters and Setters
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public double getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public int getMaxToppingsPerItem() {
        return maxToppingsPerItem;
    }

    public void setMaxToppingsPerItem(int maxToppingsPerItem) {
        this.maxToppingsPerItem = maxToppingsPerItem;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Displays current configuration.
     */
    public void displayConfig() {
        System.out.println("\n=== " + appName + " Configuration ===");
        System.out.println("Version: " + version);
        System.out.println("Currency: " + currency);
        System.out.printf("Delivery Fee: %.2f%s%n", deliveryFee, currency);
        System.out.printf("Minimum Order: %.2f%s%n", minOrderAmount, currency);
        System.out.println("Max Toppings per Item: " + maxToppingsPerItem);
        System.out.println("Debug Mode: " + debugMode);
    }

    @Override
    public String toString() {
        return String.format("%s v%s", appName, version);
    }
}
