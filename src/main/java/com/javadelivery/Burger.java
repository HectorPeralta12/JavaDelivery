package com.javadelivery;

/**
 * Concrete implementation of Dish representing a Burger.
 */
public class Burger extends Dish {

    public Burger(String name, double price, DishCategory category, String description) {
        super(name, price, category, description);
    }

    @Override
    public String getDishType() {
        return "Burger";
    }
}
