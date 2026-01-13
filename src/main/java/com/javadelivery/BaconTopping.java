package com.javadelivery;

/**
 * Decorator that adds bacon to a dish.
 * Adds 2.00€ to the base price.
 * Note: This topping is NON_VEG.
 */
public class BaconTopping extends ToppingDecorator {

    private static final double BACON_PRICE = 2.00;
    private static final String TOPPING_NAME = "Bacon";

    public BaconTopping(Item baseItem) {
        super(baseItem);
    }

    @Override
    public String getName() {
        return baseItem.getName() + " + " + TOPPING_NAME;
    }

    @Override
    public double getPrice() {
        return baseItem.getPrice() + BACON_PRICE;
    }

    @Override
    public String getToppingName() {
        return TOPPING_NAME;
    }

    @Override
    public double getToppingPrice() {
        return BACON_PRICE;
    }
}
