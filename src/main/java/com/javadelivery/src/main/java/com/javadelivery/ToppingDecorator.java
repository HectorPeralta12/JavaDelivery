package com.javadelivery;

public abstract class ToppingDecorator implements Item {
    protected final Item baseItem;

    protected ToppingDecorator(Item baseItem) {
        this.baseItem = baseItem;
    }

    @Override
    public String getName() {
        return baseItem.getName();
    }

    @Override
    public double getPrice() {
        return baseItem.getPrice();
    }
}