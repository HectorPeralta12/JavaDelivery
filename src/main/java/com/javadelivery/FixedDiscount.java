package com.javadelivery;

public class FixedDiscount implements DiscountStrategy {

    private final double amount;

    public FixedDiscount(double amount) {
        this.amount = amount;
    }

    @Override
    public double applyDiscount(double total) {
        return Math.max(0, total - amount);
    }
}