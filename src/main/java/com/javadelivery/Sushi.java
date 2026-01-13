package com.javadelivery;

/**
 * Concrete implementation of Dish representing Sushi.
 */
public class Sushi extends Dish {

    public Sushi(String name, double price, DishCategory category, String description) {
        super(name, price, category, description);
    }

    @Override
    public String getDishType() {
        return "Sushi";
    }
}
