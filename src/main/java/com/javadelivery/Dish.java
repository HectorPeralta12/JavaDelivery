package com.javadelivery;

/**
 * Abstract base class for all dishes in the food delivery system.
 * Implements the Item interface and provides common properties for all dish types.
 */
public abstract class Dish implements Item {
    protected final String name;
    protected final double price;
    protected final DishCategory category;
    protected final String description;

    protected Dish(String name, double price, DishCategory category, String description) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    public DishCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the type of dish (e.g., "Pizza", "Burger", "Sushi").
     * Subclasses must implement this to identify their type.
     */
    public abstract String getDishType();

    @Override
    public String toString() {
        return String.format("%s - %s (%.2f€) [%s]", 
            name, description, price, category.getDisplayName());
    }
}
