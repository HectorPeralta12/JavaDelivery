package com.javadelivery;

/**
 * Decorator that adds jalapeños to a dish.
 * Adds 0.75€ to the base price.
 * Note: This topping is VEGAN.
 */
public class JalapenoTopping extends ToppingDecorator {

    private static final double JALAPENO_PRICE = 0.75;
    private static final String TOPPING_NAME = "Jalapeños";

    public JalapenoTopping(Item baseItem) {
        super(baseItem);
    }

    @Override
    public String getName() {
        return baseItem.getName() + " + " + TOPPING_NAME;
    }

    @Override
    public double getPrice() {
        return baseItem.getPrice() + JALAPENO_PRICE;
    }

    @Override
    public String getToppingName() {
        return TOPPING_NAME;
    }

    @Override
    public double getToppingPrice() {
        return JALAPENO_PRICE;
    }
}
