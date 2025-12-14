package com.javadelivery;

public class PercentageDiscount implements DiscountStrategy {

    private final double percentage; // e.g. 0.10 = 10%

    public PercentageDiscount(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double applyDiscount(double total) {
        return total - (total * percentage);
    }
}