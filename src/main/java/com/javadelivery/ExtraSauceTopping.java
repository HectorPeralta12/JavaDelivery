package com.javadelivery;

/**
 * Decorator that adds extra sauce to a dish.
 * Adds 0.50€ to the base price.
 */
public class ExtraSauceTopping extends ToppingDecorator {

    private static final double SAUCE_PRICE = 0.50;
    private static final String TOPPING_NAME = "Extra Sauce";

    public ExtraSauceTopping(Item baseItem) {
        super(baseItem);
    }

    @Override
    public String getName() {
        return baseItem.getName() + " + " + TOPPING_NAME;
    }

    @Override
    public double getPrice() {
        return baseItem.getPrice() + SAUCE_PRICE;
    }

    @Override
    public String getToppingName() {
        return TOPPING_NAME;
    }

    @Override
    public double getToppingPrice() {
        return SAUCE_PRICE;
    }
}
