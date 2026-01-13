package com.javadelivery;

/**
 * Factory class for creating Dish instances.
 * Implements the Factory Method pattern to create different types of dishes
 * based on a type identifier.
 * 
 * Design Pattern: Factory Method
 * - Encapsulates object creation logic
 * - Allows creation of objects without specifying exact class
 * - Easy to extend with new dish types
 */
public class DishFactory {

    /**
     * Creates a dish based on the specified type.
     * 
     * @param type The type of dish ("pizza", "burger", "sushi")
     * @param name The name of the dish
     * @param price The price of the dish
     * @param category The dietary category (VEG, NON_VEG, VEGAN)
     * @param description A brief description of the dish
     * @return A new Dish instance of the specified type
     * @throws IllegalArgumentException if the dish type is unknown
     */
    public static Dish createDish(String type, String name, double price, 
                                   DishCategory category, String description) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Dish type cannot be null or empty");
        }
        
        return switch (type.toLowerCase().trim()) {
            case "pizza" -> new Pizza(name, price, category, description);
            case "burger" -> new Burger(name, price, category, description);
            case "sushi" -> new Sushi(name, price, category, description);
            default -> throw new IllegalArgumentException("Unknown dish type: " + type);
        };
    }

    /**
     * Creates a Pizza dish.
     */
    public static Pizza createPizza(String name, double price, 
                                     DishCategory category, String description) {
        return new Pizza(name, price, category, description);
    }

    /**
     * Creates a Burger dish.
     */
    public static Burger createBurger(String name, double price, 
                                       DishCategory category, String description) {
        return new Burger(name, price, category, description);
    }

    /**
     * Creates a Sushi dish.
     */
    public static Sushi createSushi(String name, double price, 
                                     DishCategory category, String description) {
        return new Sushi(name, price, category, description);
    }
}
