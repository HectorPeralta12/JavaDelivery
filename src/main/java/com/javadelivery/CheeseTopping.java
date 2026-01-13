package com.javadelivery;

/**
 * Decorator that adds cheese to a dish.
 * Adds 1.00€ to the base price.
 */
public class CheeseTopping extends ToppingDecorator {

    private static final double CHEESE_PRICE = 1.00;
    private static final String TOPPING_NAME = "Cheese";

    public CheeseTopping(Item baseItem) {
        super(baseItem);
    }

    @Override
    public String getName() {
        return baseItem.getName() + " + " + TOPPING_NAME;
    }

    @Override
    public double getPrice() {
        return baseItem.getPrice() + CHEESE_PRICE;
    }

    @Override
    public String getToppingName() {
        return TOPPING_NAME;
    }

    @Override
    public double getToppingPrice() {
        return CHEESE_PRICE;
    }
}