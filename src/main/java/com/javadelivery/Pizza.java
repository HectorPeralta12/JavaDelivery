package com.javadelivery;

/**
 * Concrete implementation of Dish representing a Pizza.
 */
public class Pizza extends Dish {

    public Pizza(String name, double price, DishCategory category, String description) {
        super(name, price, category, description);
    }

    @Override
    public String getDishType() {
        return "Pizza";
    }
}
