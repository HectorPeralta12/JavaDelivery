package com.javadelivery;

public class CheeseTopping extends ToppingDecorator {

    private static final double CHEESE_PRICE = 1.00;

    public CheeseTopping(Item baseItem) {
        super(baseItem);
    }

    @Override
    public String getName() {
        return baseItem.getName() + " + Cheese";
    }

    @Override
    public double getPrice() {
        return baseItem.getPrice() + CHEESE_PRICE;
    }
}